package land.noah.smartcafe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import land.noah.smartcafe.model.MenuItem

@Composable
fun ProductDetailScreen(
    item: MenuItem,
    onBack: () -> Unit,
    onAddToCart: (MenuItem) -> Unit
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
        TextButton(
            onClick = onBack
        ) {
            Text("Back")
        }

        Text(item.name)

        Text(item.category)

        Text(item.description)

        Text("£%.2f".format(item.price))

        Text(
            if (item.available) {
                "Available"
            } else {
                "Unavailable"
            }
        )

        Button(
            onClick = {
                onAddToCart(item)
            },
            enabled = item.available,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (item.available) {
                    "Add to cart"
                } else {
                    "Unavailable"
                }
            )
        }
    }
}