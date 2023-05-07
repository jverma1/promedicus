package au.com.promedicus.admissions.exceptions;

public class AdmissionNotFoundException extends RuntimeException {
    public AdmissionNotFoundException(){
        super("Admission not found");
    }
}
