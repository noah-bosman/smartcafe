package land.noah.smartcafe.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import land.noah.smartcafe.model.MenuItem

@Composable
fun MenuScreen(
    menuItems: List<MenuItem>,
    onItemClick: (MenuItem) -> Unit,
    onLogout: () -> Unit,
    cartItemCount: Int,
    onCartClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
) {
    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("All")
    }

    val categories = listOf(
        "All",
        "Coffee",
        "Tea",
        "Cake"
    )

    val filteredItems = menuItems.filter { item ->
        val matchesSearch =
            item.name.contains(
                searchQuery,
                ignoreCase = true
            )

        val matchesCategory =
            selectedCategory == "All" ||
                    item.category == selectedCategory

        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 48.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Smart Café")

            TextButton(
                onClick = onCartClick
            ) {
                Text("Cart: $cartItemCount")
            }

            TextButton(
                onClick = onOrdersClick
            ) {
                Text("Orders")
            }

            TextButton(
                onClick = onAnalyticsClick
            ) {
                Text("Analytics")
            }

            TextButton(
                onClick = onLogout
            ) {
                Text("Log out")
            }
        }

        Text(
            text = "Menu",
            modifier = Modifier.padding(vertical = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newValue ->
                searchQuery = newValue
            },
            label = {
                Text("Search menu")
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                TextButton(
                    onClick = {
                        selectedCategory = category
                    }
                ) {
                    Text(
                        if (selectedCategory == category) {
                            "[$category]" // TODO better indicator?
                        } else {
                            category
                        }
                    )
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = filteredItems,
                key = { item -> item.id }
            ) { item ->
                MenuItemCard(
                    item = item,
                    onClick = {
                        onItemClick(item)
                    }
                )
            }
        }
    }
}

@Composable
private fun MenuItemCard(
    item: MenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(item.name)
            Text(item.category)
            Text("£%.2f".format(item.price))

            Text(
                if (item.available) {
                    "Available"
                } else {
                    "Unavailable"
                }
            )
        }
    }
}