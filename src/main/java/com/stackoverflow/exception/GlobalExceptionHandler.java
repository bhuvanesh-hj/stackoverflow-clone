package com.stackoverflow.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception e, Model model) {
        model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
        return "error";
    }
}
