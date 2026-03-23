package com.royalmail.barcode.model;

/**
 * Request body for POST validation endpoint.
 *
 * <p>Contains the barcode string to be validated via POST request.
 */
public class ValidateRequest {
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
