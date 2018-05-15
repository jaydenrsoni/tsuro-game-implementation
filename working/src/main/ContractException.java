package main;

/**
 * Created by vyasalwar on 5/8/18.
 */
public class ContractException extends RuntimeException {
    public ContractException() {
    }

    public ContractException(String message) {
        super(message);
    }
}
