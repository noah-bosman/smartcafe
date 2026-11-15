package land.noah.smartcafe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import land.noah.smartcafe.model.Order

@Composable
fun OrderConfirmationScreen(
    order: Order,
    onContinue: () -> Unit
) {
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
        Text("Order confirmed")

        Text("Order #${order.id}")

        Text("Items: ${order.items.sumOf { it.quantity }}")

        Text("Total: £%.2f".format(order.total))

        Text("Status: ${order.status}")

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to menu")
        }
    }
}