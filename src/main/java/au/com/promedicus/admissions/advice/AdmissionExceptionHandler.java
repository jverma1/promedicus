package au.com.promedicus.admissions.advice;

import au.com.promedicus.admissions.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class AdmissionExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Error occurred {}", ex.getMessage());
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler({ HttpMessageNotReadableException.class,
            DateOfBirthException.class,
            MissingExternalSourceException.class,
            SourceNotAllowedForInternalRequestsException.class,
            CategoryNotAllowedForExternalSystemException.class,
            FieldUpdateNotAllowedException.class})
    public ErrorMessage handleValidationExceptions(Exception ex) {
        log.error("Error occurred {}", ex.getMessage());
        return new ErrorMessage( LocalDateTime.now(), ex.getMessage(), "Bad Request");
    }

    @ResponseBody
    @ExceptionHandler(AdmissionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(AdmissionNotFoundException ex) {
        log.error("Error occurred {}", ex.getMessage());
        return new ErrorMessage( LocalDateTime.now(), ex.getMessage(), "Not Found");
    }
}
