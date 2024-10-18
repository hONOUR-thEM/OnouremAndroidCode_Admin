package com.onourem.android.activity.ui.audio.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Language implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("languageName")
    @Expose
    private String languageName;
    @SerializedName("languageCode")
    @Expose
    private String languageCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}