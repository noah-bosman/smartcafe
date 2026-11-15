package land.noah.smartcafe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
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
import land.noah.smartcafe.model.Order

@Composable
fun FeedbackScreen(
    order: Order,
    onSubmit: (Int, String) -> Unit,
    onBack: () -> Unit
) {
    var rating by remember {
        mutableStateOf(0)
    }

    var comment by remember {
        mutableStateOf("")
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

        Text("Leave Feedback")

        Text("Order #${order.id}")

        Text("Rating")

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (1..5).forEach { value ->
                TextButton(
                    onClick = {
                        rating = value
                    }
                ) {
                    Text(
                        if (rating == value) {
                            "[$value]"
                        } else {
                            value.toString()
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = comment,
            onValueChange = { newValue ->
                comment = newValue
            },
            label = {
                Text("Comment (optional)")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onSubmit(rating, comment)
            },
            enabled = rating in 1..5,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit feedback")
        }
    }
}