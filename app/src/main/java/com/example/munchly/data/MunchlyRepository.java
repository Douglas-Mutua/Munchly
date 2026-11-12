package com.example.munchly.data;

import android.content.Context;

import com.example.munchly.model.CartItem;
import com.example.munchly.model.Order;
import com.example.munchly.model.Payment;
import com.example.munchly.model.Product;
import com.example.munchly.model.User;

import java.util.ArrayList;
import java.util.List;

public class MunchlyRepository {
    private static MunchlyRepository instance;
    private Context appContext;

    private List<Product> products = new ArrayList<>();
    private List<CartItem> cartItems = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();
    private User currentUser;

    private MunchlyRepository() {
        // Initialize fallback curated fast-food menu items
        products.add(new Product("MNCH-001", "Double Beef Burger", "Juicy double beef patties with melted cheddar, lettuce, and secret Munchly sauce.", 450.00, 25, "Burgers", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=600&q=80"));
        products.add(new Product("MNCH-002", "Golden Spicy Chicken", "2 pieces of crispy, golden-fried chicken spiced to perfection.", 350.00, 15, "Chicken", "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?auto=format&fit=crop&w=600&q=80"));
        products.add(new Product("MNCH-003", "Loaded Masala Fries", "Crispy French fries tossed in spicy masala sauce with fresh cilantro.", 250.00, 30, "Sides", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?auto=format&fit=crop&w=600&q=80"));
        products.add(new Product("MNCH-004", "Honey BBQ Wings", "6 tender chicken wings glazed in sticky, sweet-and-spicy honey BBQ sauce.", 500.00, 20, "Chicken", "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?auto=format&fit=crop&w=600&q=80"));
        products.add(new Product("MNCH-005", "Creamy Chicken Spaghetti", "Rich and savory spaghetti tossed with grilled chicken strips and herbs.", 600.00, 10, "Specials", "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=600&q=80"));
        products.add(new Product("MNCH-006", "Soda (500ml)", "Chilled Coca-Cola, Fanta, or Sprite served ice-cold.", 100.00, 50, "Drinks", "https://images.unsplash.com/photo-1551024709-8f23befc6f87?auto=format&fit=crop&w=600&q=80"));

        // Default test user
        currentUser = new User("u1", "John Doe", "john@example.com", "254712345678", "password123", "customer");
    }

    public static synchronized MunchlyRepository getInstance() {
        if (instance == null) {
            instance = new MunchlyRepository();
        }
        return instance;
    }

    public void init(Context context) {
        if (context != null) {
            this.appContext = context.getApplicationContext();
            List<Product> dbProducts = MunchlyDbHelper.getInstance(appContext).getAllProducts();
            if (dbProducts != null && !dbProducts.isEmpty()) {
                this.products = dbProducts;
            }
        }
    }

    public List<Product> getProducts() {
        if (appContext != null) {
            List<Product> dbProducts = MunchlyDbHelper.getInstance(appContext).getAllProducts();
            if (dbProducts != null && !dbProducts.isEmpty()) {
                this.products = dbProducts;
            }
        }
        return products;
    }

    public void addProduct(Product product) {
        products.add(product);
        if (appContext != null) {
            MunchlyDbHelper.getInstance(appContext).addProduct(product);
        }
    }

    public void removeProduct(String productId) {
        products.removeIf(p -> p.getProductId().equals(productId));
        if (appContext != null) {
            MunchlyDbHelper.getInstance(appContext).deleteProduct(productId);
        }
    }

    public void updateProduct(Product updated) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId().equals(updated.getProductId())) {
                products.set(i, updated);
                break;
            }
        }
        if (appContext != null) {
            MunchlyDbHelper.getInstance(appContext).addProduct(updated);
        }
    }

    public List<CartItem> getCartItems() { return cartItems; }
    public void addToCart(Product product, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getProductId().equals(product.getProductId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cartItems.add(new CartItem(product, quantity));
    }
    public void removeFromCart(String productId) {
        cartItems.removeIf(item -> item.getProduct().getProductId().equals(productId));
    }
    public void clearCart() { cartItems.clear(); }
    public double getCartTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }

    public List<Order> getOrders() { return orders; }
    public void addOrder(Order order) { orders.add(order); }

    public List<Payment> getPayments() { return payments; }
    public void addPayment(Payment payment) { payments.add(payment); }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }
}
