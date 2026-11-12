package com.example.munchly.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.munchly.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * MunchlyDbHelper manages the local SQLite database for the Munchly E-Commerce application.
 * Handles product catalog persistence, inventory stock adjustments, item additions, and deletions.
 */
public class MunchlyDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "munchly.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_STOCK = "stock_quantity";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_IMAGE_URL = "image_url";

    private static MunchlyDbHelper instance;

    /** Returns the singleton instance of MunchlyDbHelper. */
    public static synchronized MunchlyDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MunchlyDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private MunchlyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create products table schema
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_PRODUCT_ID + " TEXT PRIMARY KEY,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_STOCK + " INTEGER,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_IMAGE_URL + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

        // Seed initial curated fast-food menu items
        seedDefaultProducts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    /** Seeds initial fast-food menu items into the database. */
    private void seedDefaultProducts(SQLiteDatabase db) {
        insertProduct(db, new Product("MNCH-001", "Double Beef Burger", "Juicy double beef patties with melted cheddar, lettuce, and secret Munchly sauce.", 450.00, 25, "Burgers", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=600&q=80"));
        insertProduct(db, new Product("MNCH-002", "Golden Spicy Chicken", "2 pieces of crispy, golden-fried chicken spiced to perfection.", 350.00, 15, "Chicken", "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?auto=format&fit=crop&w=600&q=80"));
        insertProduct(db, new Product("MNCH-003", "Loaded Masala Fries", "Crispy French fries tossed in spicy masala sauce with fresh cilantro.", 250.00, 30, "Sides", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?auto=format&fit=crop&w=600&q=80"));
        insertProduct(db, new Product("MNCH-004", "Honey BBQ Wings", "6 tender chicken wings glazed in sticky, sweet-and-spicy honey BBQ sauce.", 500.00, 20, "Chicken", "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?auto=format&fit=crop&w=600&q=80"));
        insertProduct(db, new Product("MNCH-005", "Creamy Chicken Spaghetti", "Rich and savory spaghetti tossed with grilled chicken strips and herbs.", 600.00, 10, "Specials", "https://images.unsplash.com/photo-1551183053-bf91a1d81141?auto=format&fit=crop&w=600&q=80"));
        insertProduct(db, new Product("MNCH-006", "Soda (500ml)", "Chilled Coca-Cola, Fanta, or Sprite served ice-cold.", 100.00, 50, "Drinks", "https://images.unsplash.com/photo-1551024709-8f23befc6f87?auto=format&fit=crop&w=600&q=80"));
    }

    /** Inserts or replaces a product record in the SQLite database. */
    public void insertProduct(SQLiteDatabase db, Product p) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_ID, p.getProductId());
        values.put(COLUMN_NAME, p.getName());
        values.put(COLUMN_DESCRIPTION, p.getDescription());
        values.put(COLUMN_PRICE, p.getPrice());
        values.put(COLUMN_STOCK, p.getStockQuantity());
        values.put(COLUMN_CATEGORY, p.getCategory());
        values.put(COLUMN_IMAGE_URL, p.getImageUrl());
        db.insertWithOnConflict(TABLE_PRODUCTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /** Adds or updates a product in the database. */
    public void addProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        insertProduct(db, p);
    }

    /** Updates stock quantity for a specific product. */
    public void updateProductStock(String productId, int newStock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STOCK, Math.max(0, newStock));
        db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCT_ID + " = ?", new String[]{productId});
    }

    /** Deducts inventory stock quantity when an order is placed. */
    public void deductStock(String productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_PRODUCTS + " SET " + COLUMN_STOCK + " = MAX(0, " + COLUMN_STOCK + " - " + quantity + ") WHERE " + COLUMN_PRODUCT_ID + " = ?", new String[]{productId});
    }

    /** Deletes a product from the database by its product ID. */
    public void deleteProduct(String productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + " = ?", new String[]{productId});
    }

    /** Retrieves all products available in the SQLite database. */
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
        if (cursor.moveToFirst()) {
            do {
                Product p = new Product(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))
                );
                productList.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }
}
