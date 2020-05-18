package space.aqoleg.json;

@SuppressWarnings("WeakerAccess")
public class JsonException extends RuntimeException {
    JsonException(String message) {
        super(message);
    }
}