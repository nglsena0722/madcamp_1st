package com.example.phonebookimagetotab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    Fragment_First main;
    private String[] contacts;
    private ArrayList<String> filtered;
    private ArrayList<ContactItem> mData = null;
    Drawable d;

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

//                public TextView contactView;
//
//        public ContactViewHolder(TextView contactView) {
//            super(contactView);
//            this.contactView = contactView;
//        }
    }
    ContactAdapter(ArrayList<ContactItem> list) {
        mData = list;
    }
    public ContactAdapter(Fragment_First main, String[] contacts) {
        this.main = main;
        this.contacts = contacts;
        filtered = new ArrayList<>(Arrays.asList(contacts));

        ArrayList<ContactItem> mData = new ArrayList<ContactItem>();
        int l = contacts.length;
        Log.d("2", "dddd "+l);

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

//    public ContactAdapter(Fragment_First main, String[] contacts) {
//        this.main = main;
//        this.contacts = contacts;
//        filtered = new ArrayList<>(Arrays.asList(contacts));
//    }

    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.text, parent, false);

        return new ContactAdapter.ContactViewHolder(view);

        //        TextView contactView = (TextView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.text, parent, false);
//
//        return new ContactViewHolder(contactView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        ContactItem item = mData.get(position);
        int temp = 0;
        int l = contacts[position].length();
        for (int x = 0; x < l; x++) {
            if (contacts[position].charAt(x) == ':') {
                temp = x;
                break;
            }
        }
        holder.android.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.iconandroid));
        holder.name.setText(contacts[position].substring(0, temp));
        holder.phone.setText(contacts[position].substring(temp+1, l));
        holder.calling.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.iconcall));
        int finalTemp = temp;

//        holder.android.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_INSERT, Uri.parse("content://contacts/people"));
//                main.startActivity(intent);
//            }
//        });

        holder.calling.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("tel:"+contacts[position].substring(finalTemp+1, l)));

                try{
                    c.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();

                }

//                Toast.makeText(v.getContext(), contacts[position].substring(finalTemp +1, l), Toast.LENGTH_SHORT).show();
            }
        });

        //        holder.contactView.setText(filtered.get(position));
//
//        int temp = 0;
//        int l = contacts[position].length();
//        for (int x = 0; x < l; x++) {
//            if (contacts[position].charAt(x) == ':') {
//                temp = x;
//                break;
//            }
//        }

//        int finalTemp = temp;
//        holder.contactView.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                Context c = v.getContext();
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("tel:"+contacts[position].substring(finalTemp+1, l)));
//
//                try{
//                    c.startActivity(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                }
//
////                Toast.makeText(v.getContext(), contacts[position].substring(finalTemp+1, l), Toast.LENGTH_SHORT).show();
//            }
//        });
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
