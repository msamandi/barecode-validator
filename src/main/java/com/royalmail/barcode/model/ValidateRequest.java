package com.royalmail.barcode.model;

import javax.validation.constraints.NotBlank;

/**
 * Request body for POST validation endpoint.
 *
 * <p>Contains the barcode string to be validated via POST request.
 */
public class ValidateRequest {
    @NotBlank(message = "Barcode must not be blank")
    private String barcode;

    public ValidateRequest() {}

    public ValidateRequest(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
