# Smart Cafe Product Backlog

- Role: Individual Customer Application

## Status values

- To Do
- In Progress
- Done
- Partial

## CAFE-01 Customer Registration

- Role: Customer  
- Priority: Must  
- Owner: Noah  
- Status: Done

### User Story

As a customer, I want to register an account using my email address and password so that I can use 
the ordering application.

### Acceptance Criteria

- Valid registration details are accepted.
- Empty or invalid fields display an appropriate error.
- Password confirmation must match the entered password.

## CAFE-02 Customer Login and Logout

- Role: Customer  
- Priority: Must  
- Owner: Noah  
- Status: Done

### User Story

As a customer, I want to log in to my account so that I can access my orders and cafe features.

### Acceptance Criteria

- Valid credentials allow access to the Customer application.
- Invalid credentials do not grant access.
- Empty fields produce a validation error.
- Logging out returns the customer to the login screen.

## CAFE-03 Browse Menu

- Role: Customer  
- Priority: Must  
- Owner: Noah  
- Status: To Do

### User Story

As a customer, I want to browse the cafe menu so that I can decide what to order.

### Acceptance Criteria

- Available cafe products are displayed.

## CAFE-04 View Product Information

- Role: Customer  
- Priority: Must  
- Owner: Noah  
- Status: To Do

### User Story

As a customer, I want to view product details so that I can make an informed choice.

### Acceptance Criteria

- Each product displays its name, description, price and availability.
## CAFE-05 Search and Filter Menu

- Role: Customer  
- Priority: Should  
- Owner: Noah  
- Status: To Do

### User Story

As a customer, I want to search and filter the menu so that I can find products quickly.

### Acceptance Criteria

- A search term filters visible menu items.
- A selected category displays matching products.

## CAFE-06 Add Products to Cart

- Role: Customer  
- Priority: Must  
- Owner: Noah  
- Status: To Do

### User Story

As a customer, I want to add products to a cart so that I can prepare an order.

### Acceptance Criteria

- Selecting Add to Cart adds the chosen product to the cart.

## CAFE-07 Manage Cart

- Role: Customer  
- Priority: Must  
- Owner: Noah  
- Status: To Do

### User Story

As a customer, I want to change quantities and remove items so that I can correct my order before 
submitting it.

### Acceptance Criteria

- Item quantities can be increased or decreased.
- Items can be removed.
- The displayed total updates correctly.

## CAFE-08 Place Order

- Role: Customer  
- Priority: Must  
- Owner: Noah  
- Status: To Do

### User Story

As a customer, I want to submit my cart as an order so that the cafe can prepare it.

### Acceptance Criteria

- A non-empty cart can be submitted.
- An order confirmation is displayed.
- The submitted order is stored.

## CAFE-09 Order History and Status

- Role: Customer  
- Priority: Must  
- Owner: Noah  
- Status: To Do

### User Story

As a customer, I want to view my previous orders and their status so that I know the progress of my 
order.

### Acceptance Criteria

- Previous orders are displayed.
- Each order displays its current status.

## CAFE-10 Ratings and Feedback

- Role: Customer  
- Priority: Should  
- Owner: Noah  
- Status: To Do

### User Story

As a customer, I want to leave a rating and feedback so that I can share my experience.

### Acceptance Criteria

- A valid rating can be submitted.
- Optional written feedback can be submitted.
- Invalid ratings are rejected.

## CAFE-11 Order Status Notifications

- Role: Customer  
- Priority: Should  
- Owner: Noah  
- Status: To Do

### User Story

As a customer, I want to be notified when my order status changes so that I know when it is ready.

### Acceptance Criteria

- A change in order status results in a customer notification.

## CAFE-12 Customer Order Analytics

- Role: Customer  
- Priority: Could  
- Owner: Noah  
- Status: To Do

### User Story

As a customer, I want to see useful statistics about my orders so that I can understand my 
purchasing history.

### Acceptance Criteria

- At least one statistic is calculated from stored order data.
- The calculated result is displayed in the application.