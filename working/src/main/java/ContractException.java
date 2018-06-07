/**
 *
 * Called to expose contract violations
 *
 */
public class ContractException extends RuntimeException {

    //================================================================================
    // Constructors
    //================================================================================

    public ContractException() {
    }

    public ContractException(String message) {
        super(message);
    }
}
