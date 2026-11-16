package land.noah.smartcafe.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import land.noah.smartcafe.model.CartItem
import land.noah.smartcafe.model.MenuItem

class SmartCafeViewModel : ViewModel() {

    private var _cartItems by mutableStateOf<List<CartItem>>(emptyList())

    var cartItems: List<CartItem>
        get() = _cartItems

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
}