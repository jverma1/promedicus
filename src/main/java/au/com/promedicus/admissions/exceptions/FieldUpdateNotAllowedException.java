package au.com.promedicus.admissions.exceptions;

public class FieldUpdateNotAllowedException extends RuntimeException {

    String fieldName;
    public FieldUpdateNotAllowedException(String fieldName){
        super(fieldName + " update not allowed");
    }
}
