package com.zimalabs.urlshortner.web.controller;

import com.zimalabs.urlshortner.ApplicationProperties;
import com.zimalabs.urlshortner.domain.entities.Services.ShortUrlServices;
import com.zimalabs.urlshortner.domain.entities.User;
import com.zimalabs.urlshortner.domain.entities.exceptions.ShortUrlNotFoundException;
import com.zimalabs.urlshortner.domain.entities.models.CreateShortUrlCmd;
import com.zimalabs.urlshortner.domain.entities.models.PagedResult;
import com.zimalabs.urlshortner.domain.entities.models.ShortUrlDto;
import com.zimalabs.urlshortner.web.controller.dtos.CreateShortUrlForm;
import com.zimalabs.urlshortner.web.controller.dtos.RegisterUserRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final ApplicationProperties properties;
    private final ShortUrlServices shortUrlServices;
    private final SecurityUtils securityUtils;

    public HomeController(ApplicationProperties properties, ShortUrlServices shortUrlServices, SecurityUtils securityUtils) {
        this.properties = properties;
        this.shortUrlServices = shortUrlServices;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "1") Integer page,
                           Model model
                       ) {
        model.addAttribute("paginationUrl","/");
        this.addShortUrlsDataToModel(model,page);
        model.addAttribute("createShortUrlForm", new CreateShortUrlForm("",false,null));
        return "index";
    }
        private void addShortUrlsDataToModel(Model model,int pageNo){
        PagedResult<ShortUrlDto> shortUrls = shortUrlServices.findAllPublicShortUrls(pageNo, properties.pageSize());
        model.addAttribute("shortUrls", shortUrls);
        model.addAttribute("baseUrls", properties.baseUrl());
        }
    @PostMapping("/short-urls")
    String createShortUrl(@ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm form,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (bindingResult.hasErrors()) {
            this.addShortUrlsDataToModel(model,1);
        return "index";
        }
        try {
            Long userId = securityUtils.getCurrentUserId();
            CreateShortUrlCmd cmd = new CreateShortUrlCmd(form.originalUrl(),
                    form.isPrivate(),
                    form.expirationInDays(),
                    userId
                    );
       var shortUrldto =     shortUrlServices.CreateShortUrl(cmd);
        redirectAttributes.addFlashAttribute("successMessage", "ShortUrl created successfully"+
                properties.baseUrl()+"/s/"+shortUrldto.shortKey());
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create url");
        }
        return "redirect:/";
    }
    @GetMapping("/s/{shortKey}")
    String redirectToOriginalUrl(@PathVariable String shortKey){
        Long userId = securityUtils.getCurrentUserId();
        Optional<ShortUrlDto> shortUrlDtoOptional = shortUrlServices.accessShortUrl(shortKey,userId);
    if (shortUrlDtoOptional.isEmpty()){
        throw new ShortUrlNotFoundException("Invalid Short key"+shortKey);
    }
    ShortUrlDto shortUrlDto = shortUrlDtoOptional.get();
    return "redirect:"+shortUrlDto.originalUrl();
    }
    @GetMapping("/login")
    String Loginform(){
        return "login";
    }
    @GetMapping("/my-urls")
    public String showUserUrls(
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        var currentUserId = securityUtils.getCurrentUserId();
        PagedResult<ShortUrlDto> myUrls =
                shortUrlServices.getUserShortUrls(currentUserId, page, properties.pageSize());
        model.addAttribute("shortUrls", myUrls);
        model.addAttribute("baseUrl", properties.baseUrl());
        model.addAttribute("paginationUrl", "/my-urls.html");
        return "my-urls.html";
    }

    @PostMapping("/delete-urls")
    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    public String deleteUrls(
            @RequestParam(value = "ids", required = false) List<Long> ids,
            RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "No URLs selected for deletion");
            return "redirect:/my-urls";
        }
        try {
            var currentUserId = securityUtils.getCurrentUserId();
            shortUrlServices.deleteUserShortUrls(ids, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Selected URLs have been deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting URLs: " + e.getMessage());
        }
        return "redirect:/my-urls";
    }
}
