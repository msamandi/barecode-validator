package com.royalmail.barcode.service;

import com.royalmail.barcode.model.BatchValidateItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link BarcodeValidatorService}.
 */
class BarcodeValidatorServiceTest {

    private BarcodeValidatorService service;

    @BeforeEach
    void setUp() {
        service = new BarcodeValidatorService();
    }


    @Nested
    @DisplayName("Valid barcodes")
    class ValidBarcodes {

        @Test
        @DisplayName("should return true for the worked example AA473124829GB")
        void workedExample()
        {
            assertThat(service.validate("AA473124829GB")).isTrue();
        }

        @Test
        @DisplayName("should accept different valid prefix letters")
        void differentPrefixes() {
            // Manually compute a valid barcode for prefix ZZ
            // Serial 00000000 → sum = 0 → check = 11 - 0 = 11 → mapped to 5
            assertThat(service.validate("ZZ000000059GB")).isTrue(); // check=5 for serial 00000000... wait let me recalculate
            // Actually: serial=00000005, sum=5*7=35, check=11-(35%11)=11-2=9 → let me use a known good value
        }

        @Test
        @DisplayName("should accept leading-zero serial numbers")
        void leadingZeroSerial() {
            // serial = 00000001, weights = 8,6,4,2,3,5,9,7
            // sum = 0+0+0+0+0+0+0+7 = 7
            // check = 11 - (7 % 11) = 11 - 7 = 4
            assertThat(service.validate("AB000000014GB")).isTrue();
        }

        @Test
        @DisplayName("should return true when check digit rule maps 11 → 5")
        void checkDigitMappedFrom11To5() {
            // serial = 00000000, sum = 0, check = 11 - 0 = 11 → 5
            assertThat(service.validate("AB000000005GB")).isTrue();
        }

        @Test
        @DisplayName("should return true when check digit rule maps 10 → 0")
        void checkDigitMappedFrom10To0() {
            // We need sum % 11 = 1, so check = 10 → 0
            // serial = 00000010: digit[6]=1, weight=9 → sum=9; 11-(9%11)=11-9=2  not right
            // serial = 10000000: digit[0]=1, weight=8 → sum=8; 11-8=3  not right
            // We need sum ≡ 1 (mod 11)
            // Try serial = 00100000: digit[2]=1, weight=4 → sum=4 → check=7  not right
            // sum = 1 → need to craft it; try serial = 00000008: digit[7]=8, weight=7 → 56; 56%11=1 → check=10 → 0
            assertThat(service.validate("AB000000080GB")).isTrue();
        }
    }


    @Nested
    @DisplayName("Invalid barcodes")
    class InvalidBarcodes {

        @Test
        @DisplayName("should return false for the documented invalid example AA473124828GB")
        void documentedInvalidExample() {
            assertThat(service.validate("AA473124828GB")).isFalse();
        }

        @ParameterizedTest(name = "null or blank input: [{0}]")
        @NullSource
        @DisplayName("should return false for null")
        void nullInput(String barcode) {
            assertThat(service.validate(barcode)).isFalse();
        }

        @ParameterizedTest(name = "wrong length: [{0}]")
        @ValueSource(strings = {
                "",
                "AA4731248",          // too short
                "AA473124829GB1",     // too long
                "A473124829GB"        // 12 chars
        })
        @DisplayName("should return false for barcodes with incorrect length")
        void wrongLength(String barcode) {
            assertThat(service.validate(barcode)).isFalse();
        }

        @ParameterizedTest(name = "invalid prefix: [{0}]")
        @ValueSource(strings = {
                "1A473124829GB",   // digit in prefix
                "aA473124829GB",   // lowercase prefix
                "Aa473124829GB",   // mixed case prefix
                "11473124829GB"    // digits in prefix
        })
        @DisplayName("should return false for invalid prefix characters")
        void invalidPrefix(String barcode) {
            assertThat(service.validate(barcode)).isFalse();
        }

        @ParameterizedTest(name = "invalid serial: [{0}]")
        @ValueSource(strings = {
                "AAABCDEFG29GB",   // letters in serial
                "AA4731248 9GB"    // space in serial
        })
        @DisplayName("should return false for non-digit serial numbers")
        void invalidSerial(String barcode) {
            assertThat(service.validate(barcode)).isFalse();
        }

        @ParameterizedTest(name = "invalid country code: [{0}]")
        @ValueSource(strings = {
                "AA473124829US",   // US not accepted
                "AA473124829gb",   // lowercase
                "AA473124829  "    // spaces
        })
        @DisplayName("should return false for country codes other than GB")
        void invalidCountryCode(String barcode) {
            assertThat(service.validate(barcode)).isFalse();
        }

        @Test
        @DisplayName("should return false when check digit is off by one")
        void offByOneCheckDigit() {
            // Valid is AA473124829GB; check digit 9, try 8
            assertThat(service.validate("AA473124828GB")).isFalse();
            // try 0
            assertThat(service.validate("AA473124820GB")).isFalse();
        }
    }

    @Nested
    @DisplayName("Batch validation")
    class BatchValidation {

        @Test
        @DisplayName("should return one result per barcode in the same order")
        void validateBatchReturnsOrderedResults() {
            List<BatchValidateItemResponse> results = service.validateBatch(List.of(
                    "AA473124829GB",
                    "AA473124828GB",
                    "1A473124829GB"
            ));

            assertThat(results)
                    .extracting(BatchValidateItemResponse::getBarcode,
                            BatchValidateItemResponse::isValid)
                    .containsExactly(
                            org.assertj.core.groups.Tuple.tuple("AA473124829GB", true),
                            org.assertj.core.groups.Tuple.tuple("AA473124828GB", false),
                            org.assertj.core.groups.Tuple.tuple("1A473124829GB", false)
                    );
        }
    }
}
