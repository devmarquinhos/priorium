package com.devmarquinhos.priowl.user.dto;

public record UpdatePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
