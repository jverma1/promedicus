package au.com.promedicus.admissions.exceptions;

public class SourceNotAllowedForInternalRequestsException extends RuntimeException {
    public SourceNotAllowedForInternalRequestsException() {
        super("Source not allowed for internal admission");
    }
}