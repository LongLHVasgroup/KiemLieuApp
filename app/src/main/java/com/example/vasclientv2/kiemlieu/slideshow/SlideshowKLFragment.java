package com.example.vasclientv2.kiemlieu.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.vasclientv2.R;

public class SlideshowKLFragment extends Fragment {

    private com.example.vasclientv2.kiemlieu.slideshow.SlideshowKLViewModel slideshowKLViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowKLViewModel =
                new ViewModelProvider(this).get(com.example.vasclientv2.kiemlieu.slideshow.SlideshowKLViewModel.class);
        View root = inflater.inflate(R.layout.kl_fragment_slideshow, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowKLViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}