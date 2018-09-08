package com.example.nissan.farmersmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";

    public static final int RC_SIGN_IN = 1;

    private ListView productsListView;

    private ProgressBar mProgressBar;

    private ProductAdapter mProductAdapter;
    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mProductsDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseUser user;

    private String locale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        locale = sharedPrefs.getString(getString(R.string.settings_language_key),
                getString(R.string.settings_language_default));
        Log.v(LOG_TAG,"Locale: "+locale);
//        switch (locale) {
//            case "English":
//                Locale localeEN = new Locale("en_US");
//                setLocaleOnCreate(localeEN);
//                break;
//            case "Nepali":
//                Locale localeNEP = new Locale("ne_NP");
//                setLocaleOnCreate(localeNEP);
//                break;
//        }
        mUsername = ANONYMOUS;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.putExtra("fab", true);
                intent.putExtra("username", mUsername);
                startActivity(intent);
            }
        });


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProductsDatabaseReference = mFirebaseDatabase.getReference().child("products2");
        mFirebaseAuth = FirebaseAuth.getInstance();

        productsListView = findViewById(R.id.list_view_product);
//        productsListView.setEmptyView(emptyTextView);

        mProgressBar = findViewById(R.id.progressBar);
        final List<Product> products = new ArrayList<>();
        mProductAdapter = new ProductAdapter(this, R.layout.list_item, products);
        productsListView.setAdapter(mProductAdapter);
        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ProductDetails.class);
                Product selectedProduct = products.get(position);
                intent.putExtra("product", selectedProduct);
                startActivity(intent);
            }
        });


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                // User is signed in.
                onSignedInInitialize();


            }
        };

    }


    private void onSignedInInitialize() {
        attachDatabaseReadListener();

    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        detatchDatabaseReadListener();
    }


    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) { //only attach if its been detached
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Product product = dataSnapshot.getValue(Product.class);
                    product.setProductFirebaseKey(dataSnapshot.getKey());
                    mProductAdapter.insert(product, 0); //0  = new appears at top

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            mProductsDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detatchDatabaseReadListener() {
        if (mChildEventListener != null) { //only detatch if its been attached
            mProductsDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Signed in successfully. Setup the UI
                Toast.makeText(this, R.string.logged_in, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String locale_RES = sharedPrefs.getString(getString(R.string.settings_language_key),
                getString(R.string.settings_language_default));
//        if (!locale.equals(locale_RES)) {
//            switch (locale_RES) {
//                case "English":
//                    Locale localeEN = new Locale("en_US");
//                    setLocale(localeEN);
//                    break;
//                case "Nepali":
//                    Locale localeNEP = new Locale("ne-rNP");
//                    setLocale(localeNEP);
//                    break;
//            }
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detatchDatabaseReadListener();
        mProductAdapter.clear();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        invalidateOptionsMenu();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem logInItem = menu.findItem(R.id.log_in_menu);
        MenuItem logOutItem = menu.findItem(R.id.log_out_menu);
        if (user == null) {
//            Log.v(LOG_TAG, "USER: " + user);

            logInItem.setVisible(true);
            logOutItem.setVisible(false);
        } else {
//            Log.v(LOG_TAG, "USER: " + user);

            logInItem.setVisible(false);
            logOutItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_menu:
                //Sign out
                AuthUI.getInstance().signOut(this);
                user = null;
                supportInvalidateOptionsMenu();
                invalidateOptionsMenu();
                return true;
            case R.id.log_in_menu:
                //Sign out
                onSignedOutCleanup();
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build())
                        ).build(), RC_SIGN_IN);
                supportInvalidateOptionsMenu();
                invalidateOptionsMenu();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setLocaleOnCreate(Locale locale) {
        Locale.setDefault(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
    }

    public void setLocale(Locale locale) {
        Locale.setDefault(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        recreate();
        //finish();
        //startActivity(getIntent());
    }
}
