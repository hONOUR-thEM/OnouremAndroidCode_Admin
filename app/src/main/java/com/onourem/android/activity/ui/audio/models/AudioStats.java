package com.onourem.android.activity.ui.audio.models;

import java.io.Serializable;

public class AudioStats implements Serializable {

    private String name, numbers;

    public AudioStats(String name, String numbers) {
        this.name = name;
        this.numbers = numbers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }
}