package com.royalmail.barcode.model;

import java.util.List;

/**
 * Response payload returned by the batch validation endpoint.
 */
public class BatchValidateResponse {

    private final List<BatchValidateItemResponse> results;

    public BatchValidateResponse(List<BatchValidateItemResponse> results) {
        this.results = results;
    }

    public List<BatchValidateItemResponse> getResults() {
        return results;
    }
}

