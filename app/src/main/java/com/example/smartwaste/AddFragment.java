package com.example.smartwaste;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class AddFragment extends Fragment {

    private OnApproveButtonTappedListener onApproveButtonTappedListener;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_add, container, false);

        Button button_new = (Button) view.findViewById(R.id.button_approve);
        button_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onApproveButtonTappedListener.onApproveButtonTapped();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnApproveButtonTappedListener) {
            onApproveButtonTappedListener = (OnApproveButtonTappedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnApproveButtonTappedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onApproveButtonTappedListener = null;
    }

    // Container Activity must implement this interface
    public interface OnApproveButtonTappedListener {
        public void onApproveButtonTapped();
    }
}