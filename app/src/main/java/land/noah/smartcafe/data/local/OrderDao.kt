package land.noah.smartcafe.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
interface OrderDao {

    @Upsert
    suspend fun upsertOrder(order: OrderEntity)

    @Upsert
    suspend fun upsertOrderItems(items: List<OrderItemEntity>)

    @Transaction
    suspend fun saveOrder(
        order: OrderEntity,
        items: List<OrderItemEntity>
    ) {
        upsertOrder(order)
        upsertOrderItems(items)
    }

    @Transaction
    @Query(
        """
        SELECT * FROM orders
        WHERE customerId = :customerId
        ORDER BY createdAt DESC
        """
    )
    suspend fun getOrdersForCustomer(
        customerId: String
    ): List<OrderWithItems>

    @Query(
        """
        UPDATE orders
        SET status = :status
        WHERE id = :orderId
        """
    )
    suspend fun updateOrderStatus(
        orderId: Long,
        status: String
    )
}