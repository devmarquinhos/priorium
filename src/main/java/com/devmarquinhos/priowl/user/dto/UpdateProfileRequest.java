package com.devmarquinhos.priowl.user.dto;

public record UpdateProfileRequest(
        String username,
        String email
) {
}
