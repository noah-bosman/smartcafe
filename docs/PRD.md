# Smart Cafe PRD

- Version: 0.1
- Role: Individual Customer App
- Platform: Android
- Language: Kotlin

## 1. Problem & Value

Customers need a convenient way to browse a cafe menu, to place orders, and to track their orders 
from their Android device.

The application allows customers to order before collection and gives them a clear view of their 
current and previous orders.

## 2. User Roles

### Customer
The customer is the primary user of the application. Customers can register, log in to, browse the 
menu, manage their cart, place orders, view order status and history, and submit feedback.

### Simulated Admin behaviour

The full admin application is outside the scope of this individual project. Where necessary admin 
actions will be simulated so that the complete customer workflow can be demo'ed.

## 3. Scope

- Customer registration
- Customer login/logout
- Browse menu
- View product information
- Search and filter menu items
- Add products to cart
- Change cart quantities
- Remove products from cart
- Place orders
- View order confirmation
- View previous orders
- view current order status
- Receive order status notiications
- Submit ratings & feedback
- View basic customer order analytics
- Simulated admin order status updates

### Out of scope

- Full admin application
- Real payment processing
- Real customer data

## 4. Requirements

### Functional requirements

| ID | Requirement |
|---|---|
| CAFE-01 | The customer can register an account using an email address and password |
| CAFE-02 | The customer can log in and log out |
| CAFE-03 | The customer can browse available cafe products. |
| CAFE-04 | The customer can view product name, description, price and availability. |
| CAFE-05 | The customer can search and filter menu items. |
| CAFE-06 | The customer can add products to a shopping cart. |
| CAFE-07 | The customer can change quantities and remove products from the cart. |
| CAFE-08 | The customer can place an order and receive confirmation. |
| CAFE-09 | The customer can view previous orders and current order status. |
| CAFE-10 | The customer can submit ratings and feedback. |
| CAFE-11 | The customer can receive a notification when an order status changes |
| CAFE-12 | The application provides a customer facing analytics feature based on order data. |

### Non functional requirements

| ID | Requirement |
|---|---|
| NFR-01 | The Android application must be implemented in Kotlin. |
| NFR-02 | User input must be validated and invalid input must produce understandable feedback. |
| NFR-03 | Passwords must not be stored as plaintext in the local application database. |
| NFR-04 | Application data should persist between application restarts where appropriate. |
| NFR-05 | The Customer workflow should be usable on a typical Android phone screen. |

## 5. Data

The application is expected to require the following main data:

### Menu Item

- ID
- Name
- Description
- Category
- Price
- Availability

### Order

- ID
- Customer ID
- Date/time
- Total
- Status

### Order Item

- ID
- Order ID
- Menu item ID
- Quantity
- Price at time of ordering

### Feedback

- ID
- Order ID
- Rating
- Comment

## 6. Constraints

- The application must be developed for Android.
- Application code must be written in Kotlin.
- Jetpack Compose will be used for the user interface.
- The project is being developed individually.
- Only the Customer role will be fully implemented.
- Essential Admin behaviour will be simulated where required.
- Real payment processing will not be implemented.
- Fictional test data will be used.

## 7. Acceptance Criteria

### CAFE-03 Browse Menu

- Available café products are displayed to the customer.

### CAFE-04 Product Information

- Each product displays its name, description, price and availability.

### CAFE-05 Search and Filter

- Entering a search term filters the visible menu items.
- Selecting a category displays only matching items.

### CAFE-06 Add to Cart

- Selecting Add to Cart adds the chosen product to the customer's cart.

### CAFE-07 Update Cart

- The customer can increase or decrease quantity.
- The customer can remove an item.
- The displayed total updates correctly.

### CAFE-08 Place Order

- A non-empty cart can be submitted as an order.
- The customer receives an order confirmation.
- The completed order appears in order history.

### CAFE-09 Order History and Status 

- Previous orders are displayed.
- Each order displays its current status.

### CAFE-10 Feedback

- The customer can submit a valid rating and optional written feedback.
- Invalid ratings are rejected.

### CAFE-11 Notifications

- A status change produces an order-status notification.

### CAFE-12 Analytics

- The application calculates at least one useful statistic from stored customer order data.
- The calculated result is displayed to the customer.