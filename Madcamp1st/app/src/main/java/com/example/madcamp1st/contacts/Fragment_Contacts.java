package com.example.madcamp1st.contacts;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madcamp1st.R;

import java.util.ArrayList;

public class Fragment_Contacts extends Fragment {
    private ContactAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
            onPermissionGranted();
    }

    private void onPermissionGranted() {
        getActivity().findViewById(R.id.reject_contacts).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.relativeLayout_contacts).setVisibility(View.VISIBLE);

        showContacts();

        SearchView searchView = (SearchView) getActivity().findViewById(R.id.searchView_contacts);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return true;
            }
        });
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

        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView_contacts);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ContactAdapter(contacts);
        recyclerView.setAdapter(mAdapter);
    }
}