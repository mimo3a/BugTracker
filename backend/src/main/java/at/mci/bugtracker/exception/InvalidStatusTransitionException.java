package at.mci.bugtracker.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException() {
        super("Ungültiger Statuswechsel");
    }
}
