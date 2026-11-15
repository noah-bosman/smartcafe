# SmartCafe test evidence

## CAFE-01 Customer Registration

| Test ID | Test | Expected Result | Actual Result | Status |
|---|---|---|---|---|
| T-CAFE-01-01 | Submit registration with all fields empty | Email, password and confirmation validation errors are displayed and no authentication attempt is made | Validation errors were displayed for all required fields | Pass |
| T-CAFE-01-02 | Enter an invalid email, short password and mismatched confirmation | Appropriate validation errors are displayed | Invalid email, minimum password length and password mismatch errors were displayed | Pass |
| T-CAFE-01-03 | Register using a valid unused email and matching valid password | Firebase creates the account and the application displays confirmation | Account creation succeeded and the application displayed a success message | Pass |
| T-CAFE-01-04 | Register again using an email already registered in Firebase | Registration is rejected and an understandable duplicate-account error is displayed | Registration was rejected and the application displayed that an account already exists with the email | Pass |

## Evidence

- Manual testing performed using the emulator
- Firebase Authentication used as the authentication provider
- Successful account creation verified in the Firebase Authentication Users view