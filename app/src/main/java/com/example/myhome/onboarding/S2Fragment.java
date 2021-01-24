package com.example.myhome.onboarding;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myhome.R;


public class S2Fragment extends Fragment {

    public static S2Fragment newInstance(String param1, String param2) {
        S2Fragment fragment = new S2Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_s2, container, false);
        return inflater.inflate(R.layout.fragment_s2, container, false);
    }
}
