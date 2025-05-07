package com.zimalabs.urlshortner.domain.entities.models;

import jakarta.validation.constraints.Min;

public record CreateShortUrlCmd(String originalUrl,
                                Boolean isPrivate,
                                Integer expirationInDays,
                                Long userId
                                ) {
}
