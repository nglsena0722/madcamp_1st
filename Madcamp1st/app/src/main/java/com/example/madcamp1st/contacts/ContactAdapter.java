package com.example.madcamp1st.contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madcamp1st.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private String[] contacts;
    private ArrayList<String> filtered;
    private ArrayList<ContactItem> mData = null;

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public ImageView android;
        public TextView name;
        public TextView phone;
        public ImageView calling;

        public ContactViewHolder(View itemView) {
            super(itemView);
            android = itemView.findViewById(R.id.android);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            calling = itemView.findViewById(R.id.calling);
        }
    }

    public ContactAdapter(String[] contacts) {
        this.contacts = contacts;
        filtered = new ArrayList<>(Arrays.asList(contacts));

        ArrayList<ContactItem> mData = new ArrayList<ContactItem>();
        int l = contacts.length;

        for (int x = 0; x < l; x++) {
            for (int y = 0; y < contacts[x].length(); y++) {
                if (contacts[x].charAt(y) == ':') {
                    ContactItem temp = new ContactItem();
                    temp.setTitle(contacts[x].substring(0, y));
                    temp.setDesc(contacts[x].substring(y+1, contacts[x].length()));
                    mData.add(temp);
                    break;
                }
            }

        }

        this.mData = mData;
    }

    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.textview_contacts, parent, false);

        return new ContactAdapter.ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        int temp = 0;
        int l = contacts[position].length();
        for (int x = 0; x < l; x++) {
            if (contacts[position].charAt(x) == ':') {
                temp = x;
                break;
            }
        }

        holder.android.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.icon_android));
        holder.name.setText(contacts[position].substring(0, temp));
        holder.phone.setText(contacts[position].substring(temp + 1, l));
        holder.calling.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.icon_call));
        int finalTemp = temp;

        holder.calling.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("tel:" + contacts[position].substring(finalTemp + 1, l)));

                try {
                    c.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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