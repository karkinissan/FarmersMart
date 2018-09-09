package com.example.nissan.farmersmart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    public static final String ANONYMOUS = "anonymous";

    public static final int RC_SIGN_IN = 1;
    //    private EditText mProductNameView;
    private Spinner mProductNameView;
    private EditText mLocationView;
    private EditText mQuantityView;
    private Spinner mWeightUnitSpinner;
    private TextView mMinimumQuantity;
    private TextView mMaximumQuantity;
    private TextView mMinimumQuantityUnit;
    private TextView mMaximumQuantityUnit;
    private TextView mRateWeightUnit;

    private EditText mPriceView;
    private ImageView mProductImageView;
    private EditText mFarmerPhoneNoView;
    private EditText mFarmerAddressView;

    private TextView mDatePickedView;
    private TextView mTimePickedView;

    private Button datePickerButton;
    private Button timePickerButton;
//    private Button imagePickerButton;

    private Bitmap mImageThumbnail;

    private Uri mCurrentProductUri;
    private Uri mImageUri;

    private String mSelectedProductName;
    private String mWeightUnit;

    private boolean hasProductChanged = false;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mProductsDatabaseReference;

    private Calendar currentDateTime;
    private Calendar pickedDateTime;
    private String pickedDate;
    private String pickedTime;
    private String defaultDate;
    private String defaultTime;


    private String mUsername;

    private String downloadUri;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hasProductChanged = true;
            return false;
        }
    };

    private ChildEventListener mChildEventListener;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setLocale();
        setContentView(R.layout.activity_editor);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(EditorActivity.this, "Please login to continue", Toast.LENGTH_SHORT).show();
                    onSignedOutCleanup();
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build())
                            ).build(), RC_SIGN_IN);
                }
            }
        };

        currentDateTime = Calendar.getInstance();
        DateFormat dfDate = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        DateFormat dfTime = new SimpleDateFormat("HHmm",Locale.getDefault());
        defaultDate = dfDate.format(currentDateTime.getTime());
        defaultTime = dfTime.format(currentDateTime.getTime());

        // Initialise and clear the variable to be later modified by methods
        pickedDateTime = Calendar.getInstance();
        pickedDateTime.set(Calendar.DAY_OF_MONTH, pickedDateTime.get(Calendar.DAY_OF_MONTH) + 2);

//        Log.v(LOG_TAG, "Default Date: " + defaultDate);
//        Log.v(LOG_TAG, "Default Time: " + defaultTime);

        Intent intent = getIntent();
        mUsername = intent.getStringExtra("username");
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.title_add_a_product));
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Product");
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProductsDatabaseReference = mFirebaseDatabase.getReference().child("products2");

//        mProductNameView = findViewById(R.id.edit_product_name);
        mProductNameView = findViewById(R.id.product_name_spinner);
        mLocationView = findViewById(R.id.edit_product_location);
        mQuantityView = findViewById(R.id.edit_product_quantity);
        mWeightUnitSpinner = findViewById(R.id.weight_unit_sppinner);
        mMinimumQuantity = findViewById(R.id.edit_product_minimum_quantity);
        mMaximumQuantity = findViewById(R.id.edit_product_maximum_quantity);
        mMinimumQuantityUnit = findViewById(R.id.edit_product_minimum_quantity_unit);
        mMaximumQuantityUnit = findViewById(R.id.edit_product_maximum_quantity_unit);
        mRateWeightUnit = findViewById(R.id.edit_product_rate_weight_unit);
        mPriceView = findViewById(R.id.edit_product_price);
        mDatePickedView = findViewById(R.id.date_text_view);
        mTimePickedView = findViewById(R.id.time_text_view);
        datePickerButton = findViewById(R.id.date_picker_button);
        timePickerButton = findViewById(R.id.time_picker_button);
        mFarmerPhoneNoView = findViewById(R.id.farmer_phone_number);
        mFarmerAddressView = findViewById(R.id.farmer_address);
