package com.devmarquinhos.priowl.dto;

public record UpdatePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
