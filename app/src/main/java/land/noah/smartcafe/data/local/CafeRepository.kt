package land.noah.smartcafe.data

import land.noah.smartcafe.data.local.FeedbackDao
import land.noah.smartcafe.data.local.FeedbackEntity
import land.noah.smartcafe.data.local.OrderDao
import land.noah.smartcafe.data.local.OrderEntity
import land.noah.smartcafe.data.local.OrderItemEntity
import land.noah.smartcafe.model.Feedback
import land.noah.smartcafe.model.Order
import land.noah.smartcafe.model.OrderItem
import land.noah.smartcafe.model.OrderStatus

class CafeRepository(
    private val orderDao: OrderDao,
    private val feedbackDao: FeedbackDao
) {
    suspend fun saveOrder(order: Order) {
        val orderEntity = OrderEntity(
            id = order.id,
            customerId = order.customerId,
            total = order.total,
            status = order.status.name,
            createdAt = order.createdAt
        )

        val itemEntities = order.items.map { item ->
            OrderItemEntity(
                orderId = order.id,
                menuItemId = item.menuItemId,
                name = item.name,
                price = item.price,
                quantity = item.quantity
            )
        }

        orderDao.saveOrder(
            order = orderEntity,
            items = itemEntities
        )
    }

    suspend fun getOrdersForCustomer(
        customerId: String
    ): List<Order> {
        return orderDao
            .getOrdersForCustomer(customerId)
            .map { storedOrder ->
                Order(
                    id = storedOrder.order.id,
                    customerId = storedOrder.order.customerId,
                    items = storedOrder.items.map { item ->
                        OrderItem(
                            menuItemId = item.menuItemId,
                            name = item.name,
                            price = item.price,
                            quantity = item.quantity
                        )
                    },
                    total = storedOrder.order.total,
                    status = runCatching {
                        OrderStatus.valueOf(
                            storedOrder.order.status
                        )
                    }.getOrDefault(
                        OrderStatus.PENDING
                    ),
                    createdAt = storedOrder.order.createdAt
                )
            }
    }

    suspend fun updateOrderStatus(
        orderId: Long,
        status: OrderStatus
    ) {
        orderDao.updateOrderStatus(
            orderId = orderId,
            status = status.name
        )
    }

    suspend fun saveFeedback(
        feedback: Feedback
    ) {
        feedbackDao.upsertFeedback(
            FeedbackEntity(
                orderId = feedback.orderId,
                customerId = feedback.customerId,
                rating = feedback.rating,
                comment = feedback.comment,
                createdAt = feedback.createdAt
            )
        )
    }

    suspend fun getFeedbackForCustomer(
        customerId: String
    ): List<Feedback> {
        return feedbackDao
            .getFeedbackForCustomer(customerId)
            .map { item ->
                Feedback(
                    orderId = item.orderId,
                    customerId = item.customerId,
                    rating = item.rating,
                    comment = item.comment,
                    createdAt = item.createdAt
                )
            }
    }
}