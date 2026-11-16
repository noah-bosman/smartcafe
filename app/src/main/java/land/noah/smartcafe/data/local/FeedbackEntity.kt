package land.noah.smartcafe.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "feedback",
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
    primaryKeys = ["orderId", "customerId"]
)
data class FeedbackEntity(
    val orderId: Long,
    val customerId: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long
)