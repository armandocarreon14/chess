package dataaccess;

/**
 * Indicates there was an error connecting to the database or any data access issues
 */
public class DataAccessException extends Exception {
    private final int errorCode;

    public DataAccessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "[" + errorCode + "] " + getMessage();
    }
}
