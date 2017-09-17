package com.bobdylan.haritha.criminalintentapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by haritha on 17/9/17.
 */

public class UserDefaultsFragment extends Fragment{
    private EditText mTitle, mFlat, mPhone;
    private UserDefaults user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = UserDefaults.getUserDefaults(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v=inflater.inflate(R.layout.fragment_user_defaults, container,false);
        mTitle= (EditText) v.findViewById(R.id.user_title);
        mFlat= (EditText) v.findViewById(R.id.user_flat_number);
        mPhone= (EditText) v.findViewById(R.id.user_phone_no);
        mPhone.setText(user.getPhoneno());
        mTitle.setText(user.getName());
        mFlat.setText(user.getFlat());

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mFlat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user.setFlat(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user.setPhoneno(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        user.updateUser();
    }


}
