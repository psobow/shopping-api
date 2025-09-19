package com.sobow.shopping.controllers;

import com.sobow.shopping.exceptions.CartEmptyException;
import com.sobow.shopping.exceptions.CartItemAlreadyExistsException;
import com.sobow.shopping.exceptions.CategoryAlreadyExistsException;
import com.sobow.shopping.exceptions.ImageProcessingException;
import com.sobow.shopping.exceptions.InsufficientStockException;
import com.sobow.shopping.exceptions.InvalidPriceException;
import com.sobow.shopping.exceptions.OverDecrementException;
import com.sobow.shopping.exceptions.ProductAlreadyExistsException;
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
    
    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleCategoryConflict(CategoryAlreadyExistsException e, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Category already exists");
        pd.setDetail(e.getMessage());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleProductConflict(ProductAlreadyExistsException e, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Product already exists");
        pd.setDetail(e.getMessage());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(CartItemAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleCartItemConflict(CartItemAlreadyExistsException e, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Product already exists in the cart");
        pd.setDetail(e.getMessage());
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
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(OverDecrementException.class)
    public ResponseEntity<ProblemDetail> handleOverDecrement(OverDecrementException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Requested removal exceeds product quantity");
        pd.setDetail(ex.getMessage());
        pd.setProperty("productId", ex.getProductId());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(InvalidPriceException.class)
    public ResponseEntity<ProblemDetail> handleInvalidPrice(InvalidPriceException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid price");
        pd.setDetail(ex.getMessage());
        pd.setProperty("price", ex.getPrice());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(CartEmptyException.class)
    public ResponseEntity<ProblemDetail> handleCartEmpty(CartEmptyException ex, HttpServletRequest req) {
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY); // 422
        pd.setTitle("Empty cart");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
}
