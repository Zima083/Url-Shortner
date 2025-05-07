package com.zimalabs.urlshortner.domain.entities.exceptions;

public class ShortUrlNotFoundException extends RuntimeException{
    public ShortUrlNotFoundException(String message) {super(message);}
}
