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

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private String location;
    private Official official;

    private ScrollView scrollView;
    private TextView locationView;
    private TextView officeNameView;
    private TextView officialNameView;
    private TextView partyView;
    private TextView addressTitleView;
    private TextView addressView;
    private TextView phoneTitleView;
    private TextView phoneView;
    private TextView emailTitleView;
    private TextView emailView;
    private TextView websiteTitleView;
    private TextView websiteView;
    private ImageView officialImage;
    private ImageView partyImage;
    private ImageView facebookImage;
    private ImageView twitterImage;
    private ImageView youtubeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        scrollView = findViewById(R.id.scrollv);
        locationView = findViewById(R.id.locationView2);

        officeNameView = findViewById(R.id.a_office_name);
        officialNameView = findViewById(R.id.a_official_name);
        partyView = findViewById(R.id.a_party);
        addressTitleView = findViewById(R.id.a_addressT);
        addressView = findViewById(R.id.a_address);
        phoneTitleView = findViewById(R.id.a_phoneT);
        phoneView = findViewById(R.id.a_phone);
        emailTitleView = findViewById(R.id.a_emailT);
        emailView = findViewById(R.id.a_email);
        websiteTitleView = findViewById(R.id.a_websiteT);
        websiteView = findViewById(R.id.a_website);

        officialImage = findViewById(R.id.a_official_img);
        partyImage = findViewById(R.id.a_party_img);
        facebookImage = findViewById(R.id.a_facebook_img);
        twitterImage = findViewById(R.id.a_twitter_img);
        youtubeImage = findViewById(R.id.a_youtube_img);

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
        partyView.setText("(" + official.getOfficialParty() + ")");

        // Party Setup
        if (official.getOfficialParty().contains("Democrat")) {
            scrollView.setBackgroundColor(Color.BLUE);
            partyImage.setImageResource(R.drawable.dem_logo);
        } else if (official.getOfficialParty().contains("Republican")) {
            scrollView.setBackgroundColor(Color.RED);
            partyImage.setImageResource(R.drawable.rep_logo);
        } else {
            scrollView.setBackgroundColor(Color.BLACK);
            partyImage.setVisibility(View.GONE);
        }

        // Image Setup
        if (official.getOfficialPhoto() != null) {
            loadRemoteImage(official.getOfficialPhoto());
        }

        // Description Setup
        if (official.getOfficialAddr() == null) {
            addressTitleView.setVisibility(View.GONE);
            addressView.setVisibility(View.GONE);
        } else {
            String text = official.getOfficialAddr();
            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
            addressView.setText(content);
        }
        if (official.getOfficialPhone() == null) {
            phoneTitleView.setVisibility(View.GONE);
            phoneView.setVisibility(View.GONE);
        } else {
            String text = official.getOfficialPhone();
            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
            phoneView.setText(content);
        }
        if (official.getOfficialEmail() == null) {
            emailTitleView.setVisibility(View.GONE);
            emailView.setVisibility(View.GONE);
        } else {
            String text = official.getOfficialEmail();
            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
            emailView.setText(content);
        }
        if (official.getOfficialUrl() == null) {
            websiteTitleView.setVisibility(View.GONE);
            websiteView.setVisibility(View.GONE);
        } else {
            String text = official.getOfficialUrl();
            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
            websiteView.setText(content);
        }

        // Channels Setup
        if (official.getOfficialFb() == null) {
            facebookImage.setVisibility(View.GONE);
        }
        if (official.getOfficialTw() == null) {
            twitterImage.setVisibility(View.GONE);
        }
        if (official.getOfficialYt() == null) {
            youtubeImage.setVisibility(View.GONE);
        }
    }

    private void loadRemoteImage(final String imageURL) {
        Picasso.get().load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(officialImage);
    }

    public void facebookClicked(View v) {
        String name = official.getOfficialFb();
        String FACEBOOK_URL = "https://www.facebook.com/" + name;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + name;
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url }
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            facebookIntent.setData(Uri.parse(urlToUse));
            startActivity(facebookIntent);
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = official.getOfficialTw();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name)); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void youTubeClicked(View v) {
        String name = official.getOfficialYt();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void photoClicked(View v) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("OFFICIAL", official);
        intent.putExtra("LOCATION", locationView.getText().toString());
        startActivityForResult(intent, 1);
    }
}
