package land.noah.smartcafe.model

data class Feedback(
    val orderId: Long,
    val customerId: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long
)