package com.delivery.exception;

import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex, @NonNull WebRequest request) {
        return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), ex), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, @NonNull WebRequest request) {
        return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), ex), request);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    protected ResponseEntity<Object> handleEmailAlreadyExists(EmailAlreadyExistsException ex, @NonNull WebRequest request) {
        return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, ex.getMessage(), ex), request);
    }

    @ExceptionHandler(SecurityException.class)
    protected ResponseEntity<Object> handleSecurityException(SecurityException ex, @NonNull WebRequest request) {
        return buildResponseEntity(new ApiError(HttpStatus.FORBIDDEN, ex.getMessage(), ex), request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex, @NonNull WebRequest request) {
        logger.error("Erro desconhecido ocorrido", ex);
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno no servidor. Contate o suporte.", ex);
        return buildResponseEntity(apiError, request);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError, @NonNull WebRequest request) {
        HttpStatus status = apiError.getStatus();
        HttpStatusCode statusCode = (status != null) ? status : HttpStatus.INTERNAL_SERVER_ERROR;
        
        Throwable ex = apiError.getException();
        Exception exceptionToPass = (ex instanceof Exception) ? (Exception) ex : new Exception(apiError.getMessage());
        
        return handleExceptionInternal(exceptionToPass, apiError, new HttpHeaders(), statusCode, request);
    }
}
