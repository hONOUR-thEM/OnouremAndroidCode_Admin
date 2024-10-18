package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReasonList     //        this.id = id;
    (//    public Integer getId() {
    //        return id;
    //    }
    //
    //    public void setId(Integer id) {
    //        this.id = id;
    //    }
    //    @SerializedName("id")
    //    @Expose
    //    private int id;
    @field:Expose @field:SerializedName("reason") var reason: String
) {
    var otherReason: String? = null

}