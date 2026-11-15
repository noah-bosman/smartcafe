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
import land.noah.smartcafe.ui.screens.LoginScreen
import land.noah.smartcafe.ui.screens.MenuScreen
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

                val cartItemCount = cartItems.sumOf { cartItem ->
                    cartItem.quantity
                }

                if (isLoggedIn) {
                    val selectedItem = selectedMenuItem

                    if (selectedItem != null) {
                        ProductDetailScreen(
                            item = selectedItem,
                            onBack = {
                                selectedMenuItem = null
                            },
                            onAddToCart = { item ->
                                addToCart(item)
                            }
                        )
                    } else {
                        MenuScreen(
                            menuItems = sampleMenu,
                            onItemClick = { item ->
                                selectedMenuItem = item
                            },
                            onLogout = {
                                auth.signOut()
                                selectedMenuItem = null
                                isLoggedIn = false
                            },
                            cartItemCount = cartItemCount
                        )
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