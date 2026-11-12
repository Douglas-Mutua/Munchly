# Munchly M-Pesa Daraja STK Push Integration Guide

This document outlines how to use the provided Safaricom Daraja Sandbox credentials in your backend server to securely process M-Pesa STK Push payments for the Munchly application.

## Daraja Credentials
- **Shortcode (Business Short Code / Paybill / Till)**: `174379`
- **Passkey**: `bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919`
- **Consumer Key**: `z1YpJfyhrlheadyfe0pnk10lAHlQaysFEJggQj9vXtF3M7U0`
- **Consumer Secret**: `11YwzoZGgyp7RobijGSomjAG3NQbzBxGm28wIkYONl8QdhD7SLwivYqK7LmKIpcY`

---

## Backend Implementation Workflow (Python / Node.js / Java Spring Boot)

### 1. Generate Access Token
- **Endpoint**: `https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials`
- **Authentication**: Basic Auth using `Base64(ConsumerKey:ConsumerSecret)`
- **Response**: Returns an `access_token` valid for 3600 seconds.

### 2. Generate Password for STK Push
- **Timestamp Format**: `YYYYMMDDHHmmss` (e.g., `20231024123000`)
- **Password Formula**: `Base64Encode(Shortcode + Passkey + Timestamp)`
- Example: `Base64("174379" + "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919" + "20231024123000")`

### 3. Initiate STK Push Request
- **Endpoint**: `https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest`
- **Headers**:
  - `Authorization: Bearer <access_token>`
  - `Content-Type: application/json`
- **JSON Payload**:
  ```json
  {
    "BusinessShortCode": "174379",
    "Password": "<Generated_Password>",
    "Timestamp": "<Timestamp>",
    "TransactionType": "CustomerPayBillOnline",
    "Amount": "1",
    "PartyA": "254712345678",
    "PartyB": "174379",
    "PhoneNumber": "254712345678",
    "CallBackURL": "https://your-backend-server.com/api/v1/mpesa/callback",
    "AccountReference": "Munchly",
    "TransactionDesc": "Payment for Munchly Order"
  }
  ```

### 4. Handle Asynchronous Callback
- Safaricom sends the transaction result to your registered `CallBackURL`.
- The backend verifies the transaction result code (`ResultCode == 0` for success), extracts the M-Pesa receipt number (`MpesaReceiptNumber`), updates the order status to **Paid**, and records the transaction in the database.
- The Android client polls or listens to the updated order/payment status and displays confirmation to the user.
