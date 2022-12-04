package exeption;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(final String message) {
        super(message);
    }
}