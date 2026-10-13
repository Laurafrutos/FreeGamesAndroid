package com.laura.superheroes_android.data

import androidx.appcompat.widget.DialogTitle
import com.google.gson.annotations.SerializedName

data class Game(

    @SerializedName("id") val id : Int,
    @SerializedName ("title") val title: String,
    @SerializedName("thumbnail")val thumbail: String,
    @SerializedName("short_Description")val shortDescription: String
)


