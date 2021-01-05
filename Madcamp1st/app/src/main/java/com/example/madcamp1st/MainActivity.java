package com.example.madcamp1st;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.madcamp1st.contacts.Fragment_Contacts;
import com.example.madcamp1st.games.Fragment_Games;
import com.example.madcamp1st.images.Fragment_Images;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_BOTH = 0;
    private final int REQUEST_CODE_READ_CONTACTS = 1;
    private final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.madcamp1st.R.layout.activity_main);

        boolean permissionReadContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        boolean permissionReadExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(!permissionReadContacts && !permissionReadExternalStorage)
            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_BOTH);
        else if(!permissionReadContacts)
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        else if(!permissionReadExternalStorage)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
        else
            createView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_BOTH || requestCode == REQUEST_CODE_READ_CONTACTS || requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE)
            createView();
    }

    private void createView() {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager_main);
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new Fragment_Contacts());
        adapter.addFragment(new Fragment_Images());
        adapter.addFragment(new Fragment_Games());
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout =(TabLayout) findViewById(R.id.tabLayout_main);
        tabLayout.setupWithViewPager(mViewPager);

        ImageView imgView= new ImageView(this);
        imgView.setImageResource(R.drawable.tab_icon_contacts);
        imgView.setPadding(10,10,10,10);
        tabLayout.getTabAt(0).setCustomView(imgView);

        imgView= new ImageView(this);
        imgView.setImageResource(R.drawable.tab_icon_images);
        imgView.setPadding(10,10,10,10);
        tabLayout.getTabAt(1).setCustomView(imgView);

        imgView= new ImageView(this);
        imgView.setImageResource(R.drawable.tab_icon_games);
        imgView.setPadding(10,10,10,10);
        tabLayout.getTabAt(2).setCustomView(imgView);
    }
}