package com.memoryDiary.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.memoryDiary.Fragment.CameraFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * gives the wanted fragment (by the given int position)
     * @param i a wanted position
     * @return specific fragment
     */
    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new CameraFragment();
            case 1:
                return new MemoryFragment();

        }
        return null;
    }

    /**
     * We want 2 fragments
     * @return
     */
    @Override
    public int getCount() {
        return 2;
    }
}
