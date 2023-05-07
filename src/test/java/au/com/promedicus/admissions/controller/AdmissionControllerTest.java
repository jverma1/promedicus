package au.com.promedicus.admissions.controller;


import au.com.promedicus.admissions.dao.AdmissionRepository;
import au.com.promedicus.admissions.exceptions.*;
import au.com.promedicus.admissions.model.Admission;
import au.com.promedicus.admissions.model.Category;
import au.com.promedicus.admissions.model.Sex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static au.com.promedicus.admissions.model.Category.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdmissionControllerTest {

    @Mock
    private AdmissionRepository repository;

    @InjectMocks
    AdmissionController admissionController;
    private static Admission internalAdmission;
    private static Admission externalAdmission;
    private static Admission existingAdmission;

    @BeforeEach
    public void before() {
        internalAdmission = new Admission(23L,  null, "John Doe", getDate("10/12/1990"), Sex.MALE, NORMAL, null, false);
        externalAdmission = new Admission(24L,  null, "Victoria", getDate("16/02/1970"), Sex.FEMALE, Category.EMERGENCY, "External Hospital", true);
        existingAdmission = new Admission(25L,  LocalDateTime.now(), "Elizabeth",getDate("14/06/1980"), Sex.FEMALE, NORMAL, null, false);
    }

    @Test
    public void getAdmissionListTest() {
        when(repository.findAll()).thenReturn(Arrays.asList(internalAdmission, externalAdmission, existingAdmission));
        List<Admission> admissionList = admissionController.getAdmissionList();
        assertNotNull(admissionList);
        assertEquals(3, admissionList.size());
        verify(repository).findAll();
    }

    @Test
    public void createAdmissionTest() {
        when(repository.save(any())).thenReturn(internalAdmission);
        ResponseEntity<Admission> responseEntity = admissionController.createAdmission(internalAdmission);
        Admission savedAdmission = responseEntity.getBody();
        assertNotNull(savedAdmission);
        assertEquals("John Doe", savedAdmission.getName());
        assertEquals(getDate("10/12/1990"), savedAdmission.getDob());
        assertNull(savedAdmission.getSource());
        verify(repository).save(any());
    }

    @Test
    public void createAdmission_ThrowSourceNotAllowedForInternalSystemTest() {
        internalAdmission.setSource("abc xxx");
        assertThrows(SourceNotAllowedForInternalRequestsException.class, () -> admissionController.createAdmission(internalAdmission));
        verifyNoInteractions(repository);
    }

    @Test
    public void createAdmission_ThrowMissingExternalSourceTest() {
        externalAdmission.setSource(null);
        assertThrows(MissingExternalSourceException.class, () -> admissionController.createAdmission(externalAdmission));
        verifyNoInteractions(repository);
    }

    @Test
    public void createAdmission_ExternalSystemTest() {
        when(repository.save(any())).thenReturn(externalAdmission);
        ResponseEntity<Admission> responseEntity = admissionController.createAdmission(externalAdmission);
        Admission savedAdmission = responseEntity.getBody();
        assertNotNull(savedAdmission);
        assertEquals("Victoria", savedAdmission.getName());
        assertEquals(getDate("16/02/1970"), savedAdmission.getDob());
        assertNotNull(savedAdmission.getSource());
        verify(repository).save(any());
    }

    @Test
    public void createAdmission_ThrowException() {
        internalAdmission.setDob(LocalDate.now().plusDays(1));
        assertThrows(DateOfBirthException.class, () -> admissionController.createAdmission(internalAdmission));
        verifyNoInteractions(repository);
    }

    @Test
    public void updateAdmissionTest() {
        Admission updateAdmissionRequest = getUpdateAdmissionRequest(INPATIENT, false);
        when(repository.findById(any())).thenReturn(Optional.of(existingAdmission));
        when(repository.save(any())).thenReturn(updateAdmissionRequest);
        ResponseEntity<Admission> responseEntity = admissionController.updateAdmission(25L, updateAdmissionRequest);
        Admission updatedAdmission = responseEntity.getBody();
        assertNotNull(updatedAdmission);
        assertEquals("new Name", updatedAdmission.getName());
        assertEquals(getDate("10/10/1992"), updatedAdmission.getDob());
        verify(repository).findById(any());
        verify(repository).save(any());
    }

    @Test
    public void updateAdmission_ForExternalSystemTest() {
        Admission updateAdmissionRequest = getUpdateAdmissionRequest(EMERGENCY, false);
        when(repository.findById(any())).thenReturn(Optional.of(externalAdmission));
        when(repository.save(any())).thenReturn(updateAdmissionRequest);
        ResponseEntity<Admission> responseEntity = admissionController.updateAdmission(24L, updateAdmissionRequest);
        Admission updatedAdmission = responseEntity.getBody();
        assertNotNull(updatedAdmission);
        assertEquals("new Name", updatedAdmission.getName());
        assertEquals(getDate("10/10/1992"), updatedAdmission.getDob());
        verify(repository).findById(any());
        verify(repository).save(any());
    }

    @Test
    public void updateAdmission_WhenAdmissionNotFound() {
        Admission updateAdmissionRequest = getUpdateAdmissionRequest(INPATIENT, false);
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(AdmissionNotFoundException.class, () -> admissionController.updateAdmission(24L, updateAdmissionRequest));
        verify(repository).findById(any());
        verify(repository, times(0)).save(any());
    }

    @Test
    public void updateAdmission_ForExternalSystem_ThrowCategoryNotAllowedExceptionTest() {
        Admission updateAdmissionRequest = getUpdateAdmissionRequest(NORMAL,  false);
        when(repository.findById(any())).thenReturn(Optional.of(externalAdmission));
        assertThrows(CategoryNotAllowedForExternalSystemException.class, () ->
                admissionController.updateAdmission(24L, updateAdmissionRequest));
        verify(repository).findById(any());
        verify(repository,times(0)).save(any());
    }

    @Test
    public void updateAdmission_ForExternalSystem_ThrowSourceUpdateNotAllowedExceptionTest() {
        Admission updateAdmissionRequest = getUpdateAdmissionRequest(NORMAL,true);
        when(repository.findById(any())).thenReturn(Optional.of(externalAdmission));
        assertThrows(FieldUpdateNotAllowedException.class, () ->
                admissionController.updateAdmission(24L, updateAdmissionRequest));
        verify(repository).findById(any());
        verify(repository,times(0)).save(any());
    }

    @Test
    public void updateAdmission_ThrowException() {
        internalAdmission.setDob(LocalDate.now().plusDays(1));
        when(repository.findById(any())).thenReturn(Optional.of(existingAdmission));
        assertThrows(DateOfBirthException.class, () -> admissionController.updateAdmission(23L, internalAdmission));
        verify(repository,times(0)).save(any());
    }

    @Test
    public void updateAdmission_ThrowFieldUpdateNotAllowedException() {
        internalAdmission.setAdmissionDate(LocalDateTime.now());
        when(repository.findById(any())).thenReturn(Optional.ofNullable(existingAdmission));
        assertThrows(FieldUpdateNotAllowedException.class, () -> admissionController.updateAdmission(23L, internalAdmission));
        verify(repository,times(0)).save(any());
    }

    @Test
    public void deleteAdmissionTest() {
        when(repository.existsById(any())).thenReturn(true);
        doNothing().when(repository).deleteById(any());
        ResponseEntity<Admission> responseEntity = admissionController.deleteAdmission(23L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(repository).existsById(any());
        verify(repository).deleteById(any());
    }

    @Test
    public void deleteAdmission_ThrowException_WhenAdmissionNotFound() {
        when(repository.existsById(any())).thenReturn(false);
        assertThrows(AdmissionNotFoundException.class, () -> admissionController.deleteAdmission(23L));
        verify(repository, times(0)).deleteById(any());
    }

    private static LocalDate getDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private static Admission getUpdateAdmissionRequest(Category category, boolean updateSource) {
        Admission updateAdmissionRequest = new Admission();
        updateAdmissionRequest.setDob(getDate("10/10/1992"));
        updateAdmissionRequest.setName("new Name");
        updateAdmissionRequest.setSex(Sex.FEMALE);
        updateAdmissionRequest.setCategory(category);
        if (updateSource) {
            updateAdmissionRequest.setSource("xxx hospital");
        }
        return updateAdmissionRequest;
    }
}
