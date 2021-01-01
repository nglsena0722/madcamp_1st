package com.example.phonebookimagetotab;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    Fragment_Second main;
    private Image[] images;

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageButton imageButton;
        public TextView name;

        public ImageViewHolder(View imageView) {
            super(imageView);
            imageButton = imageView.findViewById(R.id.custom_image_button_image);
            name = imageView.findViewById(R.id.custom_image_button_name);
        }
    }

    public ImageAdapter(Fragment_Second main, Image[] images) {
        this.main = main;
        this.images = images;
    }

    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View imageView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_image_button, parent, false);

        return new ImageViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Uri uri = images[position].uri;
        String name = images[position].name;

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void onClick(View view) {
                Log.d("2","dddd in onclick");
                main.zoomImageFromThumb(holder.imageButton, uri);
            }
        });

        try {
            InputStream is = main.getContext().getContentResolver().openInputStream(uri);
            holder.imageButton.setImageDrawable(Drawable.createFromStream(is, uri.toString()));
            holder.name.setText(name);
        } catch (FileNotFoundException e) {
            holder.imageButton.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return images.length;
    }
}