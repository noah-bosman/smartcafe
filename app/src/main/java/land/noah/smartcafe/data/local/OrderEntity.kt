package land.noah.smartcafe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: Long,
    val customerId: String,
    val total: Double,
    val status: String,
    val createdAt: Long
)