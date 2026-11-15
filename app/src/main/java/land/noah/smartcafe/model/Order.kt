package land.noah.smartcafe.model

data class Order(
    val id: Long,
    val customerId: String,
    val items: List<OrderItem>,
    val total: Double,
    val status: OrderStatus,
    val createdAt: Long
)