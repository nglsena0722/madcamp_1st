package com.example.phonebookimagetotab;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    Fragment_First main;
    private String[] contacts;
    private ArrayList<String> filtered;

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView contactView;

        public ContactViewHolder(TextView contactView) {
            super(contactView);
            this.contactView = contactView;
        }
    }

    public ContactAdapter(Fragment_First main, String[] contacts) {
        this.main = main;
        this.contacts = contacts;
        filtered = new ArrayList<>(Arrays.asList(contacts));
    }

    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView contactView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text, parent, false);

        return new ContactViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.contactView.setText(filtered.get(position));
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void filter(String text) {
        if(text.isEmpty())
            filtered = new ArrayList<>(Arrays.asList(contacts));
        else {
            filtered = new ArrayList<>();
            text = text.toLowerCase();
            for (String item : contacts) {
                //match by name or phone
                if (item.toLowerCase().contains(text)) {
                    filtered.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