//        imagePickerButton = findViewById(R.id.image_picker_button);

        mDatePickedView.setVisibility(View.GONE);
        mTimePickedView.setVisibility(View.GONE);


        mProductNameView.setOnTouchListener(mTouchListener);
        mLocationView.setOnTouchListener(mTouchListener);
        mQuantityView.setOnTouchListener(mTouchListener);
        mPriceView.setOnTouchListener(mTouchListener);
        datePickerButton.setOnTouchListener(mTouchListener);
        timePickerButton.setOnTouchListener(mTouchListener);
        mFarmerPhoneNoView.setOnTouchListener(mTouchListener);
        mFarmerAddressView.setOnTouchListener(mTouchListener);
        mMinimumQuantity.setOnTouchListener(mTouchListener);
        mMaximumQuantity.setOnTouchListener(mTouchListener);
//        imagePickerButton.setOnTouchListener(mTouchListener);

        ArrayAdapter<CharSequence> productNameAdapter = ArrayAdapter.createFromResource(this,
                R.array.product_names, android.R.layout.simple_spinner_dropdown_item);
        productNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProductNameView.setAdapter(productNameAdapter);
        mProductNameView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {

                    mSelectedProductName = getProductNameInEnglish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSelectedProductName = "Apple";

            }
        });

        ArrayAdapter<CharSequence> weightUnitAdapter = ArrayAdapter.createFromResource(this,
                R.array.weight_units, android.R.layout.simple_spinner_dropdown_item);
        weightUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWeightUnitSpinner.setAdapter(weightUnitAdapter);
        mWeightUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.kg))) {
                        mWeightUnit = "Kg";
                        mMaximumQuantityUnit.setText(R.string.kg);
                        mMinimumQuantityUnit.setText(R.string.kg);
                        mRateWeightUnit.setText(R.string.kg);
                    } else if (selection.equals(getString(R.string.tonne))) {
                        mWeightUnit = "Tonne";
                        mMaximumQuantityUnit.setText(R.string.tonne);
                        mMinimumQuantityUnit.setText(R.string.tonne);
                        mRateWeightUnit.setText(R.string.tonne);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mWeightUnit ="Kg";
                mMaximumQuantityUnit.setText(R.string.kg);
                mMinimumQuantityUnit.setText(R.string.kg);
            }
        });
//        mMaximumQuantity.setText("0");
//        mMinimumQuantity.setText("0");
//        mQuantityView.setText("0");
        mMaximumQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.v(LOG_TAG, "Maximum OnTextChanged called.");
                int quantity;
                try {
                    quantity = Integer.parseInt(mQuantityView.getText().toString().trim());
                } catch (NumberFormatException e) {
                    quantity = 0;
                }
//                Log.v(LOG_TAG, "Quantity: " + quantity);
                int maximumQuantity;

                try {
                    maximumQuantity = Integer.parseInt(mMaximumQuantity.getText().toString().trim());
                } catch (NumberFormatException e) {
                    maximumQuantity = 0;
                }
