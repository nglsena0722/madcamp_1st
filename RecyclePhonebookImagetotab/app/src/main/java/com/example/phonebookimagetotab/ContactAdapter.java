package com.example.phonebookimagetotab;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phonebookimagetotab.Fragment_First;
import com.example.phonebookimagetotab.MainActivity;
import com.example.phonebookimagetotab.R;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    Fragment_First main;
    private String[] contacts;

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
    }

    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView contactView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text, parent, false);

        return new ContactViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.contactView.setText(contacts[position]);
    }

    @Override
    public int getItemCount() {
        return contacts.length;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void filter(String text) {
    //        if(text.isEmpty()){
    //            contacts.Clear();
    //            contacts.addAll(itemsCopy);
    //        } else{
    //            ArrayList<PhoneBookItem> result = new ArrayList<>();
    //            text = text.toLowerCase();
    //            for(PhoneBookItem item: itemsCopy){
    //                //match by name or phone
    //                if(item.name.toLowerCase().contains(text) || item.phone.toLowerCase().contains(text)){
    //                    result.add(item);
    //                }
    //            }
    //            items.clear();
    //            items.addAll(result);
    //        }
    //        notifyDataSetChanged();
    }
}
