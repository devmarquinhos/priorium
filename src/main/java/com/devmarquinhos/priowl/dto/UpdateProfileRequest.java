package com.devmarquinhos.priowl.dto;

public record UpdateProfileRequest(
        String username,
        String email
) {
}
