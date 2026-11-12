package com.example.munchly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.Order;
import com.example.munchly.model.Payment;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MpesaPaymentActivity extends AppCompatActivity {

    private TextView tvPaymentStatus, tvPaymentDetails;
    private Button btnConfirmPaid, btnCancelPayment;
    private String orderId;
    private String phone;
    private double amount;

    // Safaricom Daraja API Credentials (Replaced with placeholders for secure GitHub repository publishing)
    private static final String CONSUMER_KEY = "YOUR_DARAJA_CONSUMER_KEY";
    private static final String CONSUMER_SECRET = "YOUR_DARAJA_CONSUMER_SECRET";
    private static final String PASSKEY = "YOUR_DARAJA_PASSKEY";
    private static final String SHORTCODE = "174379";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa_payment);

        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        tvPaymentDetails = findViewById(R.id.tvPaymentDetails);
        btnConfirmPaid = findViewById(R.id.btnConfirmPaid);
        btnCancelPayment = findViewById(R.id.btnCancelPayment);

        orderId = getIntent().getStringExtra("orderId");
        phone = getIntent().getStringExtra("phone");
        amount = getIntent().getDoubleExtra("amount", 0.0);

        tvPaymentDetails.setText("Order ID: " + orderId + "\nAmount: KES " + String.format("%.2f", amount) + "\nPhone: " + phone);
        tvPaymentStatus.setText(getString(R.string.mpesa_initial_prompt));

        // Automatically trigger real Daraja STK Push prompt on load
        triggerDarajaStkPush();

        btnConfirmPaid.setOnClickListener(v -> {
            String receiptNumber = "QHS" + (int)(Math.random() * 900000 + 100000);
            Payment payment = new Payment(
                    "PAY-" + System.currentTimeMillis(),
                    orderId,
                    amount,
                    phone,
                    "WS_COP_" + System.currentTimeMillis(),
                    receiptNumber,
                    "Success",
                    System.currentTimeMillis()
            );
            MunchlyRepository.getInstance().addPayment(payment);

            for (Order order : MunchlyRepository.getInstance().getOrders()) {
                if (order.getOrderId().equals(orderId)) {
                    order.setOrderStatus("Paid");
                    break;
                }
            }
            MunchlyRepository.getInstance().clearCart();

            Toast.makeText(this, "Payment Confirmed! M-Pesa Receipt: " + receiptNumber, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnCancelPayment.setOnClickListener(v -> {
            for (Order order : MunchlyRepository.getInstance().getOrders()) {
                if (order.getOrderId().equals(orderId)) {
                    order.setOrderStatus("Cancelled");
                    break;
                }
            }
            Toast.makeText(this, "Payment cancelled.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void triggerDarajaStkPush() {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                // 1. Generate OAuth Access Token
                String credentials = CONSUMER_KEY + ":" + CONSUMER_SECRET;
                String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                Request authRequest = new Request.Builder()
                        .url("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials")
                        .get()
                        .addHeader("Authorization", "Basic " + encodedCredentials)
                        .build();

                try (Response authResponse = client.newCall(authRequest).execute()) {
                    if (!authResponse.isSuccessful()) {
                        runOnUiThread(() -> tvPaymentStatus.setText("Daraja Auth Failed. Please retry."));
                        return;
                    }

                    String authResponseBody = authResponse.body() != null ? authResponse.body().string() : "";
                    JSONObject jsonAuth = new JSONObject(authResponseBody);
                    String accessToken = jsonAuth.getString("access_token");

                    // 2. Trigger STK Push Request
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    String timestamp = sdf.format(new Date());
                    String passwordStr = SHORTCODE + PASSKEY + timestamp;
                    String password = Base64.encodeToString(passwordStr.getBytes(), Base64.NO_WRAP);

                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("BusinessShortCode", SHORTCODE);
                    jsonBody.put("Password", password);
                    jsonBody.put("Timestamp", timestamp);
                    jsonBody.put("TransactionType", "CustomerPayBillOnline");
                    jsonBody.put("Amount", (int) Math.round(amount));
                    jsonBody.put("PartyA", phone);
                    jsonBody.put("PartyB", SHORTCODE);
                    jsonBody.put("PhoneNumber", phone);
                    jsonBody.put("CallBackURL", "https://mydomainr.com/path"); // Sandbox callback URL
                    jsonBody.put("AccountReference", orderId);
                    jsonBody.put("TransactionDesc", "Munchly Food Order");

                    RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));
                    Request stkRequest = new Request.Builder()
                            .url("https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest")
                            .post(body)
                            .addHeader("Authorization", "Bearer " + accessToken)
                            .build();

                    try (Response stkResponse = client.newCall(stkRequest).execute()) {
                        runOnUiThread(() -> {
                            if (stkResponse.isSuccessful()) {
                                tvPaymentStatus.setText("STK Push sent to " + phone + "!\nPlease enter your PIN on your phone to complete payment.");
                                Toast.makeText(this, "M-Pesa STK Push sent successfully to " + phone, Toast.LENGTH_LONG).show();
                            } else {
                                tvPaymentStatus.setText("STK Push Error (" + stkResponse.code() + "). Please retry.");
                            }
                        });
                    }
                }

            } catch (Exception e) {
                runOnUiThread(() -> tvPaymentStatus.setText("Network error: " + e.getMessage()));
            }
        }).start();
    }
}
