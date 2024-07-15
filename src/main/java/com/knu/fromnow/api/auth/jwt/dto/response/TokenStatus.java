package com.knu.fromnow.api.auth.jwt.dto.response;

public enum TokenStatus {
    VALID,
    INVALID,
    ACCESS_TOKEN_EXPIRED,
    REFRESH_TOKEN_EXPIRED,
    REFRESH_TOKEN_MISMATCH,
    UNSUPPORTED,
    MISMATCH_CLAIMS
}
