package com.devmarquinhos.priowl.dto;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String isAdmin
) {
}
