package com.royalmail.barcode.controller;

import com.royalmail.barcode.service.BarcodeValidatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests covering the 500 Internal Server Error path.
 *
 * <p>Uses @MockBean to simulate an unexpected service-layer failure,
 * verifying that the global exception handler catches it and returns
 * a consistent structured error response.
 *
 * <p>This is the ONLY path to 500 in the current application:
 * all bad-input paths are caught at 400 before the service is reached.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BarcodeControllerServerErrorTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock the service so we can simulate an unexpected failure
     * without touching real validation logic.
     */
    @MockBean
    private BarcodeValidatorService barcodeValidatorService;

    // -------------------------------------------------------------------------
    // GET /validate – service crashes unexpectedly
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /validate – unexpected service failure")
    class GetServiceFailure {

        @Test
        @DisplayName("Service throws RuntimeException → 500 with errorCode INTERNAL_SERVER_ERROR")
        void serviceThrowsRuntimeException_returns500() throws Exception {
            when(barcodeValidatorService.validate(anyString()))
                    .thenThrow(new RuntimeException("Unexpected service failure"));

            mockMvc.perform(get("/validate")
                            .param("barcode", "AA473124829GB")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.path").value("/validate"));
        }
    }

    // -------------------------------------------------------------------------
    // POST /validate – service crashes unexpectedly
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("POST /validate – unexpected service failure")
    class PostServiceFailure {

        @Test
        @DisplayName("Service throws RuntimeException → 500 with errorCode INTERNAL_SERVER_ERROR")
        void serviceThrowsRuntimeException_returns500() throws Exception {
            when(barcodeValidatorService.validate(anyString()))
                    .thenThrow(new RuntimeException("Unexpected service failure"));

            mockMvc.perform(post("/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"barcode\": \"AA473124829GB\"}")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.path").value("/validate"));
        }
    }
}

