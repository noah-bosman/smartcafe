package land.noah.smartcafe.model

data class OrderAnalytics(
    val totalOrders: Int,
    val totalSpend: Double,
    val averageOrderValue: Double,
    val mostOrderedProduct: String?
)