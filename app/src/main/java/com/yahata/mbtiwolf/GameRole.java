package com.yahata.mbtiwolf;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable; // ★★★ この行を追加 ★★★

// ★★★ Parcelableの後ろに ", Serializable" を追加 ★★★
public class GameRole implements Parcelable, Serializable {

    private final String name;
    private final String description;

    public GameRole(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // --- 以下、変更なし ---
    protected GameRole(Parcel in) {
        name = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GameRole> CREATOR = new Creator<GameRole>() {
        @Override
        public GameRole createFromParcel(Parcel in) {
            return new GameRole(in);
        }

        @Override
        public GameRole[] newArray(int size) {
            return new GameRole[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}