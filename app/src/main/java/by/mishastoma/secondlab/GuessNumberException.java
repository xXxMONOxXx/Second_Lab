package by.mishastoma.secondlab;

public class GuessNumberException extends Exception{

    public GuessNumberException() {
        super();
    }

    public GuessNumberException(String message) {
        super(message);
    }

    public GuessNumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public GuessNumberException(Throwable cause) {
        super(cause);
    }
}
