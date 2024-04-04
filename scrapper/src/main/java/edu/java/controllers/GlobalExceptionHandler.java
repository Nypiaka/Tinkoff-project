package edu.java.controllers;

import edu.java.utils.Utils;
import edu.java.utils.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({Throwable.class})
    public ResponseEntity<ApiErrorResponse> handleException(Throwable ex, WebRequest request) {
        if (ex instanceof HttpServerErrorException casted) {
            return ResponseEntity.status(casted.getStatusCode())
                .body(Utils.errorRequest((casted).getStatusCode().value(), ex));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Utils.errorRequest(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex));
    }
}
