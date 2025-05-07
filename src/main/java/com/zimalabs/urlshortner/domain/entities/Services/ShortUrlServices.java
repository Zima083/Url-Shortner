package com.zimalabs.urlshortner.domain.entities.Services;

import com.zimalabs.urlshortner.ApplicationProperties;
import com.zimalabs.urlshortner.domain.entities.ShortUrl;
import com.zimalabs.urlshortner.domain.entities.models.CreateShortUrlCmd;
import com.zimalabs.urlshortner.domain.entities.models.PagedResult;
import com.zimalabs.urlshortner.domain.entities.models.ShortUrlDto;
import com.zimalabs.urlshortner.domain.entities.repositories.ShortUrlRepositiory;
import com.zimalabs.urlshortner.domain.entities.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.*;
@Service
@Transactional(readOnly = true)
public class ShortUrlServices {
    private final ShortUrlRepositiory shortUrlRepositiory;
    private final EntityMapper entityMapper;
    private final ApplicationProperties properties;
    private final UserRepository userRepository;

    public ShortUrlServices(ShortUrlRepositiory shortUrlRepositiory, EntityMapper entityMapper, ApplicationProperties properties, UserRepository userRepository) {
        this.shortUrlRepositiory = shortUrlRepositiory;
        this.entityMapper = entityMapper;
        this.properties = properties;
        this.userRepository = userRepository;
    }

    public PagedResult<ShortUrlDto> findAllPublicShortUrls(int pageNo, int pageSize) {
        pageNo = pageNo > 1? pageNo - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ShortUrlDto> shortUrlDtoPage = shortUrlRepositiory.findPublicShortUrl(pageable)
                .map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlDtoPage);
    }
@Transactional
   public ShortUrlDto CreateShortUrl(CreateShortUrlCmd cmd) {
       if (properties.validareOriginalUrl()){
           boolean urlExists = UrlExistenceValidator.isUrlExists(cmd.originalUrl());
           if (!urlExists){
            throw new RuntimeException("Invalid original url"+cmd.originalUrl());
           }
       }
        var shortKey =generateRandomShortKey();
       var shortUrl = new ShortUrl();
       shortUrl.setOriginalUrl(cmd.originalUrl());
       shortUrl.setShortKey(shortKey);
      if (cmd.userId()==null) {
          shortUrl.setCreatedBy(null);
          shortUrl.setIsPrivate(false);
          shortUrl.setExpiresAt(Instant.now().plus(properties.defaultExpiryInDays(), DAYS));
      } else {
          shortUrl.setCreatedBy(userRepository.findById(cmd.userId()).orElseThrow());
          shortUrl.setIsPrivate(cmd.isPrivate()==null?false:cmd.isPrivate());
           shortUrl.setExpiresAt(cmd.expirationInDays()!=null ? Instant.now().plus(cmd.expirationInDays(),DAYS):null);
      }
      shortUrl.setClickCount(0L);
        shortUrl.setCreatedAt(Instant.now());
        shortUrlRepositiory.save(shortUrl);
       return entityMapper.toShortUrlDto(shortUrl);
   }
    private String generateUniqueShortKey(){
        String shortKey;
        do {
            shortKey = generateRandomShortKey();
        }while (shortUrlRepositiory.existsByShortKey(shortKey));
    return shortKey;
    }
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_KEY_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomShortKey() {
        StringBuilder sb = new StringBuilder(SHORT_KEY_LENGTH);
        for (int i = 0; i < SHORT_KEY_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
@Transactional
    public Optional<ShortUrlDto> accessShortUrl(String shortKey, Long userId) {
        Optional<ShortUrl> shortUrlOptional = shortUrlRepositiory.findByShortKey(shortKey);
        if (shortUrlOptional.isEmpty()){
            return Optional.empty();
        }
        ShortUrl shortUrl = shortUrlOptional.get();
        if(shortUrl.getExpiresAt()!=null && shortUrl.getExpiresAt().isBefore(Instant.now())) {
         return Optional.empty();
        }
    if (shortUrl.getIsPrivate()!=null && shortUrl.getCreatedBy()!=null
            && !Objects.equals(shortUrl.getCreatedBy().getId(), userId)){
        return Optional.empty();
    }
        shortUrl.setClickCount(shortUrl.getClickCount()+1);
        shortUrlRepositiory.save(shortUrl);
    return shortUrlOptional.map(entityMapper::toShortUrlDto);
    }
    private Pageable getPageable(int page, int size) {
        page = page > 1 ? page - 1: 0;
        return PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
    }
    @Transactional
    public void deleteUserShortUrls(List<Long> ids, Long userId) {
        if (ids != null && !ids.isEmpty() && userId != null) {
            shortUrlRepositiory.deleteByIdInAndCreatedById(ids, userId);
        }
    }
    public PagedResult<ShortUrlDto> getUserShortUrls(Long currentUserId, int page, int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        var shortUrlsPage =  shortUrlRepositiory.findAllShortUrls(pageable).map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlsPage);
    }

    public PagedResult<ShortUrlDto> findAllShortUrls(int page, int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        var shortUrlsPage =  shortUrlRepositiory.findAllShortUrls(pageable).map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlsPage);
    }
}
