# Munchly: Android E-Commerce System with M-Pesa STK Push Integration

**Course Unit:** Mobile Application Development
**Institution:** KCA University
**Project:** Munchly Fast-Food Mobile E-Commerce & M-Pesa Payment Platform

---

## 1. System Overview & Architecture

Munchly is a professional Android mobile e-commerce application designed to streamline fast-food browsing, cart management, secure checkout, and M-Pesa STK Push payments.

### Technical Architecture
```
[Android Client (Java)]
       ↕ (HTTPS / REST)
[Safaricom Daraja API Sandbox] ↔ [M-Pesa Mobile Prompt (User PIN)]
       ↕ (Asynchronous Callback)
[Firebase Firestore & Local SQLite (MunchlyDbHelper)]
```

---

## 2. Database & Entity-Relationship Model

The application utilizes a robust dual-storage model combining local **SQLite** (`MunchlyDbHelper`) for instant offline persistence and **Firebase Firestore** for cloud synchronization.

### Core Entities:
- **Users**: `user_id`, `full_name`, `email`, `phone`, `password_hash`, `role` (`customer` or `admin`)
- **Products**: `product_id`, `name`, `description`, `price`, `stock_quantity`, `category`, `image_url`
- **Orders**: `order_id`, `user_id`, `customer_name`, `phone`, `delivery_address`, `total_amount`, `order_status`, `order_date`
- **Payments**: `payment_id`, `order_id`, `amount`, `phone_number`, `merchant_request_id`, `mpesa_receipt_number`, `payment_status`, `transaction_date`

---

## 3. M-Pesa Daraja API STK Push Workflow

1. **Cart & Checkout**: Customer adds items to cart, enters delivery details and M-Pesa phone number (with automatic device contact picker support).
2. **Order Generation**: An order is created with status **`"Pending Payment"`**.
3. **OAuth Token Generation**: The app authenticates with the Daraja API endpoint (`/oauth/v1/generate`) using Basic Auth (Consumer Key & Consumer Secret).
4. **STK Push Request**: Submits payload to `/mpesa/stkpush/v1/processrequest` with Base64 encrypted password (`Shortcode + Passkey + Timestamp`).
5. **Prompt & Verification**: Customer receives the M-Pesa PIN prompt on their mobile phone. Once completed, the payment is confirmed and registered with an official M-Pesa receipt number (`QHS...`), updating the order status to **`"Paid"`** and automatically deducting inventory stock.

---

## 4. Security & Best Practices

- **Credential Masking**: Sensitive Daraja API credentials are externalized/placeheld (`YOUR_DARAJA_CONSUMER_KEY`, etc.) for secure public repository publishing.
- **No PIN Storage**: The application never prompts for, collects, or stores customer M-Pesa PINs.
- **Role-Based Access Control**: Administrative functions (product creation, inventory restocking, product deletion, and customer order management) are strictly restricted to authenticated admin accounts.

---

## 5. Testing Plan & Results

| Test Case | Expected Result | Actual Result | Status |
| :--- | :--- | :--- | :--- |
| **User Registration** | Account created and profile saved | Successfully created in Firebase | **PASS** |
| **Product Catalogue** | Displays items with correct prices in KES | Loaded correctly from SQLite & Firestore | **PASS** |
| **Shopping Cart** | Calculates subtotal and delivery automatically | Accurate totals computed | **PASS** |
| **Contact Picker** | Pulls phone number from device contacts | Populates phone and name fields | **PASS** |
| **M-Pesa STK Push** | Prompts customer phone via Daraja API | STK push sent successfully | **PASS** |
| **Payment Cancellation** | Registers order as "Cancelled" | Status correctly updated | **PASS** |
| **Admin Inventory Restock** | Updates stock and reflects on client side | Synced instantly via SQLite | **PASS** |
