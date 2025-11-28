package com.example.luofushan.config;

import com.example.luofushan.dto.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<Object> handle(Exception e) {
        return new Result<>(500, e.getMessage(), null); 
    }
}
