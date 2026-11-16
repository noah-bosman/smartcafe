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
import land.noah.smartcafe.model.MenuItem
import land.noah.smartcafe.model.Order
import land.noah.smartcafe.model.OrderStatus
import land.noah.smartcafe.ui.SmartCafeViewModel
import land.noah.smartcafe.ui.SmartCafeViewModelFactory
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

                val cafeViewModel: SmartCafeViewModel = viewModel(
                    factory = SmartCafeViewModelFactory(repository)
                )

                var isLoggedIn by remember {
                    mutableStateOf(auth.currentUser != null)
                }

                var selectedMenuItem by remember {
                    mutableStateOf<MenuItem?>(null)
                }

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        val currentUser = auth.currentUser

                        if (currentUser != null) {
                            cafeViewModel.loadCustomerData(
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

                fun placeOrder() {
                    val currentUser = auth.currentUser ?: return

                    val order = cafeViewModel.placeOrder(
                        customerId = currentUser.uid
                    ) ?: return

                    lastPlacedOrder = order
                    showingCart = false

                    coroutineScope.launch {
                        delay(5_000.milliseconds)

                        cafeViewModel.updateOrderStatus(
                            orderId = order.id,
                            newStatus = OrderStatus.PREPARING
                        )

                        showOrderStatusNotification(
                            orderId = order.id,
                            status = OrderStatus.PREPARING
                        )

                        delay(5_000.milliseconds)

                        cafeViewModel.updateOrderStatus(
                            orderId = order.id,
                            newStatus = OrderStatus.READY_FOR_COLLECTION
                        )

                        showOrderStatusNotification(
                            orderId = order.id,
                            status = OrderStatus.READY_FOR_COLLECTION
                        )
                    }
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
                                    val currentUserId = auth.currentUser?.uid

                                    if (currentUserId != null) {
                                        cafeViewModel.submitFeedback(
                                            customerId = currentUserId,
                                            orderId = order.id,
                                            rating = rating,
                                            comment = comment
                                        )
                                    }

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

                            val customerOrders = cafeViewModel.orders.filter { order ->
                                order.customerId == currentUserId
                            }

                            OrderHistoryScreen(
                                orders = customerOrders,
                                feedback = cafeViewModel.feedback.filter { item ->
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

                            if (currentUserId != null) {
                                AnalyticsScreen(
                                    analytics = cafeViewModel.calculateOrderAnalytics(
                                        currentUserId
                                    ),
                                    onBack = {
                                        showingAnalytics = false
                                    }
                                )
                            }
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