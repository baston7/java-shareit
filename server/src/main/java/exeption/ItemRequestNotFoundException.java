package exeption;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(final String message) {
        super(message);
    }
}