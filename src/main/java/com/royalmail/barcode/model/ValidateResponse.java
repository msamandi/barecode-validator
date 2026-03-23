package com.royalmail.barcode.model;

/**
 * Response payload returned by the {@code /validate} endpoint.
 *
 * <p>Wraps the boolean validation result so that the JSON body is a proper
 * object ({@code {"valid": true}}) rather than a bare {@code true}/{@code false}.
 * This makes the contract easier to extend in the future.
 */
public class ValidateResponse {

    private final boolean valid;

    public ValidateResponse(boolean valid) {
        this.valid = valid;
    }

    public boolean getValid() {
        return valid;
    }
}
