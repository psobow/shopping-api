package com.sobow.shopping.controllers;

import com.sobow.shopping.exceptions.ImageProcessingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    
    @ExceptionHandler(ImageProcessingException.class)
    public ResponseEntity<ProblemDetail> handleImageProcessing(
        ImageProcessingException exception, HttpServletRequest request) {
        
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problemDetail.setTitle("Unable to process image");
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(EntityNotFoundException exception, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Resource not found");
        pd.setDetail(exception.getMessage());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
}
