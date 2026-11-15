package land.noah.smartcafe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import land.noah.smartcafe.model.CartItem

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onBack: () -> Unit
) {
    val total = cartItems.sumOf { cartItem ->
        cartItem.subtotal
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 48.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(
            onClick = onBack
        ) {
            Text("Back")
        }

        Text("Your Cart")

        if (cartItems.isEmpty()) {
            Text("Your cart is empty")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = cartItems,
                    key = { cartItem -> cartItem.menuItem.id }
                ) { cartItem ->
                    CartItemCard(cartItem)
                }
            }

            Text(
                "Total: £%.2f".format(total)
            )
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(cartItem.menuItem.name)
            Text("Quantity: ${cartItem.quantity}")
            Text("£%.2f each".format(cartItem.menuItem.price))
            Text("Subtotal: £%.2f".format(cartItem.subtotal))
        }
    }
}