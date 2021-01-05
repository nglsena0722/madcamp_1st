package com.example.madcamp1st.contacts;

import android.graphics.drawable.Drawable;

public class ContactItem {
    private Drawable iconDrawable;
    private String titleStr ;
    private String descStr ;
    private Drawable callDrawable ;

    public void setIcon(Drawable icon) {
        iconDrawable =  icon;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }
    public void setCall(Drawable call) {
       callDrawable = call ;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
    public Drawable getCall() {
        return this.callDrawable ;
    }
}
