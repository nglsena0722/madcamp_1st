package com.example.forviewpagerwithtab;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.applyimagetotab.R;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    com.example.forviewpagerwithtab.SectionPageAdapter adapter = new com.example.forviewpagerwithtab.SectionPageAdapter(getSupportFragmentManager());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout =(TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        ImageView imgView= new ImageView(MainActivity.this);
        imgView.setImageResource(R.drawable.tab_iconphonebook);
        imgView.setPadding(10,10,10,10);
        tabLayout.getTabAt(0).setCustomView(imgView);

        imgView= new ImageView(MainActivity.this);
        imgView.setImageResource(R.drawable.tab_iconimage);
        imgView.setPadding(10,10,10,10);
        tabLayout.getTabAt(1).setCustomView(imgView);

        imgView= new ImageView(MainActivity.this);
        imgView.setImageResource(R.drawable.tab_iconfree);
        imgView.setPadding(10,10,10,10);
        tabLayout.getTabAt(2).setCustomView(imgView);

//        tabLayout.getTabAt(0).setIcon(R.drawable.tab_iconphonebook);
//        tabLayout.getTabAt(1).setIcon(R.drawable.tab_iconimage);
//        tabLayout.getTabAt(2).setIcon(R.drawable.tab_iconfree);
    }
    public void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new com.example.forviewpagerwithtab.Fragment_First(), "phonebook");
        adapter.addFragment(new com.example.forviewpagerwithtab.Fragment_Second(), "images");
        adapter.addFragment(new com.example.forviewpagerwithtab.Fragment_Third(), "free");

        viewPager.setAdapter(adapter);
    }
}