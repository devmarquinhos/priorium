package com.devmarquinhos.priowl.user.dto;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String isAdmin
) {
}
