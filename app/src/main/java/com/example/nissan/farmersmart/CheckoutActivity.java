package com.example.nissan.farmersmart;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckoutActivity extends AppCompatActivity {
    private static final String LOG_TAG = "CheckoutActivity";

    private TextView mProductName;
    private TextView mLocation;
    private TextView mRate;
    private TextView mRateWeightUnit;
    private TextView mWeight;
    private TextView mWeightWeightUnit;
    private TextView mSubTotal;
    private TextView mTransportationCost;
    private TextView mServiceCost;
    private TextView mGrandTotal;
    private AppCompatEditText mCustomerName;
    private AppCompatEditText mCustomerAddress;
    private AppCompatEditText mCustomerPhoneNumber;
    private Button mPurchaseButton;
    private Button mCancelButton;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mProductsDatabaseReference;

    private boolean hasProductChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hasProductChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_acvitity);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProductsDatabaseReference = mFirebaseDatabase.getReference().child("orders");

        mProductName = findViewById(R.id.cout_product_name);
        mLocation = findViewById(R.id.cout_location);
        mRate = findViewById(R.id.cout_rate);
        mRateWeightUnit = findViewById(R.id.cout_rate_weight_unit);
        mWeight = findViewById(R.id.cout_picked_weight);
        mWeightWeightUnit = findViewById(R.id.cout_picked_weight_unit);
        mSubTotal = findViewById(R.id.cout_subtotal);
        mTransportationCost = findViewById(R.id.cout_transportaion);
        mServiceCost = findViewById(R.id.cout_service);
        mGrandTotal = findViewById(R.id.cout_total);
        mCustomerName = findViewById(R.id.cout_customer_name);
        mCustomerAddress = findViewById(R.id.cout_customer_address);
        mCustomerPhoneNumber = findViewById(R.id.cout_customer_phone_number);
        mPurchaseButton = findViewById(R.id.cout_purchase_button);
        mCancelButton = findViewById(R.id.cout_cancel_button);

        Intent intent = getIntent();
        final Order order = (Order) intent.getSerializableExtra("orderDetails");
        Log.v(LOG_TAG, order.printAll());
        Product product = order.getProduct();
        mProductName.setText(product.getName());
        mLocation.setText(product.getLocation());
        mRate.setText(String.valueOf(product.getRate()));
        mRateWeightUnit.setText(product.getWeightUnit());
        mWeight.setText(String.valueOf(order.getSelectedWeight()));
        mWeightWeightUnit.setText(order.getSelectedWeightUnit());
        mSubTotal.setText(String.valueOf(order.getSubtotal()));
        mTransportationCost.setText(String.valueOf(order.getTransportationCost()));
        mServiceCost.setText(String.valueOf(order.getServiceCharge()));
        mGrandTotal.setText(String.valueOf(order.getGrandTotal()));

        mCustomerName.setOnTouchListener(mTouchListener);
        mCustomerAddress.setOnTouchListener(mTouchListener);
        mCustomerPhoneNumber.setOnTouchListener(mTouchListener);

        mPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String customerName = mCustomerName.getText().toString().trim();
                String customerAddress = mCustomerAddress.getText().toString().trim();
                String customerPhoneNumber = mCustomerPhoneNumber.getText().toString().trim();
                FinalOrder finalOrder = new FinalOrder(order, customerName, customerAddress, customerPhoneNumber);
                Log.v(LOG_TAG, finalOrder.printAll());
                saveOrder(finalOrder);

            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        };
                showGoingBackDialog(discardButtonClickListener, getString(R.string.cancel_order_confirmation));

            }
        });

    }

    private void saveOrder(FinalOrder finalOrder) {
        if (TextUtils.isEmpty(mCustomerName.getText().toString().trim()) ||

                TextUtils.isEmpty(mCustomerAddress.getText().toString().trim()) ||
                TextUtils.isEmpty(mCustomerPhoneNumber.getText().toString().trim())
                ) {
            showEmptyFieldsDialog();
            return;
        }
        mProductsDatabaseReference.push().setValue(finalOrder);
        showSuccessAlert();
    }

    @Override
    public void onBackPressed() {
        if (!hasProductChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        showGoingBackDialog(discardButtonClickListener, getString(R.string.do_you_want_to_go_back));
    }

    private void showGoingBackDialog(DialogInterface.OnClickListener discardButtonClickListener, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, discardButtonClickListener);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                if (!hasProductChanged) {
                    super.onBackPressed();
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        };
                showGoingBackDialog(discardButtonClickListener, getString(R.string.go_back_question));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSuccessAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
        builder.setMessage(R.string.order_confirmed_message);
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showEmptyFieldsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
        builder.setMessage(R.string.please_fill_out_all_the_fields);
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
