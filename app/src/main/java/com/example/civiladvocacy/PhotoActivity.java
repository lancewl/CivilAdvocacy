package com.example.civiladvocacy;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private String location;
    private Official official;

    private ConstraintLayout root;
    private TextView locationView;
    private TextView officeNameView;
    private TextView officialNameView;
    private ImageView officialImage;
    private ImageView partyImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        root = findViewById(R.id.p_root);
        locationView = findViewById(R.id.locationView3);

        officeNameView = findViewById(R.id.p_office_name);
        officialNameView = findViewById(R.id.p_official_name);

        officialImage = findViewById(R.id.p_official_img);
        partyImage = findViewById(R.id.p_party_img);

        Intent intent = getIntent();
        if (intent.hasExtra("LOCATION")) {
            location = intent.getStringExtra("LOCATION");
            locationView.setText(location);
            official = (Official) intent.getSerializableExtra("OFFICIAL");
            setViews();
        }
    }

    private void setViews() {
        // Title Setup
        officeNameView.setText(official.getOfficeName());
        officialNameView.setText(official.getOfficialName());

        // Party Setup
        if (official.getOfficialParty().contains("Democrat")) {
            root.setBackgroundColor(Color.BLUE);
            partyImage.setImageResource(R.drawable.dem_logo);
        } else if (official.getOfficialParty().contains("Republican")) {
            root.setBackgroundColor(Color.RED);
            partyImage.setImageResource(R.drawable.rep_logo);
        } else {
            root.setBackgroundColor(Color.BLACK);
            partyImage.setVisibility(View.GONE);
        }

        // Image Setup
        if (official.getOfficialPhoto() != null) {
            loadRemoteImage(official.getOfficialPhoto());
        }
    }

    private void loadRemoteImage(final String imageURL) {
        Picasso.get().load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(officialImage);
    }
}
