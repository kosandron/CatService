package kosandron.exceptions;

public class SameCatException extends RuntimeException {
    public SameCatException() {
        super("You cannot make it with same cat!");
    }
}
