package au.com.promedicus.admissions.exceptions;

public class MissingExternalSourceException extends RuntimeException {
    public MissingExternalSourceException(){
        super("Source is mandatory for external admissions");
    }
}