//                Log.v(LOG_TAG, "Max Quant: " + maximumQuantity);
                if (maximumQuantity > quantity) {
                    mMaximumQuantity.setText(String.format(Locale.getDefault(), "%d", quantity));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        mMinimumQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.v(LOG_TAG, "Minimum OnTextChanged called.");
                int quantity;
                try {

                    quantity = Integer.parseInt(mQuantityView.getText().toString().trim());
                } catch (NumberFormatException e) {
                    quantity = 0;
                }


                int minimumQuantity;
                try {
                    minimumQuantity = Integer.parseInt(mMinimumQuantity.getText().toString().trim());
                } catch (NumberFormatException e) {
                    minimumQuantity = 0;
                }
                if (minimumQuantity > quantity) {
//                mMinimumQuantity.setText(Integer.toString(quantity));
                    mMinimumQuantity.setText(String.format(Locale.getDefault(), "%d", quantity));
                }
                if (quantity>0 && minimumQuantity == 0){
                    mMinimumQuantity.setText("1");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mQuantityView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.v(LOG_TAG, "Quantity OnTextChanged called.");
                int quantity;
                try {
                    quantity = Integer.parseInt(mQuantityView.getText().toString().trim());
                } catch (NumberFormatException e) {
                    quantity = 0;

                }
//                Log.v(LOG_TAG,"Quantity: "+quantity);
                mMaximumQuantity.setText(String.format(Locale.getDefault(), "%d", quantity));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });

        timePickerButton.setEnabled(false);
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker();
            }
        });
        mDatePickedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });
        mTimePickedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker();
            }
        });


    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            int quantity;
            try {

                quantity = Integer.parseInt(mQuantityView.getText().toString().trim());
            } catch (NumberFormatException e) {
                quantity = 0;
            }

            int maximumQuantity;
            try {
                maximumQuantity = Integer.parseInt(mMaximumQuantity.getText().toString().trim());
            } catch (NumberFormatException e) {
                maximumQuantity = 0;
            }
            if (maximumQuantity > quantity || maximumQuantity == 0) {
                mMaximumQuantity.setText(String.format(Locale.getDefault(), "%d", quantity));
            }

            int minimumQuantity;
            try {
                minimumQuantity = Integer.parseInt(mMinimumQuantity.getText().toString().trim());
            } catch (NumberFormatException e) {
                minimumQuantity = 0;
            }
            if (minimumQuantity > quantity) {
//                mMinimumQuantity.setText(Integer.toString(quantity));
                mMinimumQuantity.setText(String.format(Locale.getDefault(), "%d", quantity));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    public void datePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                EditorActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Initial day selection
        );
        datePickerDialog.setMinDate(Calendar.getInstance());
        datePickerDialog.setTitle(getString(R.string.please_pick_a_date));
        datePickerDialog.show(getFragmentManager(), "DatePicker");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        datePickerButton.setVisibility(View.GONE);
        mDatePickedView.setVisibility(View.VISIBLE);

        pickedDateTime.set(year, monthOfYear, dayOfMonth);

        DateFormat dfDate = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        pickedDate = dfDate.format(pickedDateTime.getTime());
//        Log.v(LOG_TAG, "Picked Date: " + pickedDate);
        if (Integer.parseInt(pickedDate) < Integer.parseInt(defaultDate)) {
//            Log.v(LOG_TAG, "Earlier Date Picked");
            pickedDateTime = Calendar.getInstance();
            showIncorrectDateTimeDialog("date");
        }

        mDatePickedView.setText(new SimpleDateFormat("yyyy/MM/dd",Locale.getDefault()).format(pickedDateTime.getTime()));
        timePickerButton.setEnabled(true);
    }

    private void timePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                EditorActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.setTitle(getString(R.string.please_pick_a_time));
        timePickerDialog.show(getFragmentManager(), "TimePicker");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        //TODO Make sure that the datetime in ms is updated with user changes the date again.

        timePickerButton.setVisibility(View.GONE);
        mTimePickedView.setVisibility(View.VISIBLE);

        pickedDateTime.set(Calendar.HOUR, hourOfDay);
        pickedDateTime.set(Calendar.MINUTE, minute);

        DateFormat dfTime = new SimpleDateFormat("HHmm",Locale.getDefault());
        pickedTime = dfTime.format(pickedDateTime.getTime());

        if (Integer.parseInt(pickedDate) == Integer.parseInt(defaultDate) &&
                Integer.parseInt(pickedTime) < Integer.parseInt(defaultTime)) {
            pickedDateTime.set(Calendar.HOUR, Calendar.getInstance().get(Calendar.HOUR));
            pickedDateTime.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
            showIncorrectDateTimeDialog("time");
        }

        mTimePickedView.setText(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(pickedDateTime.getTime()));
