package il.ac.technion.cs.matrices.synthesis;

/**
 * An exception thrown when a synthesizer cannot synthesize a program for a given set of examples.
 * This can happen if the examples are inconsistent, or if the synthesizer is not powerful enough.
 */
public class CannotSynthesizeException extends RuntimeException {

    /**
     * Constructs a new instance of the exception with the given message.
     * @param message The message
     */
    public CannotSynthesizeException(String message) {
        super(message);
    }
}
