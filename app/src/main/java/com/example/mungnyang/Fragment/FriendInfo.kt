package com.example.mungnyang.Fragment

import android.os.Parcel
import android.os.Parcelable

data class FriendInfo(
    val friendEmail: String,
    val friendNickname: String,
    val friendImageUrl: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(friendEmail)
        p0.writeString(friendNickname)
        p0.writeString(friendImageUrl)
    }

    companion object CREATOR : Parcelable.Creator<FriendInfo> {
        override fun createFromParcel(parcel: Parcel): FriendInfo {
            return FriendInfo(parcel)
        }

        override fun newArray(size: Int): Array<FriendInfo?> {
            return arrayOfNulls(size)
        }
    }
}
