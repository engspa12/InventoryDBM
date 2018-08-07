package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.product_type_spinner) Spinner productTypesSpinner;
    @BindView(R.id.in_stock_spinner) Spinner inStockSpinner;
    @BindView(R.id.product_image) ImageView productImageView;

    @BindView(R.id.details_button) Button multi_button;
    @BindView(R.id.quantity_minus) Button decreaseQuantityButton;
    @BindView(R.id.quantity_plus) Button increaseQuantityButton;
    @BindView(R.id.order_supplier_button) Button orderFromSupplierButton;
    @BindView(R.id.increase_quantity_by_button) Button increaseQuantityByButton;
    @BindView(R.id.decrease_quantity_by_button) Button decreaseQuantityByButton;

    @BindView(R.id.product_brand) EditText brandET;
    @BindView(R.id.product_warranty) EditText warrantyET;
    @BindView(R.id.product_year_manufacture) EditText dateManufactureET;
    @BindView(R.id.product_weight) EditText weightET;
    @BindView(R.id.product_price) EditText priceET;
    @BindView(R.id.product_quantity) EditText quantityET;
    @BindView(R.id.product_name) EditText nameET ;
    @BindView(R.id.modify_quantity_by_et) EditText modifyQuantityET;

    @BindView(R.id.increase_decrease_order__buttons_section) LinearLayout containerOne;
    @BindView(R.id.increase_decrease_section) LinearLayout containerTwo;

    private Uri wantedUri;
    private int typeProduct;
    private int inStock;
    private int image_id_resource;
    private boolean nullValues;
    private boolean quantityOK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        ButterKnife.bind(this);

        wantedUri = getIntent().getData();

        if (wantedUri == null) {
            // This is a new product, so change the app bar to say "Add a Product"
            setTitle("Add Product");
            image_id_resource = R.drawable.sports;
            productImageView.setImageResource(image_id_resource);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            multi_button.setText(getString(R.string.change_image_button));
            decreaseQuantityButton.setVisibility(View.GONE);
            increaseQuantityButton.setVisibility(View.GONE);
            orderFromSupplierButton.setVisibility(View.GONE);
            increaseQuantityByButton.setVisibility(View.GONE);
            decreaseQuantityByButton.setVisibility(View.GONE);

            modifyQuantityET.setVisibility(View.GONE);

            containerOne.setVisibility(View.GONE);
            containerTwo.setVisibility(View.GONE);
            invalidateOptionsMenu();

            multi_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (image_id_resource) {
                        case R.drawable.sports:
                            productImageView.setImageResource(R.drawable.technology);
                            image_id_resource = R.drawable.technology;
                            break;
                        case R.drawable.technology:
                            productImageView.setImageResource(R.drawable.home_stuff);
                            image_id_resource = R.drawable.home_stuff;
                            break;
                        case R.drawable.home_stuff:
                            productImageView.setImageResource(R.drawable.clothing);
                            image_id_resource = R.drawable.clothing;
                            break;
                        case R.drawable.clothing:
                            productImageView.setImageResource(R.drawable.sports);
                            image_id_resource = R.drawable.sports;
                            break;
                        default:
                            image_id_resource = R.drawable.sports;
                    }
                }
            });

        } else { //other stuff
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle("Product Details");
            multi_button.setText(getString(R.string.delete_product_button));
            avoidModifications();
            invalidateOptionsMenu();

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(0, null, this);

            multi_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteConfirmationDialog();
                }
            });

            decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = Integer.parseInt(quantityET.getText().toString().trim());
                    if (quantity > 0) {
                        quantity--;
                        quantityET.setText(String.valueOf(quantity));
                    } else {
                        Toast.makeText(getBaseContext(), "The min quantity allowed is 0", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = Integer.parseInt(quantityET.getText().toString().trim());
                    if (quantity < 15) {
                        quantity++;
                        quantityET.setText(String.valueOf(quantity));
                    } else {
                        Toast.makeText(getBaseContext(), "The max quantity allowed is 15", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            orderFromSupplierButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = Integer.parseInt(quantityET.getText().toString().trim());
                    String[] addresses = {"arturo.lpc12@gmail.com"};
                    String brand = brandET.getText().toString().trim();
                    String name = nameET.getText().toString().trim();
                    String price = String.format("%.2f", Double.parseDouble(priceET.getText().toString().trim()));
                    String message = "Dear Provider,\n\n We need "+ quantity + " more items from the product " +
                            "with the following data:\n\nName: " + name + "\nBrand: " + brand + "\nPrice: USD " + price +
                            "\n\nThank you for your attention.";
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Sale Order");
                    intent.putExtra(Intent.EXTRA_TEXT,message);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });


            increaseQuantityByButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String amountString = modifyQuantityET.getText().toString().trim();
                    int amount;
                    if (amountString.equals("")) {
                        amount = 0;
                        Toast.makeText(getBaseContext(), "You need to enter a quantity in the input field", Toast.LENGTH_SHORT).show();
                    } else {
                        amount = Integer.parseInt(amountString);
                    }

                    int quantity = Integer.parseInt(quantityET.getText().toString().trim());
                    quantity = quantity + amount;
                    if (quantity > 15) {
                        Toast.makeText(getBaseContext(), "The max quantity allowed is 15", Toast.LENGTH_SHORT).show();
                    } else {
                        quantityET.setText(String.valueOf(quantity));
                    }
                }
            });

            decreaseQuantityByButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String amountString = modifyQuantityET.getText().toString().trim();
                    int amount;
                    if (amountString.equals("")) {
                        amount = 0;
                        Toast.makeText(getBaseContext(), "You need to enter a quantity in the input field", Toast.LENGTH_SHORT).show();
                    } else {
                        amount = Integer.parseInt(amountString);
                    }

                    int quantity = Integer.parseInt(quantityET.getText().toString().trim());
                    quantity = quantity - amount;
                    if (quantity < 0) {
                        Toast.makeText(getBaseContext(), "The min quantity allowed is 0", Toast.LENGTH_SHORT).show();
                    } else {
                        quantityET.setText(String.valueOf(quantity));
                    }
                }
            });


        }


        setupSpinners();


    }


    private void avoidModifications() {
        brandET.setKeyListener(null);
        brandET.setBackgroundDrawable(null);
        warrantyET.setKeyListener(null);
        warrantyET.setBackgroundDrawable(null);
        dateManufactureET.setKeyListener(null);
        dateManufactureET.setBackgroundDrawable(null);
        weightET.setKeyListener(null);
        weightET.setBackgroundDrawable(null);
        priceET.setKeyListener(null);
        priceET.setBackgroundDrawable(null);
        quantityET.setKeyListener(null);
        quantityET.setBackgroundDrawable(null);
        nameET.setKeyListener(null);
        nameET.setBackgroundDrawable(null);

    }

    private void setupSpinners() {

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> typesAdapter = ArrayAdapter.createFromResource(this,
                R.array.product_types_options, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        productTypesSpinner.setAdapter(typesAdapter);

        productTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String selection = (String) adapterView.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.sports_type))) {
                        //Sports Type
                        typeProduct = 0;
                    } else if (selection.equals(getString(R.string.technology_type))) {
                        //Technology Type
                        typeProduct = 1;
                    } else if (selection.equals(getString(R.string.home_stuff_type))) {
                        //Home Stuff Type
                        typeProduct = 2;
                    } else {
                        //Clothing Type
                        typeProduct = 3;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                typeProduct = 1; //Sports is the default choice
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> inStockAdapter = ArrayAdapter.createFromResource(this,
                R.array.in_stock_options, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        inStockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        inStockSpinner.setAdapter(inStockAdapter);

        inStockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String selection = (String) adapterView.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.not_in_stock))) {
                        //Product in Stock
                        inStock = 0;
                    } else if (selection.equals(getString(R.string.in_stock))) {
                        //Product is not in Stock
                        inStock = 1;
                    } else {
                        //Unknown Stock
                        inStock = 2;
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Unknown Stock
                inStock = 2;
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_add, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        //if (wantedUri == null) {
        MenuItem menuItem = menu.findItem(R.id.action_delete);
        menuItem.setVisible(false);
        // }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_save:
                nullValues = false;
                quantityOK = true;
                saveProduct();
                if (!nullValues) {
                    if (quantityOK)
                        finish();
                }
                return true;
            // Respond to a click on the "Delete all entries" menu option
            //case R.id.action_delete:
               // showDeleteConfirmationDialog();
                //deleteProduct();
             //   return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void saveProduct() {

        String brand = brandET.getText().toString().trim();
        String warranty = warrantyET.getText().toString().trim();
        String dateManufacture = dateManufactureET.getText().toString().trim();
        String weight = weightET.getText().toString().trim();
        String price = priceET.getText().toString().trim();
        String quantity = quantityET.getText().toString().trim();
        String name = nameET.getText().toString().trim();

        if (brand.equals("") || warranty.equals("") || dateManufacture.equals("") || weight.equals("") ||
                price.equals("") || quantity.equals("") || name.equals("")) {
            Toast.makeText(getBaseContext(), "Null values are not allowed", Toast.LENGTH_SHORT).show();
            nullValues = true;
            return;
        }


        int warrantyInt = Integer.parseInt(warranty);
        int weightInt = Integer.parseInt(weight);
        double priceDouble = Double.parseDouble(price);
        int quantityInt = Integer.parseInt(quantity);

        if (quantityInt > 15 || quantityInt < 0) {
            Toast.makeText(this, "Quantity must be between 0 and 15", Toast.LENGTH_SHORT).show();
            quantityOK = false;
            return;
        }


        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COLUMN_PRODUCT_BRAND, brand);
        values.put(InventoryEntry.COLUMN_PRODUCT_WARRANTY, warrantyInt);
        values.put(InventoryEntry.COLUMN_PRODUCT_DATE_MANUFACTURE, dateManufacture);
        values.put(InventoryEntry.COLUMN_PRODUCT_WEIGHT, weightInt);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceDouble);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityInt);
        values.put(InventoryEntry.COLUMN_PRODUCT_STOCK, inStock);
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, name);
        values.put(InventoryEntry.COLUMN_PRODUCT_TYPE, typeProduct);
        values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE_ID, image_id_resource);


        if (wantedUri == null) {
            getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        } else {
            getContentResolver().update(wantedUri, values, null, null);
        }

    }

    public void deleteProduct() {

        if (wantedUri != null) {
            getContentResolver().delete(wantedUri, null, null);
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(), wantedUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            return;
        }


        if (data.moveToFirst()) {

            String currentBrand = data.getString(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_BRAND));
            int currentWarranty = data.getInt(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_WARRANTY));
            String currentDateManufacture = data.getString(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_DATE_MANUFACTURE));
            int currentWeight = data.getInt(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_WEIGHT));
            double currentPrice = data.getDouble(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE));
            int currentQuantity = data.getInt(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY));
            String currentName = data.getString(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));

            int currentType = data.getInt(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_TYPE));
            int currentInStock = data.getInt(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_STOCK));

            int currentImageResource = data.getInt(data.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_IMAGE_ID));
            image_id_resource = currentImageResource;

            brandET.setText(currentBrand);
            warrantyET.setText(String.valueOf(currentWarranty));
            dateManufactureET.setText(currentDateManufacture);
            weightET.setText(String.valueOf(currentWeight));
            priceET.setText(String.valueOf(currentPrice));
            quantityET.setText(String.valueOf(currentQuantity));
            nameET.setText(currentName);

            productImageView.setImageResource(currentImageResource);


            if (currentType == InventoryEntry.SPORTS_TYPE) {
                productTypesSpinner.setSelection(0);
            } else if (currentType == InventoryEntry.TECHNOLOGY_TYPE) {
                productTypesSpinner.setSelection(1);
            } else if (currentType == InventoryEntry.HOME_STUFF_TYPE) {
                productTypesSpinner.setSelection(2);
            } else {
                productTypesSpinner.setSelection(3);
            }


            if (currentInStock == InventoryEntry.NO_STOCK_AVAILABLE) {
                inStockSpinner.setSelection(0);
            } else if (currentInStock == InventoryEntry.IN_STOCK) {
                inStockSpinner.setSelection(1);
            } else {
                inStockSpinner.setSelection(2);
            }

            productTypesSpinner.setEnabled(false);
            inStockSpinner.setEnabled(false);


        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        brandET.setText("");
        warrantyET.setText("");
        dateManufactureET.setText("");
        weightET.setText("");
        priceET.setText("");
        quantityET.setText("");
        nameET.setText("");
        productTypesSpinner.setSelection(0);
        inStockSpinner.setSelection(0);

    }
}

