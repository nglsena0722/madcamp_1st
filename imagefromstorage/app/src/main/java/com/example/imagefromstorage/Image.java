package com.example.imagefromstorage;

import android.net.Uri;

public class Image {
    final Uri uri;
    final String name;

    public Image(Uri uri, String name) {
        this.uri = uri;
        this.name = name;
    }
}