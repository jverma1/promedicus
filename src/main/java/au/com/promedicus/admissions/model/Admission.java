package au.com.promedicus.admissions.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Admission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime admissionDate;
    @NotBlank(message="Name is mandatory")
    private String name;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "Birth Date is mandatory")
    private LocalDate dob;
    @NotNull(message = "Gender is mandatory")
    private Sex sex;
    @NotNull(message = "Category is mandatory")
    private Category category;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String source;
    private boolean external;
}
