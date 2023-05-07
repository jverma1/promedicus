package au.com.promedicus.admissions.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorMessage {
    private LocalDateTime timestamp;
    private String message;
    private String description;
}