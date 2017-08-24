package com.gylgroup.gpmovil;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.gylgroup.gpmovil.model.DireccionLocal;

import java.util.ArrayList;

import static com.gylgroup.gpmovil.R.string.direccion;

public class DireccionActivity extends BaseActivity  implements DireccionesDialog.Listener {

    private static final String TAG = DireccionActivity.class.getSimpleName();
    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private Address mAddressValidated;
    private String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    private TextView mLocationAddressTextView;


    /**
     * Kicks off the request to fetch an address when pressed.
     */
    private SharedPreferences sharedPref;
    private EditText mDireccionIdEditText;
    private EditText mDireccionEditText;
    private EditText mPisoDeptoEditText;
    private ImageView mEliminarButton;
    private ImageView mAceptarButton;
    private ImageView mCancelarButton;
    private ImageView mValidarButton;
    private ArrayList<Address> addresses;
    private DireccionLocal direccionLocal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        permissions=new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
        grantResults=new int[] {PackageManager.PERMISSION_GRANTED};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion);
        setupActionBar();
        updateValuesFromBundle(savedInstanceState);
    }

    @Override
    protected void dale() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mResultReceiver = new AddressResultReceiver(new Handler());

        mLocationAddressTextView = (TextView) findViewById(R.id.location_address_view);
        mDireccionEditText = (EditText) findViewById(R.id.direccionDescripcion);
        mDireccionIdEditText = (EditText) findViewById(R.id.direccionId);
        mPisoDeptoEditText = (EditText) findViewById(R.id.pisoDepto);
        mDireccionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mAddressValidated = null;
                updateUIWidgets();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mAceptarButton = (ImageView) findViewById(R.id.aceptarButton);
        mEliminarButton = (ImageView) findViewById(R.id.eliminarButton);
        int mode = getIntent().getIntExtra(Constants.ABM_MODE, 0);
        if (mode == Constants.ABM_ALTA){
            // Set defaults, then update using values stored in the Bundle.
            mAddressValidated = null;
            mAddressOutput = "";
            mEliminarButton.setVisibility(View.INVISIBLE);
        }
        else {
            // Edit
            String id=getIntent().getStringExtra(Constants.EDIT_ID);
            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
            String json = sharedPreferences.getString("dir_"+id, null);
            direccionLocal=json == null ? null : new Gson().fromJson(json, DireccionLocal.class);
            mDireccionIdEditText.setText(direccionLocal.getId());
            mDireccionEditText.setText(Utils.AddressToString(direccionLocal.getAddress()));
            mPisoDeptoEditText.setText(direccionLocal.getPisoDepto());
            mEliminarButton.setVisibility(View.VISIBLE);
            mAddressValidated=direccionLocal.getAddress();
        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        updateUIWidgets();
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressValidated = savedInstanceState.getParcelable(ADDRESS_REQUESTED_KEY);
            }
        }
    }
    @SuppressWarnings("unused")
    public void cancelarButtonHandler(View view) {
        finish();
    }

    @SuppressWarnings("unused")
    public void validarButtonHandler(View view) {
        startLocationIntentService();
    }

    @SuppressWarnings("unused")
    public void aceptarButtonHandler(View view) {
        try {

            DireccionLocal local=new DireccionLocal();
            local.setId(mDireccionIdEditText.getText().toString());
            local.setAddress(mAddressValidated);
            local.setPisoDepto(mPisoDeptoEditText.getText().toString());
            String json = mAddressValidated == null ? null : new Gson().toJson(local);
            SharedPreferences.Editor editor=sharedPref.edit();
            editor.putString(String.format("dir_%s", mDireccionIdEditText.getText()), json);
            editor.commit();
            finish();
        }
        catch(Exception e) {
            Log.d("Error",e.getMessage());
        }
    }
    @SuppressWarnings("unused")
    public void eliminarButtonHandler(View view) {
        try {

            SharedPreferences.Editor editor=sharedPref.edit();
            editor.remove("dir_" + getIntent().getStringExtra(Constants.EDIT_ID));
            editor.commit();
            finish();
        }
        catch(Exception e) {
            Log.d("Error",e.getMessage());
        }
    }
    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startLocationIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchLocationIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.ADDRESS_DATA_EXTRA, mDireccionEditText.getText().toString());

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }


    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
        if (mAddressValidated!=null) {
            mAceptarButton.setVisibility(View.VISIBLE);
        } else {
            mAceptarButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Shows a toast with the given text.
     */
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putParcelable(ADDRESS_REQUESTED_KEY, mAddressValidated);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                addresses=resultData.getParcelableArrayList(Constants.RESULT_DATA_KEY);
                if (addresses.size()==1) {
                    String elegida=Utils.AddressToString(addresses.get(0));
                    if (!elegida.isEmpty()) {
                        mDireccionEditText.setText(elegida);
                        mAddressValidated = addresses.get(0);
                    }
                }
                if (addresses.size()>1) {
                    FragmentManager manager=getSupportFragmentManager();
                    Fragment frag = manager.findFragmentByTag("dummy");
                    if (frag != null) {
                        manager.beginTransaction().remove(frag).commit();
                    }
                    DireccionesDialog dialog=new DireccionesDialog();
                    dialog.setAddresses(addresses);
                    dialog.show(manager,"dummy");
                }
            }
            else if(resultCode==Constants.FAILURE_RESULT) {
                String message=resultData.getString(Constants.RESULT_ERROR_KEY);
                Utils.error(DireccionActivity.this,message);
            }
            updateUIWidgets();
        }
    }



    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    @Override
    public void onFinishDireccionesDialog(Address address) {
        mDireccionEditText.setText(Utils.AddressToString(address));
        mAddressValidated=address;
        updateUIWidgets();
    }
}
