package au.com.promedicus.admissions.controller;

import au.com.promedicus.admissions.dao.AdmissionRepository;
import au.com.promedicus.admissions.exceptions.*;
import au.com.promedicus.admissions.model.Admission;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static au.com.promedicus.admissions.model.Category.EMERGENCY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RestController
@Slf4j
@RequestMapping("/api/admission")
public class AdmissionController {
    private static final String SOURCE_INTERNAL = "Internal";
    @Autowired
    private AdmissionRepository admissionRepository;

    @GetMapping(produces = "application/json")
    public List<Admission> getAdmissionList(){
        log.info("Getting Admission List");
        return admissionRepository.findAll();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Admission> createAdmission(
            @Valid
            @RequestBody Admission admission) {
        log.info("Creating Admission");
        validateCreateAdmissionRequest(admission);
        admission.setAdmissionDate(LocalDateTime.now());
        Admission savedAdmission = admissionRepository.save(admission);
        return new ResponseEntity<>(savedAdmission, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Admission> updateAdmission(@PathVariable Long id, @Valid @RequestBody Admission admission) {
        log.info("Updating existing admission");
        Optional<Admission> existingAdmission =  admissionRepository.findById(id);
        if (existingAdmission.isPresent()) {
            validateUpdateAdmissionRequest(admission, existingAdmission);
            existingAdmission.get().setName(admission.getName());
            existingAdmission.get().setDob(admission.getDob());
            existingAdmission.get().setSex(admission.getSex());
            Admission updatedAdmission = admissionRepository.save(existingAdmission.get());
            return new ResponseEntity<>(updatedAdmission, HttpStatus.OK);
        }
        throw new AdmissionNotFoundException();
    }

    private static void validateCreateAdmissionRequest(Admission admission) {
        validateDateOfBirth(admission);
        if (admission.isExternal()){
            if (isNull(admission.getSource())) {
                throw new MissingExternalSourceException();
            }
            validateExternalSystemCategory(admission);
        } else {
            validateSource(admission);
        }
    }

    private static void validateUpdateAdmissionRequest(Admission admission, Optional<Admission> existingAdmission) {
        validateDateOfBirth(admission);
        if (nonNull(admission.getAdmissionDate())
                && !existingAdmission.get().getAdmissionDate().equals(admission.getAdmissionDate())) {
            throw new FieldUpdateNotAllowedException("admissionDate");
        }
        if (existingAdmission.get().isExternal()) {
            //category or source update not allowed for external system
            if (nonNull(admission.getSource())
                    && !existingAdmission.get().getSource().equals(admission.getSource())) {
                throw new FieldUpdateNotAllowedException("source");
            }
            validateExternalSystemCategory(admission);
        } else {
            validateSource(admission);
            existingAdmission.get().setCategory(admission.getCategory());
        }
    }

    private static void validateExternalSystemCategory(Admission admission) {
        if(!EMERGENCY.equals(admission.getCategory())) {
            throw new CategoryNotAllowedForExternalSystemException();
        }
    }

    private static void validateDateOfBirth(Admission admission) {
        if (admission.getDob().isAfter(LocalDate.now())) {
            throw new DateOfBirthException();
        }
    }

    private static void validateSource(Admission admission) {
        if (nonNull(admission.getSource())) {
            throw new SourceNotAllowedForInternalRequestsException();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Admission> deleteAdmission(@PathVariable Long id) {
        log.info("Deleting admission");
        if (!admissionRepository.existsById(id)) {
            throw new AdmissionNotFoundException();
        }
        admissionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
