package com.example.allcontacts;

import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends Activity {
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // should be in onViewCreated
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
                Toast.makeText(this, "debug", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean customeCheckPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadContactsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            Snackbar.make(this.findViewById(android.R.id.content), "연락처를 사용하려면 권한이 필요합니다", Snackbar.LENGTH_INDEFINITE)
                    .setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View V) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    PERMISSION_REQUEST_READ_CONTACTS);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        }
    }

    private String[] loadContacts() {
        ProgressDialog pd;
        ArrayList<String> contacts = new ArrayList<String>();

        pd = ProgressDialog.show(MainActivity.this, "Loading Contacts", "Please Wait");

        Cursor c = getContentResolver().query(
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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter mAdapter = new ContactAdapter(this, contacts);
        recyclerView.setAdapter(mAdapter);
    }
}