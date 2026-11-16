package land.noah.smartcafe.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("orderId")
    ],
    primaryKeys = ["orderId", "menuItemId"]
)
data class OrderItemEntity(
    val orderId: Long,
    val menuItemId: Int,
    val name: String,
    val price: Double,
    val quantity: Int
)