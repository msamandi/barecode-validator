package com.royalmail.barcode.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for validating Royal Mail S10 barcodes.
 *
 * <p>An S10 barcode has the structure: [AA][NNNNNNNN][C][GB]
 * <ul>
 *   <li>Prefix (2 chars)      – two uppercase alpha characters, e.g. "AA"–"ZZ"</li>
 *   <li>Serial number (8 digits) – e.g. "47312482"</li>
 *   <li>Check digit (1 digit) – calculated from the serial number</li>
 *   <li>Country code (2 chars) – must be "GB" for Royal Mail</li>
 * </ul>
 *
 * <p>Total length: 13 characters.
 *
 * @see <a href="https://en.wikipedia.org/wiki/S10_(UPU_standard)">S10 UPU standard</a>
 */
@Service
public class BarcodeValidatorService {

    private static final Logger logger = LoggerFactory.getLogger(BarcodeValidatorService.class);

    /** Weights applied left-to-right to the 8 serial digits when computing the check digit. */
    private static final int[] WEIGHTS = {8, 6, 4, 2, 3, 5, 9, 7};

    /** The only country code accepted by this validator. */
    private static final String VALID_COUNTRY_CODE = "GB";

    /** Expected total length of a valid S10 barcode. */
    private static final int BARCODE_LENGTH = 13;

    /**
     * Validates an S10 barcode string.
     *
     * @param barcode the barcode string to validate (must not be {@code null})
     * @return {@code true} if the barcode is structurally valid and has a correct check digit;
     *         {@code false} otherwise
     */
    public boolean validate(String barcode) {
        if (barcode == null) {
            logger.debug("Barcode validation failed - barcode is null");
            return false;
        }

        // Must be exactly 13 characters
        if (barcode.length() != BARCODE_LENGTH) {
            logger.debug("Barcode validation failed - invalid length: {}, expected: {}", 
                    barcode.length(), BARCODE_LENGTH);
            return false;
        }

        String prefix      = barcode.substring(0, 2);    // chars 1–2
        String serial      = barcode.substring(2, 10);   // chars 3–10
        char   checkChar   = barcode.charAt(10);          // char 11
        String countryCode = barcode.substring(11, 13);  // chars 12–13

        return isValidPrefix(prefix)
                && isValidSerial(serial)
                && isValidCheckDigit(serial, checkChar)
                && isValidCountryCode(countryCode);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Prefix must consist of exactly two uppercase ASCII letters (A–Z).
     */
    private boolean isValidPrefix(String prefix) {
        return prefix.matches("[A-Z]{2}");
    }

    /**
     * Serial number must consist of exactly eight decimal digits (0–9).
     * Leading zeros are permitted (e.g. "00000001").
     */
    private boolean isValidSerial(String serial) {
        return serial.matches("\\d{8}");
    }

    /**
     * Country code must be "GB".
     */
    private boolean isValidCountryCode(String countryCode) {
        return VALID_COUNTRY_CODE.equals(countryCode);
    }

    /**
     * Computes the expected check digit from the 8-digit serial and compares it
     * against the supplied check character.
     *
     * <p>Algorithm (per the UPU S10 specification):
     * <ol>
     *   <li>Multiply each of the 8 serial digits by its corresponding weight
     *       (8, 6, 4, 2, 3, 5, 9, 7) and sum the products.</li>
     *   <li>check = 11 − (sum mod 11)</li>
     *   <li>If check == 10 → use 0</li>
     *   <li>If check == 11 → use 5</li>
     * </ol>
     *
     * @param serial    8-digit serial number string (already validated as digits)
     * @param checkChar the check digit character from the barcode
     * @return {@code true} if the computed check digit matches {@code checkChar}
     */
    private boolean isValidCheckDigit(String serial, char checkChar) {
        if (!Character.isDigit(checkChar)) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < WEIGHTS.length; i++) {
            int digit = Character.getNumericValue(serial.charAt(i));
            sum += digit * WEIGHTS[i];
        }

        int checkDigit = 11 - (sum % 11);

        if (checkDigit == 10) {
            checkDigit = 0;
        } else if (checkDigit == 11) {
            checkDigit = 5;
        }

        return checkDigit == Character.getNumericValue(checkChar);
    }
}
