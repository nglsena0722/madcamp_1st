package com.example.phonebookimagetotab;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class Fragment_First extends Fragment {
    public ViewPager viewPager;
    ListView list;
    LinearLayout ll;

    private static final int PERMISSION_REQUEST_READ_CONTACTS = 0;

    public Fragment_First() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        ll = (LinearLayout) view.findViewById(R.id.LinearLayout1);
        list = (ListView) view.findViewById(R.id.listView1);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(checkPermission(Manifest.permission.READ_CONTACTS)) {
            showContactsAsync();
        } else {
            requestReadContactsPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_READ_CONTACTS){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showContactsAsync();
            }
            else{
                Toast.makeText(getContext(), "debug", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkPermission(String permission) {
        return ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadContactsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                Snackbar.make(ll, "연락처를 사용하려면 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE)
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

    class LoadContactsAsycn extends AsyncTask<Void, Void, ArrayList<String>> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pd = ProgressDialog.show(getContext(), "Loading Contacts",
                    "Please Wait");
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            ArrayList<String> contacts = new ArrayList<String>();

            Cursor c = getContext().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null);

            int nameColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (c.moveToNext()) {
                String contactName = c.getString(nameColumn);
                String phNumber = c.getString(numberColumn);

                contacts.add(contactName + ":" + phNumber);
            }
            c.close();

            return contacts;
        }

        @Override
        protected void onPostExecute(ArrayList<String> contacts) {
            // TODO Auto-generated method stub
            super.onPostExecute(contacts);

            pd.cancel();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity().getApplicationContext(), R.layout.text, contacts);

            list.setAdapter(adapter);
        }
    }

    private void showContactsAsync() {
        LoadContactsAsycn lca = new LoadContactsAsycn();
        lca.execute();
    }
}