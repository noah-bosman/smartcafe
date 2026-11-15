package land.noah.smartcafe.data

import land.noah.smartcafe.model.MenuItem

val sampleMenu = listOf( 
    MenuItem(
        id = 1,
        name = "Cappuccino",
        description = "Espresso with steamed milk and foam",
        category = "Coffee",
        price = 3.20,
        available = true
    ),
    MenuItem(
        id = 2,
        name = "Latte",
        description = "Espresso with steamed milk",
        category = "Coffee",
        price = 3.40,
        available = true
    ),
    MenuItem(
        id = 3,
        name = "Americano",
        description = "Espresso with hot water",
        category = "Coffee",
        price = 2.80,
        available = true
    ),
    MenuItem(
        id = 4,
        name = "English Breakfast Tea",
        description = "Traditional black tea",
        category = "Tea",
        price = 2.40,
        available = true
    ),
    MenuItem(
        id = 5,
        name = "Chocolate Cake",
        description = "Rich chocolate sponge cake",
        category = "Cake",
        price = 3.75,
        available = true
    ),
    MenuItem(
        id = 6,
        name = "Carrot Cake",
        description = "Spiced carrot cake with cream cheese frosting",
        category = "Cake",
        price = 3.75,
        available = false
    )
)