# Smart Café

This is an Android cafe ordering app developed in Kotlin for the Mobile Application Development & Big
Data coursework.

It allows customers to register, authenticate, browse and search the menu, manage their cart,
place orders, track order statuses, receive push notifications if their order status changes,
submit ratings & feedback, and view analytics based on their order history.

## Tech

- Kotlin
- Android Studio
- Jetpack Compose
- Firebase Auth
- Room (SQLite)

## Menu data

The menu uses fictional sample data hard-coded into the app

Menu items contain:

```text
id
name
description
category
price
availability
```

## Simulation

This is an individual Customer implementation, so all admin behaviour, such as actually processing 
orders, is completely simulated

## Persistence

Room is used to persist:

- Orders
- Order items
- Order status
- Customer feedback

## Firebase setup

Firebase Authentication is used for customer registration and login.

The Android application must be registered in Firebase using this package name:

```text
land.noah.smartcafe
```

Email/password authentication must be enabled in:

```text
Firebase Console
> Authentication
> Sign-in method
> Email/Password
```

The Firebase Android configuration file must be located at:

```text
app/google-services.json
```

## Building the project

### Requirements

Install:

- Android Studio
- Android SDK
- An Android emulator

Clone the repository and enter the project directory:

```bash
git clone git@github.com:noah-bosman/smartcafe.git SmartCafe
cd SmartCafe
```

Build the debug application:

```bash
./gradlew assembleDebug
````

## Demonstration workflow

A complete demonstration can follow this sequence:

1. Register a customer account or log in;
2. Browse the menu;
3. Search for or filter products;
4. Open a product and view its information;
5. Add available products to the cart;
6. Change quantities or remove an item;
7. Place the order;
8. View the order confirmation;
9. Open Order History;
10. Observe the simulated status change to `PREPARING`;
11. Observe the Android status notification;
12. Observe the change to `READY_FOR_COLLECTION`;
13. Submit a rating and optional written feedback;
14. Restart the application and confirm that orders and feedback remain available;
15. Open Analytics and review the customer metrics;
16. Log out and confirm that protected customer content is no longer displayed

## Testing

Manual acceptance testing is documented in:

```text
docs/TESTS.md
```

Tests cover the requirements identified as:

```text
CAFE-01
through
CAFE-12
```

## Requirements and development evidence

Project requirements are documented in:

```text
docs/PRD.md
```

The implementation backlog is documented in:

```text
docs/BACKLOG.md
```

Test evidence is documented in:

```text
docs/TESTS.md
```

Development was performed incrementally using GitHub Issues, feature branches, commits and pull 
requests