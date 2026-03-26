# S10 Barcode Validator Service

A Spring Boot REST API that validates Royal Mail **S10 1D barcodes**.

---

## Requirements

| Tool | Version |
|------|---------|
| Java | 17+     |
| Maven | 3.8+   |

---

## Running locally

### 1. Build and start the server

```bash
# From the project root directory:
./mvnw spring-boot:run
```

Or build a JAR and run it:

```bash
./mvnw clean package
java -jar target/barcode-validator-1.0.0.jar
```

The service starts on **http://localhost:8080**.

---

### 2. Call the API

The service exposes three endpoints:

1. `GET /validate?barcode=<barcode_value>`
2. `POST /validate`
3. `POST /validate/batch`

#### GET /validate

**Valid barcode example:**

```bash
curl "http://localhost:8080/validate?barcode=AA473124829GB"
```

```json
{"valid": true}
```

**Invalid barcode example (wrong check digit):**

```bash
curl "http://localhost:8080/validate?barcode=AA473124828GB"
```

```json
{"valid": false}
```

**Missing parameter:**

```bash
curl "http://localhost:8080/validate"
```

Returns `400 Bad Request` with structured error payload.

#### POST /validate

Use POST when sending JSON payloads.

```bash
curl -X POST "http://localhost:8080/validate" \
  -H "Content-Type: application/json" \
  -d '{"barcode": "AA473124829GB"}'
```

```json
{"valid": true}
```

Invalid request example:

```bash
curl -X POST "http://localhost:8080/validate" \
  -H "Content-Type: application/json" \
  -d '{"barcode": "   "}'
```

Returns `400 Bad Request` with `VALIDATION_ERROR`.

#### POST /validate/batch

Validate multiple barcodes in one request.

```bash
curl -X POST "http://localhost:8080/validate/batch" \
  -H "Content-Type: application/json" \
  -d '{"barcodes": ["AA473124829GB", "AA473124828GB", "1A473124829GB"]}'
```

```json
{
  "results": [
    {"barcode": "AA473124829GB", "valid": true},
    {"barcode": "AA473124828GB", "valid": false},
    {"barcode": "1A473124829GB", "valid": false}
  ]
}
```

Invalid request example (empty list):

```bash
curl -X POST "http://localhost:8080/validate/batch" \
  -H "Content-Type: application/json" \
  -d '{"barcodes": []}'
```

Returns `400 Bad Request` with `VALIDATION_ERROR`.

### Error response format

For `4xx` and `5xx` responses, the API returns a consistent JSON payload:

```json
{
  "message": "Validation failed: barcode: Barcode must not be blank",
  "errorCode": "VALIDATION_ERROR",
  "status": 400,
  "path": "/validate",
  "timestamp": "2026-03-25T17:00:00"
}
```

Common error codes:

- `VALIDATION_ERROR`
- `MISSING_PARAMETER`
- `INVALID_JSON`
- `INTERNAL_SERVER_ERROR`

---

## Running the tests

```bash
./mvnw test
```

---

## Barcode structure

An S10 barcode is exactly **13 characters** long:

```
A  A  4  7  3  1  2  4  8  2  9  G  B
^  ^  |----serial (8 digits)----|  ^  ^  ^
prefix                           check country
(2 uppercase letters)            digit code
```

| Part          | Position | Rules                                      |
|---------------|----------|--------------------------------------------|
| Prefix        | 1–2      | Two uppercase ASCII letters `A`–`Z`        |
| Serial number | 3–10     | Eight decimal digits `0`–`9` (leading zeros allowed) |
| Check digit   | 11       | Computed from serial (see below)            |
| Country code  | 12–13    | Must be `GB`                               |

### Check digit algorithm

1. Assign weights `8, 6, 4, 2, 3, 5, 9, 7` to serial digits left → right.
2. `sum = Σ(digit × weight)`
3. `check = 11 − (sum mod 11)`
4. If `check == 10` → use `0`; if `check == 11` → use `5`.

**Example** – serial `47312482`:

```
sum = 4×8 + 7×6 + 3×4 + 1×2 + 2×3 + 4×5 + 8×9 + 2×7
    = 32 + 42 + 12 + 2 + 6 + 20 + 72 + 14
    = 200
check = 11 − (200 mod 11) = 11 − 2 = 9  ✓
```

---
## Project structure

```
barcode-validator/
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/royalmail/barcode/
    │   ├── BarcodeValidatorApplication.java   ← Spring Boot entry point
    │   ├── controller/
    │   │   └── BarcodeController.java          ← GET /validate, POST /validate, POST /validate/batch
    │   ├── exception/
    │   │   └── GlobalExceptionHandler.java     ← centralized error handling
    │   ├── service/
    │   │   └── BarcodeValidatorService.java    ← single + batch validation logic
    │   └── model/
    │       ├── ValidateRequest.java
    │       ├── ValidateResponse.java
    │       ├── BatchValidateRequest.java
    │       ├── BatchValidateItemResponse.java
    │       ├── BatchValidateResponse.java
    │       └── ErrorResponse.java
    └── test/java/com/royalmail/barcode/
        ├── service/
        │   └── BarcodeValidatorServiceTest.java ← unit tests
        └── controller/
            ├── BarcodeControllerIntegrationTest.java ← full-stack endpoint tests
            └── BarcodeControllerServerErrorTest.java ← 500 error-path tests
```

