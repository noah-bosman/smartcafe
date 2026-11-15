package land.noah.smartcafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import land.noah.smartcafe.data.sampleMenu
import land.noah.smartcafe.model.CartItem
import land.noah.smartcafe.model.MenuItem
import land.noah.smartcafe.model.Order
import land.noah.smartcafe.model.OrderItem
import land.noah.smartcafe.model.OrderStatus
import land.noah.smartcafe.ui.screens.CartScreen
import land.noah.smartcafe.ui.screens.LoginScreen
import land.noah.smartcafe.ui.screens.MenuScreen
import land.noah.smartcafe.ui.screens.OrderConfirmationScreen
import land.noah.smartcafe.ui.screens.OrderHistoryScreen
import land.noah.smartcafe.ui.screens.ProductDetailScreen
import land.noah.smartcafe.ui.theme.SmartCafeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCafeTheme {
                val auth = FirebaseAuth.getInstance()

                var isLoggedIn by remember {
                    mutableStateOf(auth.currentUser != null)
                }

                var selectedMenuItem by remember {
                    mutableStateOf<MenuItem?>(null)
                }

                var cartItems by remember {
                    mutableStateOf<List<CartItem>>(emptyList())
                }

                var orders by remember {
                    mutableStateOf<List<Order>>(emptyList())
                }

                var lastPlacedOrder by remember {
                    mutableStateOf<Order?>(null)
                }

                val cartItemCount = cartItems.sumOf { cartItem ->
                    cartItem.quantity
                }

                var showingCart by remember {
                    mutableStateOf(false)
                }

                var showingOrderHistory by remember {
                    mutableStateOf(false)
                }

                fun addToCart(menuItem: MenuItem) {
                    if (!menuItem.available) {
                        return
                    }

                    val existingItem = cartItems.find { cartItem ->
                        cartItem.menuItem.id == menuItem.id
                    }

                    cartItems = if (existingItem == null) {
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
                    cartItems = cartItems.map { cartItem ->
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
                    cartItems = cartItems.mapNotNull { cartItem ->
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
                    cartItems = cartItems.filterNot { cartItem ->
                        cartItem.menuItem.id == menuItemId
                    }
                }

                fun placeOrder() {
                    if (cartItems.isEmpty()) {
                        return
                    }

                    val currentUser = auth.currentUser ?: return

                    val createdAt = System.currentTimeMillis()

                    val orderItems = cartItems.map { cartItem ->
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
                    cartItems = emptyList()
                    showingCart = false
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
                                    addToCart(item)
                                }
                            )
                        }

                        showingCart -> {
                            CartScreen(
                                cartItems = cartItems,
                                onIncreaseQuantity = { menuItemId ->
                                    increaseCartQuantity(menuItemId)
                                },
                                onDecreaseQuantity = { menuItemId ->
                                    decreaseCartQuantity(menuItemId)
                                },
                                onRemoveItem = { menuItemId ->
                                    removeFromCart(menuItemId)
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
                                onBack = {
                                    showingOrderHistory = false
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