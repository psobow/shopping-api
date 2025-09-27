package com.sobow.shopping.controllers.advice;

import com.sobow.shopping.controllers.advice.dto.ErrorResponse;
import com.sobow.shopping.exceptions.CartEmptyException;
import com.sobow.shopping.exceptions.CartItemAlreadyExistsException;
import com.sobow.shopping.exceptions.CategoryAlreadyExistsException;
import com.sobow.shopping.exceptions.EmailAlreadyExistsException;
import com.sobow.shopping.exceptions.ImageProcessingException;
import com.sobow.shopping.exceptions.InsufficientStockException;
import com.sobow.shopping.exceptions.InvalidOldPasswordException;
import com.sobow.shopping.exceptions.InvalidPriceException;
import com.sobow.shopping.exceptions.InvalidUserAuthoritiesException;
import com.sobow.shopping.exceptions.NoAuthenticationException;
import com.sobow.shopping.exceptions.OverDecrementException;
import com.sobow.shopping.exceptions.ProductAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Removes the wrapper path segment <code>.value</code> from dotted/indexed field paths.
     *
     * <p><b>Pattern:</b> <code>\\.value(?=\\.|\\[|$)</code></p>
     *
     * <p><b>How it works</b></p>
     * <ul>
     *   <li><code>\\.</code> — match a literal dot before <code>value</code> (ensures a segment).</li>
     *   <li><code>value</code> — match the exact text.</li>
     *   <li><code>(?=\\.|\\[|$)</code> — positive lookahead: next char must be a dot, an opening
     *       bracket, or end of string. Prevents touching words like <code>evaluation</code>
     *       or <code>valueExtra</code>.</li>
     * </ul>
     *
     * <p><b>Examples</b></p>
     * <pre>
     * // matched → stripped
     * "password.value"              => "password"
     * "authorities.value[0].role"   => "authorities[0].role"
     * "a.value.b.value[0].c"        => "a.b[0].c"
     *
     * // NOT matched → unchanged
     * "evaluation.valuex"
     * "obj.valueExtra.more"
     * "value"
     * </pre>
     */
    private static final Pattern VALUE_SEGMENT =
        Pattern.compile("\\.value(?=\\.|\\[|$)");
    
    private String normalizeField(String field) {
        return VALUE_SEGMENT.matcher(field).replaceAll("");
    }
    
    // This exception is thrown for a single object
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBodyValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        // log.error("Unhandled exception on {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getClass().getName());
        
        List<ErrorResponse.FieldViolation> violations =
            ex.getBindingResult().getFieldErrors()
              .stream()
              .map(fe -> new ErrorResponse.FieldViolation(
                  normalizeField(fe.getField()),
                  fe.getDefaultMessage()))
              .toList();
        
        return ResponseEntity.badRequest().body(
            new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Constraint violations", violations));
    }
    
    // This exception is thrown for a list of method parameters
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodParamValidation(
        HandlerMethodValidationException ex, HttpServletRequest req
    ) {
        // log.error("Unhandled exception on {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getClass().getName());
        
        List<ErrorResponse.FieldViolation> violations = new ArrayList<>();
        
        // @RequestParam / @PathVariable - Skip request body
        ex.getParameterValidationResults()
          .stream()
          .filter(r -> !r.getMethodParameter().hasParameterAnnotation(RequestBody.class))
          .forEach(r -> {
              String name = r.getMethodParameter().getParameterName();
              r.getResolvableErrors()
               .forEach(err -> violations.add(new ErrorResponse.FieldViolation(name, err.getDefaultMessage())));
          });
        
        // @RequestBody bean results — take only FieldError
        ex.getBeanResults().forEach(bean -> {
            bean.getResolvableErrors().forEach(err -> {
                if (err instanceof FieldError fe) {
                    violations.add(new ErrorResponse.FieldViolation(
                        normalizeField(fe.getField()), fe.getDefaultMessage())
                    );
                }
            });
        });
        
        return ResponseEntity.badRequest().body(
            new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Constraint violations", violations));
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(BadCredentialsException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Invalid credentials");
        pd.setDetail("Email or password is incorrect.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(pd);
    }
    
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
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setTitle("Empty cart");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFound(UsernameNotFoundException ex,
                                                            HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("User not found");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(NoAuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleNoAuthentication(NoAuthenticationException ex,
                                                                HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Authentication required");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(InvalidOldPasswordException.class)
    public ResponseEntity<ProblemDetail> handleInvalidOldPassword(InvalidOldPasswordException ex,
                                                                  HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid old password");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleEmailConflict(EmailAlreadyExistsException e, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Email already exists");
        pd.setDetail(e.getMessage());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
    
    @ExceptionHandler(InvalidUserAuthoritiesException.class)
    public ResponseEntity<ProblemDetail> handleInvalidUserAuthorities(
        InvalidUserAuthoritiesException ex,
        HttpServletRequest request
    ) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid authorities");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }
}
