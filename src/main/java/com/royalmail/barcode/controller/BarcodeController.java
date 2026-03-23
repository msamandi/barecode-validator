package com.royalmail.barcode.controller;

import com.royalmail.barcode.exception.InvalidInputException;
import com.royalmail.barcode.model.ValidateRequest;
import com.royalmail.barcode.model.ValidateResponse;
import com.royalmail.barcode.service.BarcodeValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the S10 barcode validation endpoint.
 *
 * <p>The endpoints accept a barcode string and return a JSON object indicating whether
 * the barcode is valid.
 *
 * <p>Examples:
 * <pre>
 *   GET /validate?barcode=AA473124829GB  → {"valid": true}
 *   POST /validate {"barcode":"AA473124829GB"}  → {"valid": true}
 *   GET /validate?barcode=AA473124828GB  → {"valid": false}
 *   POST /validate {"barcode":"AA473124828GB"}  → {"valid": false}
 * </pre>
 */
@RestController
@Validated
public class BarcodeController {

    private static final Logger logger = LoggerFactory.getLogger(BarcodeController.class);
    private final BarcodeValidatorService validatorService;

    public BarcodeController(BarcodeValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    /**
     * Validates the supplied S10 barcode via GET request.
     *
     * @param barcode the barcode string to validate (required, must not be blank)
     * @return {@code 200 OK} with {@code {"valid": true/false}}; or
     *         {@code 400 Bad Request} if the {@code barcode} parameter is missing / blank
     */
    @GetMapping("/validate")
    public ResponseEntity<ValidateResponse> validateGet(
            @RequestParam String barcode) {

        logger.info("Processing GET /validate request with barcode");

        if (barcode == null || barcode.isBlank()) {
            logger.warn("Invalid input - barcode is null or blank");
            throw new InvalidInputException(
                    "Barcode must not be null or blank",
                    "BLANK_BARCODE",
                    "barcode"
            );
        }

        logger.debug("Validating barcode: {}", barcode);
        boolean result = validatorService.validate(barcode);
        
        logger.info("Validation completed for barcode - Result: {}", result);
        return ResponseEntity.ok(new ValidateResponse(result));
    }

    /**
     * Validates the supplied S10 barcode via POST request.
     *
     * @param request the request body containing the barcode
     * @return {@code 200 OK} with {@code {"valid": true/false}}; or
     *         {@code 400 Bad Request} if the barcode is missing / blank
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidateResponse> validatePost(
            @RequestBody @Validated ValidateRequest request) {

        String barcode = request.getBarcode().trim();
        logger.debug("Validating barcode: {}", barcode);
        boolean result = validatorService.validate(barcode);
        
        return ResponseEntity.ok(new ValidateResponse(result));
    }
}
