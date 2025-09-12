package com.sobow.shopping.controllers;

import com.sobow.shopping.exceptions.ImageProcessingException;
import com.sobow.shopping.exceptions.InsufficientStockException;
import com.sobow.shopping.exceptions.OutOfStockException;
import com.sobow.shopping.exceptions.ResourceAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ProblemDetail> handleMaxSize(MaxUploadSizeExceededException e, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.PAYLOAD_TOO_LARGE);
        pd.setTitle("File too large");
        pd.setDetail(e.getMessage());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleResourceConflict(ResourceAlreadyExistsException e, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Resource already exists");
        pd.setDetail(e.getMessage());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ProblemDetail> handleOutOfStock(OutOfStockException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Out of stock");
        pd.setDetail(ex.getMessage());
        pd.setProperty("productId", ex.getProductId());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ProblemDetail> handleInsufficient(InsufficientStockException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Insufficient stock");
        pd.setDetail(ex.getMessage());
        pd.setProperty("productId", ex.getProductId());
        pd.setProperty("available", ex.getAvailable());
        pd.setProperty("requested", ex.getRequested());
        pd.setProperty("alreadyInCart", ex.getAlreadyInCart());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
}
