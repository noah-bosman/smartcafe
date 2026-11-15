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
import land.noah.smartcafe.model.Feedback
import land.noah.smartcafe.model.Order

@Composable
fun OrderHistoryScreen(
    orders: List<Order>,
    feedback: List<Feedback>,
    onLeaveFeedback: (Order) -> Unit,
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

        Text("Order History")

        if (orders.isEmpty()) {
            Text("No previous orders")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = orders.sortedByDescending { order ->
                        order.createdAt
                    },
                    key = { order ->
                        order.id
                    }
                ) { order ->
                    val orderFeedback = feedback.find { item ->
                        item.orderId == order.id
                    }

                    OrderHistoryCard(
                        order = order,
                        feedback = orderFeedback,
                        onLeaveFeedback = {
                            onLeaveFeedback(order)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(
    order: Order,
    feedback: Feedback?,
    onLeaveFeedback: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Order #${order.id}")

            Text(
                "Items: ${order.items.sumOf { item ->
                    item.quantity
                }}"
            )

            Text("Total: £%.2f".format(order.total))

            Text("Status: ${order.status}")
        }

        if (feedback != null) {
            Text("Rating: ${feedback.rating}/5")

            if (feedback.comment.isNotBlank()) {
                Text("Feedback: ${feedback.comment}")
            }
        }

        TextButton(
            onClick = onLeaveFeedback
        ) {
            Text("Leave feedback")
        }
    }
}