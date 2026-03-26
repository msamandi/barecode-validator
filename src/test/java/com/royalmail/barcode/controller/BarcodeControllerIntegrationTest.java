package com.royalmail.barcode.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for barcode validation endpoints.
 *
 * <p>Tests both GET and POST endpoints for single barcode validation.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BarcodeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // -------------------------------------------------------------------------
    // GET /validate endpoint tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /validate")
    class GetValidateEndpoint {

        @Test
        @DisplayName("GET /validate?barcode=AA473124829GB → 200 {valid:true}")
        void validBarcode_returns200WithTrue() throws Exception {
            mockMvc.perform(get("/validate")
                            .param("barcode", "AA473124829GB")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.valid").value(true));
        }

        @Test
        @DisplayName("GET /validate?barcode=AA473124828GB → 200 {valid:false}")
        void invalidCheckDigit_returns200WithFalse() throws Exception {
            mockMvc.perform(get("/validate")
                            .param("barcode", "AA473124828GB")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valid").value(false));
        }

        @Test
        @DisplayName("GET /validate?barcode=AA473124829US → 200 {valid:false} (wrong country)")
        void wrongCountryCode_returns200WithFalse() throws Exception {
            mockMvc.perform(get("/validate")
                            .param("barcode", "AA473124829US")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valid").value(false));
        }

        @Test
        @DisplayName("GET /validate?barcode=1A473124829GB → 200 {valid:false} (invalid prefix)")
        void invalidPrefix_returns200WithFalse() throws Exception {
            mockMvc.perform(get("/validate")
                            .param("barcode", "1A473124829GB")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valid").value(false));
        }

        @Test
        @DisplayName("GET /validate (no barcode param) → 400 Bad Request")
        void missingBarcodeParam_returns400() throws Exception {
            mockMvc.perform(get("/validate")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET /validate?barcode= (blank barcode param) → 400 Bad Request")
        void blankBarcodeParam_returns400() throws Exception {
            mockMvc.perform(get("/validate")
                            .param("barcode", "   ")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
        }
    }

    // -------------------------------------------------------------------------
    // POST /validate endpoint tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("POST /validate")
    class PostValidateEndpoint {

        @Test
        @DisplayName("POST /validate with valid barcode → 200 {valid:true}")
        void validBarcode_returns200WithTrue() throws Exception {
            mockMvc.perform(post("/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"barcode\": \"AA473124829GB\"}"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.valid").value(true));
        }

        @Test
        @DisplayName("POST /validate with invalid barcode → 200 {valid:false}")
        void invalidBarcode_returns200WithFalse() throws Exception {
            mockMvc.perform(post("/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"barcode\": \"AA473124828GB\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valid").value(false));
        }

        @Test
        @DisplayName("POST /validate with wrong country code → 200 {valid:false}")
        void wrongCountryCode_returns200WithFalse() throws Exception {
            mockMvc.perform(post("/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"barcode\": \"AA473124829US\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valid").value(false));
        }

        @Test
        @DisplayName("POST /validate with invalid prefix → 200 {valid:false}")
        void invalidPrefix_returns200WithFalse() throws Exception {
            mockMvc.perform(post("/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"barcode\": \"1A473124829GB\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valid").value(false));
        }

        @Test
        @DisplayName("POST /validate with missing barcode field → 400 Bad Request")
        void missingBarcodeField_returns400() throws Exception {
            mockMvc.perform(post("/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /validate with null barcode → 400 Bad Request")
        void nullBarcode_returns400() throws Exception {
            mockMvc.perform(post("/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"barcode\": null}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /validate with blank barcode → 400 Bad Request")
        void blankBarcode_returns400() throws Exception {
            mockMvc.perform(post("/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"barcode\": \"   \"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /validate with empty barcode → 400 Bad Request")
        void emptyBarcode_returns400() throws Exception {
            mockMvc.perform(post("/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"barcode\": \"\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /validate with invalid JSON → 400 Bad Request")
        void invalidJson_returns400() throws Exception {
            mockMvc.perform(post("/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("invalid json"))
                    .andExpect(status().isBadRequest());
        }
    }

    // -------------------------------------------------------------------------
    // POST /validate/batch endpoint tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("POST /validate/batch")
    class PostBatchValidateEndpoint {

        @Test
        @DisplayName("POST /validate/batch with mixed barcodes → 200 with per-item results")
        void mixedBarcodes_returns200WithResults() throws Exception {
            mockMvc.perform(post("/validate/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"barcodes\": [\"AA473124829GB\", \"AA473124828GB\", \"1A473124829GB\"]}"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.results.length()").value(3))
                    .andExpect(jsonPath("$.results[0].barcode").value("AA473124829GB"))
                    .andExpect(jsonPath("$.results[0].valid").value(true))
                    .andExpect(jsonPath("$.results[1].barcode").value("AA473124828GB"))
                    .andExpect(jsonPath("$.results[1].valid").value(false))
                    .andExpect(jsonPath("$.results[2].barcode").value("1A473124829GB"))
                    .andExpect(jsonPath("$.results[2].valid").value(false));
        }

        @Test
        @DisplayName("POST /validate/batch with missing barcodes field → 400 Bad Request")
        void missingBarcodesField_returns400() throws Exception {
            mockMvc.perform(post("/validate/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("POST /validate/batch with empty barcodes list → 400 Bad Request")
        void emptyBarcodesList_returns400() throws Exception {
            mockMvc.perform(post("/validate/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"barcodes\": []}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("POST /validate/batch with blank barcode entry → 400 Bad Request")
        void blankBarcodeEntry_returns400() throws Exception {
            mockMvc.perform(post("/validate/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"barcodes\": [\"AA473124829GB\", \"   \"]}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("POST /validate/batch with invalid JSON → 400 Bad Request")
        void invalidJson_returns400() throws Exception {
            mockMvc.perform(post("/validate/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("invalid json"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("INVALID_JSON"));
        }
    }
}
