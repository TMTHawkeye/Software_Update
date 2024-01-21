package com.project.Helper;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.io.Serializable;

public class AppsModel implements Parcelable {
    Drawable icon_of_app;
    String appname;
    String packageName;
    String current_version;


    public AppsModel(Drawable icon_of_app, String appname, String packageName, String current_version) {
        this.icon_of_app = icon_of_app;
        this.appname = appname;
        this.packageName = packageName;
        this.current_version = current_version;
    }

    protected AppsModel(Parcel in) {
        appname = in.readString();
        packageName = in.readString();
        current_version = in.readString();
    }

    public static final Creator<AppsModel> CREATOR = new Creator<AppsModel>() {
        @Override
        public AppsModel createFromParcel(Parcel in) {
            return new AppsModel(in);
        }

        @Override
        public AppsModel[] newArray(int size) {
            return new AppsModel[size];
        }
    };

    public Drawable getIcon_of_app() {
        return icon_of_app;
    }

    public void setIcon_of_app(Drawable icon_of_app) {
        this.icon_of_app = icon_of_app;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getCurrent_version() {
        return current_version;
    }

    public void setCurrent_version(String current_version) {
        this.current_version = current_version;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appname);
        dest.writeString(packageName);
        dest.writeString(current_version);
    }
}
