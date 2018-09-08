package com.example.nissan.farmersmart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductDetails extends AppCompatActivity {
    private static final String LOG_TAG = "ProductDetails";
    private ImageView mProductImage;
    private TextView mProductName;
    private TextView mLocation;
    private TextView mRate;
    private TextView mMinAmount;
    private TextView mMaxAmount;
    private EditText mAmountPickerTextView;
    private TextView mSubTotal;
    private TextView mTransportationCharge;
    private TextView mServiceCharge;
    private TextView mTotal;
    private TextView mMinAmountUnit;
    private TextView mMaxAmountUnit;
    private Button mDecreaseButton;
    private Button mIncreaseButton;
    private Button mCheckoutButton;

    private double subtotal;
    private double totalRounded;
    private double transportationCost;
    private double serviceCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        final Intent intent = getIntent();
        final Product selectedProduct = (Product) intent.getSerializableExtra("product");
        Log.v(LOG_TAG, selectedProduct.printAll());

        mProductImage = findViewById(R.id.det_product_image);
        mProductName = findViewById(R.id.det_product_name);
        mLocation = findViewById(R.id.det_location);
        mRate = findViewById(R.id.det_rate);
        mMinAmount = findViewById(R.id.det_min_amount);
        mMaxAmount = findViewById(R.id.det_max_amount);
        mAmountPickerTextView = findViewById(R.id.det_amount);
        mSubTotal = findViewById(R.id.det_subtotal);
        mTransportationCharge = findViewById(R.id.det_transport_charge);
        mServiceCharge = findViewById(R.id.det_service_charge);
        mTotal = findViewById(R.id.det_total);
        mDecreaseButton = findViewById(R.id.det_dec_button);
        mIncreaseButton = findViewById(R.id.det_inc_button);
        mMinAmountUnit = findViewById(R.id.det_min_amount_unit);
        mMaxAmountUnit = findViewById(R.id.det_max_amount_unit);
        mCheckoutButton = findViewById(R.id.det_checkout_button);

        switch (selectedProduct.getName()) {
            case "Apple":
                mProductImage.setImageResource(R.drawable.apples);
                break;
            case "Banana":
                mProductImage.setImageResource(R.drawable.bananas);
                break;
            case "Cauliflower":
                mProductImage.setImageResource(R.drawable.cauliflower);
                break;
            case "Potatoes":
                mProductImage.setImageResource(R.drawable.potatoes);
                break;
            case "Tomatoes":
                mProductImage.setImageResource(R.drawable.tomatoes);
                break;
            default:
                mProductImage.setImageResource(R.drawable.fruitsandvegetables);
                break;
        }

        mProductName.setText(selectedProduct.getName());
        mLocation.setText(selectedProduct.getLocation());
        mRate.setText(String.valueOf(selectedProduct.getRate()));
        mMinAmount.setText(String.valueOf(selectedProduct.getMinimumQuantity()));
        mAmountPickerTextView.setText("" + 1);
        mMaxAmount.setText(String.valueOf(selectedProduct.getMaximumQuantity()));
        String weightUnit = selectedProduct.getWeightUnit();
        String weightUnitLoc;
        if (weightUnit.equals("Kg")){
            weightUnitLoc = getString(R.string.kg);
        }
        else {
            weightUnitLoc = getString(R.string.tonne);
        }
        mMinAmountUnit.setText(weightUnitLoc);
        mMaxAmountUnit.setText(weightUnitLoc);

        final float rate = selectedProduct.getRate();

        calculateTotalAndDisplay(selectedProduct);
        Log.v(LOG_TAG, "amount: " + mAmountPickerTextView.getText());
//        calculateTotalAndDisplay(selectedProduct, transportationCost,serviceCost);

        mDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedAmount = Integer.valueOf(mAmountPickerTextView.getText().toString().trim());
                if (selectedAmount > 1) {
                    mAmountPickerTextView.setText("" + (selectedAmount - 1));
                } else {
                    mAmountPickerTextView.setText("" + 1);
                }

            }
        });
        mIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedAmount = Integer.valueOf(mAmountPickerTextView.getText().toString().trim());
                if (selectedAmount < selectedProduct.getMaximumQuantity()) {
                    mAmountPickerTextView.setText("" + (selectedAmount + 1));
                }

            }
        });
        mAmountPickerTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateTotalAndDisplay(selectedProduct);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), CheckoutActivity.class);
                int currentAmount = Integer.valueOf(mAmountPickerTextView.getText().toString().trim());
                Order order = new Order(selectedProduct,
                        currentAmount,
                        selectedProduct.getWeightUnit(),
                        subtotal,
                        transportationCost,
                        serviceCost,
                        totalRounded);
                intent1.putExtra("orderDetails", order);
                startActivity(intent1);
            }
        });

    }

    private void calculateTotalAndDisplay(Product selectedProduct) {
        if (!mAmountPickerTextView.getText().toString().trim().equals("")) {
            int currentAmount = Integer.valueOf(mAmountPickerTextView.getText().toString().trim());
            if (currentAmount > selectedProduct.getMaximumQuantity()) {
                currentAmount = selectedProduct.getMaximumQuantity();
                mAmountPickerTextView.setText("" + currentAmount);
            }
            subtotal = currentAmount * selectedProduct.getRate();
            subtotal = (double) Math.round(subtotal * 100D) / 100D;
            mSubTotal.setText(String.valueOf(subtotal));


            transportationCost = (double) Math.round(0.1 * subtotal * 100D) / 100D;
            serviceCost = (double) Math.round(0.1 * subtotal * 100D) / 100D;
            mTransportationCharge.setText(String.valueOf(transportationCost));
            mServiceCharge.setText(String.valueOf(serviceCost));

            double total = subtotal + transportationCost + serviceCost;
            totalRounded = (double) Math.round(total * 100D) / 100D;
            mTotal.setText(String.valueOf(totalRounded));
        } else if (mAmountPickerTextView.getText().toString().trim().equals("")) {
            mSubTotal.setText(String.valueOf(0));
            mTotal.setText(String.valueOf(0));
        }
    }

}
