package at.mci.bugtracker.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String from, String to) {
        super("Invalid status transition from " + from + " to " + to);
    }
}
