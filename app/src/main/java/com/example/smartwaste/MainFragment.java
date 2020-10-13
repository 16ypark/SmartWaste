package com.example.smartwaste;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class MainFragment extends Fragment {

    private OnNewButtonSelectedListener onNewButtonSelectedListener;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button button_new = (Button) view.findViewById(R.id.button_new);
        button_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAPPED", "onNewButtonSelected() is being executed in the fragment!");
                onNewButtonSelectedListener.onNewButtonSelected();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewButtonSelectedListener) {
            onNewButtonSelectedListener = (OnNewButtonSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNewButtonSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onNewButtonSelectedListener = null;
    }

    // Container Activity must implement this interface
    public interface OnNewButtonSelectedListener {
        public void onNewButtonSelected();
    }

}