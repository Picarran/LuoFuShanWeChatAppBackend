package com.example.luofushan.common.exception;

import com.example.luofushan.dto.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = LuoFuShanException.class)
    public Result<String> handleAIExternalException(LuoFuShanException e) {
        e.printStackTrace();
        return Result.buildFailure(e.getMessage());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public Result<String> handleAIExternalException(RuntimeException e) {
        e.printStackTrace();
        return Result.buildFailure(e.getMessage());
    }
}