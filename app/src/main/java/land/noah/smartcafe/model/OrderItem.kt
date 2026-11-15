package land.noah.smartcafe.model

data class OrderItem(
    val menuItemId: Int,
    val name: String,
    val price: Double,
    val quantity: Int
) {
    val subtotal: Double
        get() = price * quantity
}