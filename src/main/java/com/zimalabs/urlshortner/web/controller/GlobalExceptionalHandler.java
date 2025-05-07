package com.zimalabs.urlshortner.web.controller;

import com.zimalabs.urlshortner.domain.entities.exceptions.ShortUrlNotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.slf4j.Logger;

@ControllerAdvice
public class GlobalExceptionalHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionalHandler.class);
    @ExceptionHandler(ShortUrlNotFoundException.class)
String handleShortUrlNotFoundException(ShortUrlNotFoundException ex){
        log.error("Short Url Not found: {}"+ex.getMessage());
    return "error/404";
}
    @ExceptionHandler(Exception.class)
    String handleException(Exception ex){
        log.error("Unchecked exception: {}"+ex.getMessage(),ex);
        return "error/500";
    }
}
