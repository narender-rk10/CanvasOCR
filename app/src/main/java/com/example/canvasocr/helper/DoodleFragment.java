package com.example.canvasocr.helper;


import androidx.fragment.app.Fragment;

import com.example.canvasocr.MainActivity;
import com.example.canvasocr.adapter.ViewPagerAdapter;
import com.example.canvasocr.fragment.PaintFragment;


public class DoodleFragment {

    public static PaintFragment getDoodleFragment(){


        ViewPagerAdapter adapter = MainActivity.adapter;
        Fragment page = adapter.getRegisteredFragment(0);
        return (PaintFragment) page;
    }


}
