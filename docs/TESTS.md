# SmartCafe test evidence

## CAFE-01 Customer Registration

| Test ID | Test | Expected Result | Actual Result | Status |
|---|---|---|---|---|
| T-CAFE-01-01 | Submit registration with all fields empty | Email, password and confirmation validation errors are displayed and no authentication attempt is made | Validation errors were displayed for all required fields | Pass |
| T-CAFE-01-02 | Enter an invalid email, short password and mismatched confirmation | Appropriate validation errors are displayed | Invalid email, minimum password length and password mismatch errors were displayed | Pass |
| T-CAFE-01-03 | Register using a valid unused email and matching valid password | Firebase creates the account and the application displays confirmation | Account creation succeeded and the application displayed a success message | Pass |
| T-CAFE-01-04 | Register again using an email already registered in Firebase | Registration is rejected and an understandable duplicate-account error is displayed | Registration was rejected and the application displayed that an account already exists with the email | Pass |

### Evidence

- Manual testing performed using the emulator
- Firebase Authentication used as the authentication provider
- Successful account creation verified in the Firebase Authentication Users view

## CAFE-02 Customer Login and Logout

| Test ID | Test | Expected Result | Actual Result | Status |
|---|---|---|---|---|
| T-CAFE-02-01 | Submit login with email and password fields empty | Required field validation errors are displayed and no authentication attempt is made | Email and password validation errors were displayed | Pass |
| T-CAFE-02-02 | Submit login using a valid registered email with an incorrect password | Authentication is rejected, an understandable error is displayed, and the customer remains on the login screen | Firebase rejected the credentials, an invalid email/password message was displayed, and the login screen remained visible | Pass |
| T-CAFE-02-03 | Submit login using valid registered credentials | Authentication succeeds and the customer is shown the authenticated Customer screen | Firebase authentication succeeded and the Customer screen was displayed | Pass |
| T-CAFE-02-04 | Close and reopen the application while authenticated | The existing Firebase session is recognised and the authenticated Customer screen is displayed | The authenticated session persisted and the Customer screen was displayed after relaunch | Pass |
| T-CAFE-02-05 | Select Log out while authenticated | Firebase session ends and the customer returns to the login screen | The session ended and the login screen was displayed | Pass |
| T-CAFE-02-06 | Close and reopen the application after logging out | No authenticated session exists and the login screen is displayed | The application opened on the login screen | Pass |

### Evidence

- Manual testing performed using the emulator
- Firebase Authentication used for email/password authentication and session management
- Invalid credentials do not provide access to the authenticated Customer screen

## CAFE-03 Browse Cafe Menu

| Test ID | Test | Expected Result | Actual Result | Status |
|---|---|---|---|---|
| T-CAFE-03-01 | Log in and open the Customer application | The café menu is displayed | The menu screen was displayed after successful login | Pass |
| T-CAFE-03-02 | View the available menu items | All configured café products are displayed | All six configured products were displayed | Pass |
| T-CAFE-03-03 | Scroll through the menu | The customer can browse all menu items | The menu scrolled correctly and all items were accessible | Pass |
| T-CAFE-03-04 | View an available product | The product displays its name, category, price and available status | The configured product information and Available status were displayed | Pass |
| T-CAFE-03-05 | View an unavailable product | The product remains visible but is clearly marked unavailable | Carrot Cake was displayed with an Unavailable status | Pass |
| T-CAFE-03-06 | Select Log out from the menu screen | The authenticated session ends and the login screen is displayed | Logout succeeded and the login screen was displayed | Pass |

### Evidence

- Manual testing performed using the emulator
- Menu data currently comes from the in-memory `sampleMenu` data source!
- Each menu item has a stable ID, name, description, category, price and availability state

## CAFE-04 View Product Information

| Test ID | Test | Expected Result | Actual Result | Status |
|---|---|---|---|---|
| T-CAFE-04-01 | Select an available product from the menu | A product detail screen opens for the selected product | The selected product detail screen was displayed | Pass |
| T-CAFE-04-02 | View Cappuccino product details | Name, category, description, price and availability are displayed correctly | Cappuccino details displayed correctly, including £3.20 and Available status | Pass |
| T-CAFE-04-03 | View an unavailable product | Product information remains visible and the unavailable state is clearly displayed | Carrot Cake details displayed correctly with Unavailable status | Pass |
| T-CAFE-04-04 | Select Back from the product detail screen | The customer returns to the café menu | The menu screen was displayed again | Pass |
| T-CAFE-04-05 | Open different products consecutively | The detail screen displays information for the product that was selected | Each selected product displayed its corresponding information | Pass |

