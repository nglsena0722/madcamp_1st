package com.example.madcamp1st.contacts;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.madcamp1st.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private String[] contacts;
    private ArrayList<String> filtered;

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView contactView;

        public ContactViewHolder(TextView contactView) {
            super(contactView);
            this.contactView = contactView;
        }
    }

    public ContactAdapter(String[] contacts) {
        this.contacts = contacts;
        filtered = new ArrayList<>(Arrays.asList(contacts));
    }

    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView contactView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.textview_contacts, parent, false);

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
