package com.zimalabs.urlshortner.domain.entities.repositories;

import com.zimalabs.urlshortner.domain.entities.ShortUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ShortUrlRepositiory extends JpaRepository<ShortUrl, Long> {
//  @Query("select su from ShortUrl su left join fetch su.createdBy where su.isPrivate = false order by su.createdAt desc ")
  //@EntityGraph(attributePaths = {"createdBy"})
  @Query("select su from ShortUrl su left join fetch su.createdBy where su.isPrivate = false ")
  Page<ShortUrl> findPublicShortUrl(Pageable pageable);

  boolean existsByShortKey(String shortKey);

  Optional<ShortUrl> findByShortKey(String shortKey);

  Page<ShortUrl> findByCreatedById(Long userId, Pageable pageable);

  void deleteByIdInAndCreatedById(List<Long> ids, Long userId);
  @Query("select u from ShortUrl u left join fetch u.createdBy")
   Page<ShortUrl> findAllShortUrls(Pageable pageable);
}
