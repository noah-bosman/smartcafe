package land.noah.smartcafe

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import land.noah.smartcafe.data.CafeRepository
import land.noah.smartcafe.data.local.CafeDatabase
import land.noah.smartcafe.data.sampleMenu
import land.noah.smartcafe.model.Feedback
import land.noah.smartcafe.model.MenuItem
import land.noah.smartcafe.model.Order
import land.noah.smartcafe.model.OrderAnalytics
import land.noah.smartcafe.model.OrderItem
import land.noah.smartcafe.model.OrderStatus
import land.noah.smartcafe.ui.SmartCafeViewModel
import land.noah.smartcafe.ui.screens.AnalyticsScreen
import land.noah.smartcafe.ui.screens.CartScreen
import land.noah.smartcafe.ui.screens.FeedbackScreen
import land.noah.smartcafe.ui.screens.LoginScreen
import land.noah.smartcafe.ui.screens.MenuScreen
import land.noah.smartcafe.ui.screens.OrderConfirmationScreen
import land.noah.smartcafe.ui.screens.OrderHistoryScreen
import land.noah.smartcafe.ui.screens.ProductDetailScreen
import land.noah.smartcafe.ui.theme.SmartCafeTheme
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    private val database by lazy {
        CafeDatabase.getInstance(applicationContext)
    }

    private val repository by lazy {
        CafeRepository(
            orderDao = database.orderDao(),
            feedbackDao = database.feedbackDao()
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "order_status",
                "Order status",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Updates about café order status"
            }

            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    private fun showOrderStatusNotification(
        orderId: Long,
        status: OrderStatus
    ) {
        val message = when (status) {
            OrderStatus.PENDING ->
                "Your order has been received"

            OrderStatus.PREPARING ->
                "Your order is now being prepared"

            OrderStatus.READY_FOR_COLLECTION ->
                "Your order is ready for collection"

            OrderStatus.COLLECTED ->
                "Your order has been collected"
        }

        val notification = NotificationCompat.Builder(
            this,
            "order_status"
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Smart Café")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat
            .from(this)
            .notify(
                (orderId + status.ordinal).hashCode(),
                notification
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        requestNotificationPermission()
        enableEdgeToEdge()
        setContent {
            SmartCafeTheme {
                val auth = FirebaseAuth.getInstance()

                val cafeViewModel: SmartCafeViewModel = viewModel()

                var isLoggedIn by remember {
                    mutableStateOf(auth.currentUser != null)
                }

                var selectedMenuItem by remember {
                    mutableStateOf<MenuItem?>(null)
                }

                var orders by remember {
                    mutableStateOf<List<Order>>(emptyList())
                }

                var feedback by remember {
                    mutableStateOf<List<Feedback>>(emptyList())
                }

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        val currentUser = auth.currentUser

                        if (currentUser != null) {
                            orders = repository.getOrdersForCustomer(
                                currentUser.uid
                            )

                            feedback = repository.getFeedbackForCustomer(
                                currentUser.uid
                            )
                        }
                    }
                }

                var lastPlacedOrder by remember {
                    mutableStateOf<Order?>(null)
                }

                val cartItemCount = cafeViewModel.cartItems.sumOf { cartItem ->
                    cartItem.quantity
                }

                var showingCart by remember {
                    mutableStateOf(false)
                }

                var showingOrderHistory by remember {
                    mutableStateOf(false)
                }

                var feedbackOrder by remember {
                    mutableStateOf<Order?>(null)
                }

                var showingAnalytics by remember {
                    mutableStateOf(false)
                }

                val coroutineScope = rememberCoroutineScope()

                fun updateOrderStatus(
                    orderId: Long,
                    newStatus: OrderStatus
                ) {
                    orders = orders.map { order ->
                        if (order.id == orderId) {
                            order.copy(
                                status = newStatus
                            )
                        } else {
                            order
                        }
                    }

                    coroutineScope.launch {
                        repository.updateOrderStatus(
                            orderId = orderId,
                            status = newStatus
                        )
                    }

                    showOrderStatusNotification(
                        orderId = orderId,
                        status = newStatus
                    )
                }

                fun submitFeedback(
                    orderId: Long,
                    rating: Int,
                    comment: String
                ) {
                    val currentUser = auth.currentUser ?: return

                    if (rating !in 1..5) {
                        return
                    }

                    val newFeedback = Feedback(
                        orderId = orderId,
                        customerId = currentUser.uid,
                        rating = rating,
                        comment = comment.trim(),
                        createdAt = System.currentTimeMillis()
                    )

                    feedback = feedback
                        .filterNot { existingFeedback ->
                            existingFeedback.orderId == orderId &&
                                    existingFeedback.customerId == currentUser.uid
                        } + newFeedback

                    coroutineScope.launch {
                        repository.saveFeedback(newFeedback)
                    }
                }

                fun placeOrder() {
                    if (cafeViewModel.cartItems.isEmpty()) {
                        return
                    }

                    val currentUser = auth.currentUser ?: return

                    val createdAt = System.currentTimeMillis()

                    val orderItems = cafeViewModel.cartItems.map { cartItem ->
                        OrderItem(
                            menuItemId = cartItem.menuItem.id,
                            name = cartItem.menuItem.name,
                            price = cartItem.menuItem.price,
                            quantity = cartItem.quantity
                        )
                    }

                    val total = orderItems.sumOf { orderItem ->
                        orderItem.subtotal
                    }

                    val order = Order(
                        id = createdAt,
                        customerId = currentUser.uid,
                        items = orderItems,
                        total = total,
                        status = OrderStatus.PENDING,
                        createdAt = createdAt
                    )

                    orders = orders + order
                    lastPlacedOrder = order
                    cafeViewModel.cartItems = emptyList()
                    showingCart = false

                    coroutineScope.launch {
                        repository.saveOrder(order)
                    }

                    // simulate order statuses
                    coroutineScope.launch {
                        delay(5_000.milliseconds)

                        updateOrderStatus(
                            orderId = order.id,
                            newStatus = OrderStatus.PREPARING
                        )

                        delay(5_000.milliseconds)

                        updateOrderStatus(
                            orderId = order.id,
                            newStatus = OrderStatus.READY_FOR_COLLECTION
                        )
                    }
                }

                fun calculateOrderAnalytics(
                    orders: List<Order>
                ): OrderAnalytics {
                    val totalOrders = orders.size

                    val totalSpend = orders.sumOf { order ->
                        order.total
                    }

                    val averageOrderValue =
                        if (totalOrders == 0) {
                            0.0
                        } else {
                            totalSpend / totalOrders
                        }

                    val productQuantities = orders
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

                if (isLoggedIn) {
                    val selectedItem = selectedMenuItem
                    val confirmedOrder = lastPlacedOrder

                    when {
                        confirmedOrder != null -> {
                            OrderConfirmationScreen(
                                order = confirmedOrder,
                                onContinue = {
                                    lastPlacedOrder = null
                                }
                            )
                        }

                        selectedItem != null -> {
                            ProductDetailScreen(
                                item = selectedItem,
                                onBack = {
                                    selectedMenuItem = null
                                },
                                onAddToCart = { item ->
                                    cafeViewModel.addToCart(item)
                                }
                            )
                        }

                        feedbackOrder != null -> {
                            val order = feedbackOrder!!

                            FeedbackScreen(
                                order = order,
                                onSubmit = { rating, comment ->
                                    submitFeedback(
                                        orderId = order.id,
                                        rating = rating,
                                        comment = comment
                                    )

                                    feedbackOrder = null
                                },
                                onBack = {
                                    feedbackOrder = null
                                }
                            )
                        }

                        showingCart -> {
                            CartScreen(
                                cartItems = cafeViewModel.cartItems,
                                onIncreaseQuantity = { menuItemId ->
                                    cafeViewModel.increaseCartQuantity(menuItemId)
                                },
                                onDecreaseQuantity = { menuItemId ->
                                    cafeViewModel.decreaseCartQuantity(menuItemId)
                                },
                                onRemoveItem = { menuItemId ->
                                    cafeViewModel.removeFromCart(menuItemId)
                                },
                                onPlaceOrder = {
                                    placeOrder()
                                },
                                onBack = {
                                    showingCart = false
                                }
                            )
                        }

                        showingOrderHistory -> {
                            val currentUserId = auth.currentUser?.uid

                            val customerOrders = orders.filter { order ->
                                order.customerId == currentUserId
                            }

                            OrderHistoryScreen(
                                orders = customerOrders,
                                feedback = feedback.filter { item ->
                                    item.customerId == currentUserId
                                },
                                onLeaveFeedback = { order ->
                                    feedbackOrder = order
                                },
                                onBack = {
                                    showingOrderHistory = false
                                }
                            )
                        }

                        showingAnalytics -> {
                            val currentUserId = auth.currentUser?.uid

                            val customerOrders = orders.filter { order ->
                                order.customerId == currentUserId
                            }

                            val analytics = calculateOrderAnalytics(customerOrders)

                            AnalyticsScreen(
                                analytics = analytics,
                                onBack = {
                                    showingAnalytics = false
                                }
                            )
                        }

                        else -> {
                            MenuScreen(
                                menuItems = sampleMenu,
                                cartItemCount = cartItemCount,
                                onItemClick = { item ->
                                    selectedMenuItem = item
                                },
                                onAnalyticsClick = {
                                    showingAnalytics = true
                                },
                                onCartClick = {
                                    showingCart = true
                                },
                                onOrdersClick = {
                                    showingOrderHistory = true
                                },
                                onLogout = {
                                    auth.signOut()
                                    selectedMenuItem = null
                                    showingCart = false
                                    isLoggedIn = false
                                    showingAnalytics = false
                                }
                            )
                        }
                    }
                } else {
                    LoginScreen(
                        onLoginSuccess = {
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    }
}