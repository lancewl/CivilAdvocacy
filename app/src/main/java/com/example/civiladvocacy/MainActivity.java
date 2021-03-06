package com.example.civiladvocacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;

    private final List<Official> officialList = new ArrayList<>();  // Main content is here

    private TextView locationView;
    private TextView errorView;

    private RecyclerView recyclerView; // Layout's recyclerview

    private OfficialAdapter mAdapter; // Data to recyclerview adapter

    private static final int INFO_CODE = 1;
    private static final int OFFICIAL_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationView = findViewById(R.id.locationView);
        errorView = findViewById(R.id.error_dialog);

        recyclerView = findViewById(R.id.offcialRecycler);
        mAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new OfficialDecoration(this, R.drawable.separator));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        determineLocation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.info_btn) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivityForResult(intent, INFO_CODE);
            return true;
        } else if (item.getItemId() == R.id.location_btn) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Address");
            final EditText input = new EditText(this);
            input.setGravity(Gravity.CENTER);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String inputAddr = input.getText().toString();
                    doCivicDownload(inputAddr);
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // From OnClickListener
    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks
        int pos = recyclerView.getChildLayoutPosition(v);
        Official o = officialList.get(pos);

        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("OFFICIAL", o);
        intent.putExtra("LOCATION", locationView.getText().toString());
        startActivityForResult(intent, OFFICIAL_CODE);
    }

    // Location Code
    private void determineLocation() {
        if (checkPermission()) {
            if (checkNetwork()) {
                errorView.setVisibility(View.GONE);
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                String addr = getAddress(location);
                                doCivicDownload(addr);
                            }
                        })
                        .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show());
            } else {
                locationView.setText("No Data For Location");
                errorView.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    locationView.setText("No Data For Location");
                }
            }
        }
    }

    private Boolean checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private String getAddress(Location loc) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String res = "0";

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            res = addresses.get(0).getAddressLine(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private void doCivicDownload(String addr) {
        GoogleCivicApiRunnable loaderTaskRunnable = new GoogleCivicApiRunnable(this, addr);
        new Thread(loaderTaskRunnable).start();
    }

    public void updateCivicData(String addr, List<Official> oList) {
        if (addr == null) {
            locationView.setText("No Data For Location");
        } else {
            locationView.setText(addr);
        }
        officialList.clear();
        officialList.addAll(oList);
        mAdapter.notifyDataSetChanged();
    }
}