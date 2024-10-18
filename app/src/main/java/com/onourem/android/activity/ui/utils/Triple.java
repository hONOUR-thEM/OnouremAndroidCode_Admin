package com.onourem.android.activity.ui.utils;

import java.io.Serializable;

public class Triple<A, B, C> implements Serializable {
    public A first;
    public B second;
    public C third;

    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
