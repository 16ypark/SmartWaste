package com.example.smartwaste;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {

    private OnNewButtonTappedListener onNewButtonTappedListener;

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
                onNewButtonTappedListener.onNewButtonTapped();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewButtonTappedListener) {
            onNewButtonTappedListener = (OnNewButtonTappedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNewButtonTappedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onNewButtonTappedListener = null;

    }

    // Container Activity must implement this interface
    public interface OnNewButtonTappedListener {
        public void onNewButtonTapped();
    }

}