package com.example.chillbill.helpers;

import androidx.fragment.app.Fragment;

import java.io.Serializable;

public interface AbstractFactory {

    public Fragment newInstance(Serializable ...serializable);

}
