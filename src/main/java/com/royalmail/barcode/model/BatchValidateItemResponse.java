package com.royalmail.barcode.model;

/**
 * Per-barcode result returned by the batch validation endpoint.
 */
public class BatchValidateItemResponse {

    private final String barcode;
    private final boolean valid;

    public BatchValidateItemResponse(String barcode, boolean valid) {
        this.barcode = barcode;
        this.valid = valid;
    }

    public String getBarcode() {
        return barcode;
    }

    public boolean isValid() {
        return valid;
    }
}

