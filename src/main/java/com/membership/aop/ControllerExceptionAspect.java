package com.membership.aop;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ControllerExceptionAspect {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException exception){
        Map<String, String> errMessage = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(
                error->errMessage.put(error.getField(), error.getDefaultMessage())
        );
        return errMessage;
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public Map<String, String> handleInvalidArgument(NoSuchElementException exception){
        Map<String, String> errMessage = new HashMap<>();
       errMessage.put("Error Message", exception.getMessage());
        return errMessage;
    }
}
