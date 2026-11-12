package com.example.munchly;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.munchly.data.MunchlyDbHelper;
import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.CartItem;
import com.example.munchly.model.Order;
import com.example.munchly.model.User;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    private EditText etCustomerName, etPhone, etAddress;
    private TextView tvCheckoutTotal, btnPickContact;
    private Button btnPayMpesa;
    private ImageView btnBack;
    private ActivityResultLauncher<Intent> contactPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        etCustomerName = findViewById(R.id.etCustomerName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal);
        btnPayMpesa = findViewById(R.id.btnPayMpesa);
        btnBack = findViewById(R.id.btnBack);
        btnPickContact = findViewById(R.id.btnPickContact);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Pre-fill user details if logged in
        User currentUser = MunchlyRepository.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getFullName() != null && !currentUser.getFullName().isEmpty()) {
                etCustomerName.setText(currentUser.getFullName());
            }
            if (currentUser.getPhone() != null && !currentUser.getPhone().isEmpty()) {
                etPhone.setText(currentUser.getPhone());
            }
        }

        contactPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri contactUri = result.getData().getData();
                        if (contactUri != null) {
                            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
                            try (Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null)) {
                                if (cursor != null && cursor.moveToFirst()) {
                                    int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                    int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                                    String number = cursor.getString(numberIndex);
                                    String name = cursor.getString(nameIndex);

                                    if (number != null) {
                                        String formattedPhone = number.replaceAll("[^0-9]", "");
                                        if (formattedPhone.startsWith("0")) {
                                            formattedPhone = "254" + formattedPhone.substring(1);
                                        } else if (!formattedPhone.startsWith("254") && formattedPhone.length() == 9) {
                                            formattedPhone = "254" + formattedPhone;
                                        }
                                        etPhone.setText(formattedPhone);
                                    }
                                    if (name != null && etCustomerName.getText().toString().isEmpty()) {
                                        etCustomerName.setText(name);
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(this, "Failed to read contact", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        btnPickContact.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            contactPickerLauncher.launch(pickIntent);
        });

        double total = MunchlyRepository.getInstance().getCartTotal() + 150.00; // subtotal + delivery
        tvCheckoutTotal.setText(String.format("KES %.2f", total));

        btnPayMpesa.setOnClickListener(v -> {
            String name = etCustomerName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill in all checkout fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("^254[0-9]{9}$")) {
                Toast.makeText(this, "Enter valid Kenyan phone number starting with 254 (e.g. 254712345678)", Toast.LENGTH_LONG).show();
                return;
            }

            // Create order with Pending Payment status
            String orderId = "ORD-" + System.currentTimeMillis();
            Order order = new Order(
                    orderId,
                    MunchlyRepository.getInstance().getCurrentUser().getUserId(),
                    name,
                    phone,
                    address,
                    new ArrayList<>(MunchlyRepository.getInstance().getCartItems()),
                    total,
                    "Pending Payment",
                    System.currentTimeMillis()
            );

            // Deduct stock quantity in SQLite for each ordered item
            for (CartItem item : MunchlyRepository.getInstance().getCartItems()) {
                if (item != null && item.getProduct() != null) {
                    MunchlyDbHelper.getInstance(this).deductStock(item.getProduct().getProductId(), item.getQuantity());
                }
            }

            MunchlyRepository.getInstance().addOrder(order);

            Intent intent = new Intent(this, MpesaPaymentActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("phone", phone);
            intent.putExtra("amount", total);
            startActivity(intent);
            finish();
        });
    }
}