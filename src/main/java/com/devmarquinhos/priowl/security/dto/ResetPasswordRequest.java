package com.devmarquinhos.priowl.security.dto;

public record ResetPasswordRequest(String token, String newPassword) {
}
