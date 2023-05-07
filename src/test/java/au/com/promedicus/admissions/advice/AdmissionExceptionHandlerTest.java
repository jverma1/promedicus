package au.com.promedicus.admissions.advice;

import au.com.promedicus.admissions.exceptions.AdmissionNotFoundException;
import au.com.promedicus.admissions.exceptions.DateOfBirthException;
import au.com.promedicus.admissions.exceptions.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdmissionExceptionHandlerTest {

    AdmissionExceptionHandler handler = new AdmissionExceptionHandler();
    @Test
    public void admissionNotFoundExceptionTest(){
        AdmissionNotFoundException admissionNotFoundException = new AdmissionNotFoundException();
        ErrorMessage message = handler.handleNotFoundException(admissionNotFoundException);
        assertEquals("Admission not found", message.getMessage());
    }

    @Test
    public void validationExceptionTest(){
        DateOfBirthException dobException = new DateOfBirthException();
        ErrorMessage message = handler.handleValidationExceptions(dobException);
        assertEquals("Birth date cannot be in the future", message.getMessage());
    }

    @Test
    public void methodArgumentExceptionTest(){
        List<ObjectError> objectErrorList = new ArrayList<>();
        objectErrorList.add(new FieldError("test","name", "test message"));
        MethodArgumentNotValidException methodArgumentNotValidException =mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(objectErrorList);
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult) ;
        Map<String, String> errorMap = handler.handleValidationExceptions(methodArgumentNotValidException);
        assertEquals("test message", errorMap.get("name"));
    }
}
