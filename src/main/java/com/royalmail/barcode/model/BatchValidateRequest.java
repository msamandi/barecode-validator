package com.royalmail.barcode.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Request body for batch barcode validation.
 *
 * <p>Contains a non-empty list of barcodes to validate in a single request.
 */
public class BatchValidateRequest {

    @NotEmpty(message = "Barcodes list must not be empty")
    private List<@NotBlank(message = "Barcode must not be blank") String> barcodes;

    public BatchValidateRequest() {
    }

    public BatchValidateRequest(List<String> barcodes) {
        this.barcodes = barcodes;
    }

    public List<String> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<String> barcodes) {
        this.barcodes = barcodes;
    }
}

