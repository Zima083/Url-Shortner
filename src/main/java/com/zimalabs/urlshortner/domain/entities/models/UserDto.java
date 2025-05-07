package com.zimalabs.urlshortner.domain.entities.models;

import java.io.Serializable;

/**
 * DTO for {@link com.zimalabs.urlshortner.domain.entities.User}
 */
public record UserDto(Long id, String name) implements Serializable {
}