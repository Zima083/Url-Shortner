package com.zimalabs.urlshortner.web.controller.dtos;

import com.zimalabs.urlshortner.domain.entities.models.Role;

public record CreateUserCmd(
        String email,
        String password,
        String name,
        Role role
) {
}
