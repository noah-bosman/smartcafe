package land.noah.smartcafe.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import land.noah.smartcafe.data.CafeRepository
import land.noah.smartcafe.model.CartItem
import land.noah.smartcafe.model.Feedback
import land.noah.smartcafe.model.MenuItem
import land.noah.smartcafe.model.Order
import land.noah.smartcafe.model.OrderAnalytics
import land.noah.smartcafe.model.OrderItem
import land.noah.smartcafe.model.OrderStatus

class SmartCafeViewModel(
    private val repository: CafeRepository
) : ViewModel() {

    private var _cartItems by mutableStateOf<List<CartItem>>(emptyList())

    val cartItems: List<CartItem>
        get() = _cartItems

    private var _orders by mutableStateOf<List<Order>>(emptyList())

    val orders: List<Order>
        get() = _orders

    private var _feedback by mutableStateOf<List<Feedback>>(emptyList())

    val feedback: List<Feedback>
        get() = _feedback

    val cartItemCount: Int
        get() = cartItems.sumOf { item ->
            item.quantity
        }

    fun addToCart(menuItem: MenuItem) {
        if (!menuItem.available) {
            return
        }

        val existingItem = cartItems.find { cartItem ->
            cartItem.menuItem.id == menuItem.id
        }

        _cartItems = if (existingItem == null) {
            cartItems + CartItem(
                menuItem = menuItem,
                quantity = 1
            )
        } else {
            cartItems.map { cartItem ->
                if (cartItem.menuItem.id == menuItem.id) {
                    cartItem.copy(
                        quantity = cartItem.quantity + 1
                    )
                } else {
                    cartItem
                }
            }
        }
    }

    fun increaseCartQuantity(menuItemId: Int) {
        _cartItems = cartItems.map { cartItem ->
            if (cartItem.menuItem.id == menuItemId) {
                cartItem.copy(
                    quantity = cartItem.quantity + 1
                )
            } else {
                cartItem
            }
        }
    }

    fun decreaseCartQuantity(menuItemId: Int) {
        _cartItems = cartItems.mapNotNull { cartItem ->
            if (cartItem.menuItem.id == menuItemId) {
                val newQuantity = cartItem.quantity - 1

                if (newQuantity <= 0) {
                    null
                } else {
                    cartItem.copy(
                        quantity = newQuantity
                    )
                }
            } else {
                cartItem
            }
        }
    }

    fun removeFromCart(menuItemId: Int) {
        _cartItems = cartItems.filterNot { cartItem ->
            cartItem.menuItem.id == menuItemId
        }
    }

    fun clearCart() {
        _cartItems = emptyList()
    }

    fun loadCustomerData(customerId: String) {
        viewModelScope.launch {
            _orders = repository.getOrdersForCustomer(customerId)
            _feedback = repository.getFeedbackForCustomer(customerId)
        }
    }

    fun placeOrder(
        customerId: String
    ): Order? {
        if (_cartItems.isEmpty()) {
            return null
        }

        val createdAt = System.currentTimeMillis()

        val orderItems = _cartItems.map { cartItem ->
            OrderItem(
                menuItemId = cartItem.menuItem.id,
                name = cartItem.menuItem.name,
                price = cartItem.menuItem.price,
                quantity = cartItem.quantity
            )
        }

        val total = orderItems.sumOf { item ->
            item.subtotal
        }

        val order = Order(
            id = createdAt,
            customerId = customerId,
            items = orderItems,
            total = total,
            status = OrderStatus.PENDING,
            createdAt = createdAt
        )

        _orders = _orders + order
        clearCart()

        viewModelScope.launch {
            repository.saveOrder(order)
        }

        return order
    }

    fun updateOrderStatus(
        orderId: Long,
        newStatus: OrderStatus
    ) {
        _orders = _orders.map { order ->
            if (order.id == orderId) {
                order.copy(
                    status = newStatus
                )
            } else {
                order
            }
        }

        viewModelScope.launch {
            repository.updateOrderStatus(
                orderId = orderId,
                status = newStatus
            )
        }
    }

    fun submitFeedback(
        customerId: String,
        orderId: Long,
        rating: Int,
        comment: String
    ) {
        if (rating !in 1..5) {
            return
        }

        val newFeedback = Feedback(
            orderId = orderId,
            customerId = customerId,
            rating = rating,
            comment = comment.trim(),
            createdAt = System.currentTimeMillis()
        )

        _feedback = _feedback
            .filterNot { existingFeedback ->
                existingFeedback.orderId == orderId &&
                        existingFeedback.customerId == customerId
            } + newFeedback

        viewModelScope.launch {
            repository.saveFeedback(newFeedback)
        }
    }

    fun calculateOrderAnalytics(
        customerId: String
    ): OrderAnalytics {
        val customerOrders = _orders.filter { order ->
            order.customerId == customerId
        }

        val totalOrders = customerOrders.size

        val totalSpend = customerOrders.sumOf { order ->
            order.total
        }

        val averageOrderValue =
            if (totalOrders == 0) {
                0.0
            } else {
                totalSpend / totalOrders
            }

        val productQuantities = customerOrders
            .flatMap { order ->
                order.items
            }
            .groupBy { item ->
                item.name
            }
            .mapValues { (_, items) ->
                items.sumOf { item ->
                    item.quantity
                }
            }

        val mostOrderedProduct = productQuantities
            .maxByOrNull { (_, quantity) ->
                quantity
            }
            ?.key

        return OrderAnalytics(
            totalOrders = totalOrders,
            totalSpend = totalSpend,
            averageOrderValue = averageOrderValue,
            mostOrderedProduct = mostOrderedProduct
        )
    }
}