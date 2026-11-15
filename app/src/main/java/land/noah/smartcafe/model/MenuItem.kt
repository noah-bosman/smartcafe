package land.noah.smartcafe.model

data class MenuItem(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val available: Boolean
)