//        Log.v(LOG_TAG, "Picked Time: " + pickedTime);

    }

    private void showIncorrectDateTimeDialog(final String dateOrTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setMessage(R.string.pick_future_date_time);
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                    if (dateOrTime.equals("date")) {
                        datePicker();
                    } else if (dateOrTime.equals("time")) {
                        timePicker();
                    }
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Signed in successfully. Setup the UI
                Toast.makeText(this, R.string.logged_in, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Signin was cancelled by the user. Finish the activity
//                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;

            case android.R.id.home:
                if (!hasProductChanged) {
                    if (getIntent().getExtras() == null) {
                        Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                showUnsavedChangesDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getIntent().getExtras() == null) {
                            Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_and_quit_dialog);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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

    private void showEmptyFieldsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
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
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void saveProduct() {

        final String location = mLocationView.getText().toString().trim();

        int quantity;
        try {
            quantity = Integer.parseInt(mQuantityView.getText().toString().trim());
        } catch (NumberFormatException e) {
            quantity = 0;
        }

        int minimumQuantity;
        try {
            minimumQuantity = Integer.parseInt(mMinimumQuantity.getText().toString().trim());
        } catch (NumberFormatException e) {
            minimumQuantity = 0;
        }
        int maximumQuantity;
        try {
            maximumQuantity = Integer.parseInt(mMaximumQuantity.getText().toString().trim());
        } catch (NumberFormatException e) {
            maximumQuantity = 0;
        }

        float price;
        try {
            price = Float.parseFloat(mPriceView.getText().toString().trim());
        } catch (NumberFormatException e) {
            price = 0;
        }
        float farmerPhoneNo;
        try {
            farmerPhoneNo = Float.parseFloat(mFarmerPhoneNoView.getText().toString().trim());
        } catch (NumberFormatException e) {
            farmerPhoneNo = 0;
        }
        String farmerAddress = mFarmerAddressView.getText().toString().trim();
        if (TextUtils.isEmpty(mSelectedProductName) ||
                mSelectedProductName.equals("Pick a product") ||
                quantity <= 0 ||
                minimumQuantity <= 0 ||
                maximumQuantity <= 0 ||
                price <= 0 ||
                TextUtils.isEmpty(location) ||
                TextUtils.isEmpty(farmerAddress) ||
                farmerPhoneNo <= 0
                ) {
            showEmptyFieldsDialog();
            return;
        }

        final String expiryDate = new SimpleDateFormat("yyyyMMddHHmm",Locale.ENGLISH).format(pickedDateTime.getTime());

        final int finalQuantity = quantity;
        final float finalPrice = price;

        byte[] data;
        StorageReference photoRef;


        Product product = new Product(mUsername, mSelectedProductName, finalQuantity, mWeightUnit,
                minimumQuantity, maximumQuantity, expiryDate,
                finalPrice, location, farmerPhoneNo, farmerAddress, downloadUri);
        mProductsDatabaseReference.push().setValue(product);
        finish();


    }


    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) { //only detatch if its been attached
            mProductsDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        setLocale();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
    }

    private String getProductNameInEnglish(){
        Configuration config = getResources().getConfiguration();

        // Save original location
        Locale originalLocal = config.locale;

        // Set new one for single using
        config.locale = new Locale("en");
        getResources().updateConfiguration(config, null);

        // Get search_options array for english values
        String[] searchOptionsEn = getResources().getStringArray(R.array.product_names);

        // Set previous location back
        config.locale = originalLocal;
        getResources().updateConfiguration(config, null);

        String valueForSendingToServer = searchOptionsEn[mProductNameView.getSelectedItemPosition()];
        return valueForSendingToServer;
    }
    private void setLocale(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String locale = sharedPrefs.getString(getString(R.string.settings_language_key),
                getString(R.string.settings_language_default));
//        Locale current = getResources().getConfiguration().locale;
//        if (!current.toString().equals(locale)){
            LocaleManager.setLocale(this,locale);
//        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ChangeLangContextWrapper.wrap(newBase));
    }
}
