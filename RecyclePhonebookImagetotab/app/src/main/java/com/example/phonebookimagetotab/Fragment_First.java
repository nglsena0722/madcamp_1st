package com.example.phonebookimagetotab;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.phonebookimagetotab.R;
import com.example.phonebookimagetotab.ContactAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class Fragment_First extends Fragment {
    public ViewPager viewPager;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 0;

    public Fragment_First() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(customeCheckPermission(Manifest.permission.READ_CONTACTS)) {
            showContacts();
        } else {
            requestReadContactsPermission();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_READ_CONTACTS){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showContacts();
            }
            else{
                LayoutInflater mInflater =
                        (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                mInflater.inflate(R.layout.rejectaction, getActivity().findViewById(android.R.id.content), true);
                Toast.makeText(getContext(), "debug", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean customeCheckPermission(String permission) {
        return ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadContactsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "연락처를 사용하려면 권한이 필요합니다", Snackbar.LENGTH_INDEFINITE)
                    .setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View V) {
                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                    PERMISSION_REQUEST_READ_CONTACTS);
                        }
                    }).show();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        }
    }

    private String[] loadContacts() {
        ProgressDialog pd;
        ArrayList<String> contacts = new ArrayList<String>();

        pd = ProgressDialog.show(getContext(), "Loading Contacts", "Please Wait");

        Cursor c = getContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                null, null, null);

        int nameColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int numberColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        while (c.moveToNext()) {
            String contactName = c.getString(nameColumn);
            String phNumber = c.getString(numberColumn);

            contacts.add(contactName + ":" + phNumber);
        }
        c.close();
        pd.cancel();

        return contacts.toArray(new String[0]);
    }

    private void showContacts() {
        String[] contacts = loadContacts();

        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter mAdapter = new ContactAdapter(this, contacts);
        recyclerView.setAdapter(mAdapter);
    }
}


