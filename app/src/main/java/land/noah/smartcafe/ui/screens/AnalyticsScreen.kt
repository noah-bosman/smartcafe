package land.noah.smartcafe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import land.noah.smartcafe.model.OrderAnalytics

@Composable
fun AnalyticsScreen(
    analytics: OrderAnalytics,
    onBack: () -> Unit
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

        Text("Order Analytics")

        AnalyticsCard(
            title = "Total orders",
            value = analytics.totalOrders.toString()
        )

        AnalyticsCard(
            title = "Total spend",
            value = "£%.2f".format(analytics.totalSpend)
        )

        AnalyticsCard(
            title = "Average order value",
            value = "£%.2f".format(analytics.averageOrderValue)
        )

        AnalyticsCard(
            title = "Most ordered product",
            value = analytics.mostOrderedProduct ?: "No order data yet"
        )
    }
}

@Composable
private fun AnalyticsCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title)
            Text(value)
        }
    }
}