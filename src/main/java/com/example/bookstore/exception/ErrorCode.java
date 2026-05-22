package com.example.bookstore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Unidentified system error", HttpStatus.INTERNAL_SERVER_ERROR),
    BOOK_NOT_EXISTED(1001, "This book does not existed", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1002, "Invalid category", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1003, "invalid input data", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1004, " This user already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, " User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "You do not have permission", HttpStatus.FORBIDDEN),
    INSUFFICIENT_STOCK(1009, "Không đủ số lượng sách trong kho", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}