### Evidence

- Manual testing performed using the emulator
- Product details are supplied from the selected `MenuItem`
- Product selection is represented using application UI state

## CAFE-05 Search and Filter Menu

| Test ID | Test | Expected Result | Actual Result | Status |
|---|---|---|---|---|
| T-CAFE-05-01 | Search for `lat` | Only menu items whose names match the search text are displayed | Latte was displayed and non-matching items were hidden | Pass |
| T-CAFE-05-02 | Search using different letter case such as `CAP` | Search behaves case-insensitively | Cappuccino was displayed correctly | Pass |
| T-CAFE-05-03 | Select the Coffee category | Only Coffee products are displayed | Cappuccino, Latte and Americano were displayed | Pass |
| T-CAFE-05-04 | Select the Cake category | Only Cake products are displayed | Chocolate Cake and Carrot Cake were displayed | Pass |
| T-CAFE-05-05 | Apply a search and category filter together | Only products matching both conditions are displayed | Combined filtering behaved correctly | Pass |
| T-CAFE-05-06 | Apply filters that match no products | No unrelated products are displayed | The menu displayed no products for the unmatched criteria | Pass |
| T-CAFE-05-07 | Clear the search and select All | The full café menu is restored | All six menu items were displayed again | Pass |

### Evidence

- Manual testing performed using the emulator
- Search is performed against product names using a case-insensitive comparison
- Category filtering supports All, Coffee, Tea and Cake
- Search and category filtering can operate together

## CAFE-06 Add Products to Cart

| Test ID | Test | Expected Result | Actual Result | Status |
|---|---|---|---|---|
| T-CAFE-06-01 | Add an available Latte to the cart once | Cart quantity increases by one | Cart count changed to 1 | Pass |
| T-CAFE-06-02 | Add the same Latte to the cart again | Existing cart item quantity increases rather than creating a duplicate entry | Cart count changed to 2 | Pass |
| T-CAFE-06-03 | Add a different available product | New product is added and total cart quantity increases | Cappuccino was added and cart count changed to 3 | Pass |
| T-CAFE-06-04 | Open an unavailable product | Customer cannot add the unavailable product to the cart | Carrot Cake displayed a disabled add-to-cart control | Pass |
| T-CAFE-06-05 | Navigate between menu and product detail screens after adding products | Cart state remains available during normal application navigation | Cart count remained unchanged while navigating between screens | Pass |

### Evidence

- Manual testing performed using the emulator
- Cart state is stored in application memory using a list of `CartItem` objects
- Adding an existing product increases its quantity
- Unavailable menu items are prevented from being added to the cart
- The menu displays the total quantity of products currently in the cart

## CAFE-07 Manage Shopping Cart

| Test ID | Test | Expected Result | Actual Result | Status |
|---|---|---|---|---|
| T-CAFE-07-01 | Open the cart with no products added | Empty-cart state is displayed | `Your cart is empty` was displayed | Pass |
| T-CAFE-07-02 | Add Latte twice and Cappuccino once, then open the cart | Both products are displayed with the correct quantities, subtotals and total | Latte ×2 and Cappuccino ×1 were displayed with a £10.00 total | Pass |
| T-CAFE-07-03 | Increase Cappuccino quantity from 1 to 2 | Quantity, subtotal and cart total update correctly | Cappuccino changed to quantity 2, subtotal £6.40 and total £13.20 | Pass |
| T-CAFE-07-04 | Decrease Latte quantity from 2 to 1 | Quantity, subtotal and cart total update correctly | Latte changed to quantity 1, subtotal £3.40 and total £9.80 | Pass |
| T-CAFE-07-05 | Remove Cappuccino from the cart | Cappuccino is removed and the total is recalculated | Cappuccino was removed and the remaining total was £3.40 | Pass |
| T-CAFE-07-06 | Decrease an item with quantity 1 | The item is removed instead of allowing quantity zero | Latte was removed and the cart became empty | Pass |
| T-CAFE-07-07 | Return to the menu after emptying the cart | Menu cart indicator reflects the updated cart state | Cart indicator displayed `Cart: 0` | Pass |

### Evidence

- Manual testing performed using the emulator
- Cart contents are represented as `CartItem` objects with quantity
- Subtotals are derived from product price * quantity
- Increasing, decreasing and removing items updates Compose state
- Decreasing an item from quantity 1 removes it from the cart