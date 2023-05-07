package au.com.promedicus.admissions.exceptions;

public class CategoryNotAllowedForExternalSystemException extends RuntimeException {
    public CategoryNotAllowedForExternalSystemException(){
        super("Category must be EMERGENCY for external admissions");
    }
}
