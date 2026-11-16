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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import land.noah.smartcafe.data.AuthRepository
import land.noah.smartcafe.data.CafeRepository
import land.noah.smartcafe.data.local.CafeDatabase
import land.noah.smartcafe.data.sampleMenu
import land.noah.smartcafe.model.MenuItem
import land.noah.smartcafe.model.Order
import land.noah.smartcafe.model.OrderStatus
import land.noah.smartcafe.ui.AuthViewModel
import land.noah.smartcafe.ui.AuthViewModelFactory
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
import land.noah.smartcafe.ui.screens.RegisterScreen
import land.noah.smartcafe.ui.theme.SmartCafeTheme
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            // No action required.
        }

    private val database by lazy {
        CafeDatabase.getInstance(applicationContext)
    }

    private val repository by lazy {
        CafeRepository(
            orderDao = database.orderDao(),
            feedbackDao = database.feedbackDao()
        )
    }

    private val authRepository by lazy {
        AuthRepository()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "order_status",
                "Order status",
                NotificationManager.IMPORTANCE_HIGH
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
            .setPriority(NotificationCompat.PRIORITY_HIGH)
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
                val cafeViewModel: SmartCafeViewModel = viewModel(
                    factory = SmartCafeViewModelFactory(repository)
                )

                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(authRepository)
                )

                var isLoggedIn by remember {
                    mutableStateOf(authViewModel.isLoggedIn)
                }

                var showingRegister by remember {
                    mutableStateOf(false)
                }

                var selectedMenuItem by remember {
                    mutableStateOf<MenuItem?>(null)
                }

                var lastPlacedOrder by remember {
                    mutableStateOf<Order?>(null)
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

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        val currentUserId =
                            authViewModel.currentUserId

                        if (currentUserId != null) {
                            cafeViewModel.loadCustomerData(
                                currentUserId
                            )
                        }
                    }
                }

                fun resetNavigationState() {
                    selectedMenuItem = null
                    lastPlacedOrder = null
                    showingCart = false
                    showingOrderHistory = false
                    feedbackOrder = null
                    showingAnalytics = false
                }

                fun placeOrder() {
                    val currentUserId =
                        authViewModel.currentUserId
                            ?: return

                    val order = cafeViewModel.placeOrder(
                        customerId = currentUserId
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
                            newStatus =
                                OrderStatus.READY_FOR_COLLECTION
                        )

                        showOrderStatusNotification(
                            orderId = order.id,
                            status =
                                OrderStatus.READY_FOR_COLLECTION
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
                                    val currentUserId =
                                        authViewModel.currentUserId

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
                                cartItems =
                                    cafeViewModel.cartItems,
                                onIncreaseQuantity = { menuItemId ->
                                    cafeViewModel
                                        .increaseCartQuantity(
                                            menuItemId
                                        )
                                },
                                onDecreaseQuantity = { menuItemId ->
                                    cafeViewModel
                                        .decreaseCartQuantity(
                                            menuItemId
                                        )
                                },
                                onRemoveItem = { menuItemId ->
                                    cafeViewModel
                                        .removeFromCart(
                                            menuItemId
                                        )
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
                            val currentUserId =
                                authViewModel.currentUserId

                            val customerOrders =
                                cafeViewModel.orders.filter { order ->
                                    order.customerId ==
                                            currentUserId
                                }

                            val customerFeedback =
                                cafeViewModel.feedback.filter { item ->
                                    item.customerId ==
                                            currentUserId
                                }

                            OrderHistoryScreen(
                                orders = customerOrders,
                                feedback = customerFeedback,
                                onLeaveFeedback = { order ->
                                    feedbackOrder = order
                                },
                                onBack = {
                                    showingOrderHistory = false
                                }
                            )
                        }

                        showingAnalytics -> {
                            val currentUserId =
                                authViewModel.currentUserId

                            if (currentUserId != null) {
                                AnalyticsScreen(
                                    analytics =
                                        cafeViewModel
                                            .calculateOrderAnalytics(
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
                                cartItemCount =
                                    cafeViewModel.cartItemCount,
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
                                    authViewModel.logout()

                                    resetNavigationState()

                                    showingRegister = false
                                    isLoggedIn = false
                                }
                            )
                        }
                    }
                } else {
                    if (showingRegister) {
                        RegisterScreen(
                            isLoading =
                                authViewModel.isLoading,
                            registrationError =
                                authViewModel.errorMessage,
                            onRegister = {
                                    email,
                                    password,
                                    onSuccess ->

                                authViewModel.register(
                                    email = email,
                                    password = password,
                                    onSuccess = {
                                        onSuccess()

                                        /*
                                         * Firebase signs in a newly
                                         * created account automatically,
                                         * so registration can enter the
                                         * authenticated application.
                                         */
                                        isLoggedIn = true
                                        showingRegister = false
                                    }
                                )
                            },
                            onBackToLogin = {
                                authViewModel.clearError()
                                showingRegister = false
                            }
                        )
                    } else {
                        LoginScreen(
                            isLoading =
                                authViewModel.isLoading,
                            loginError =
                                authViewModel.errorMessage,
                            onLogin = { email, password ->
                                authViewModel.login(
                                    email = email,
                                    password = password,
                                    onSuccess = {
                                        isLoggedIn = true
                                    }
                                )
                            },
                            onRegisterClick = {
                                authViewModel.clearError()
                                showingRegister = true
                            }
                        )
                    }
                }
            }
        }
    }
}