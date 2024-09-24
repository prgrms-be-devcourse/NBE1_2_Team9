package com.grepp.nbe1_2_team09.common.exception;

public record ErrorResponse(
        int status,
        String title,
        String message
) {
    public static ErrorResponse from(int status, String title, String message) {
        return new ErrorResponse(status, title, message);
    }
}