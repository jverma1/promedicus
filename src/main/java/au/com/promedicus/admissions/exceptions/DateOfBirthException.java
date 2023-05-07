package au.com.promedicus.admissions.exceptions;

public class DateOfBirthException extends RuntimeException {
    public DateOfBirthException(){
        super("Birth date cannot be in the future");
    }
}
