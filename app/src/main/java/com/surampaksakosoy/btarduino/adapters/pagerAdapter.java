package com.surampaksakosoy.btarduino.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.surampaksakosoy.btarduino.fragments.BaseRPMFragment;
import com.surampaksakosoy.btarduino.fragments.LogFragment;
import com.surampaksakosoy.btarduino.fragments.MainFragment;

public class pagerAdapter extends FragmentStatePagerAdapter {

    private int nomortab;

    public pagerAdapter(FragmentManager fm, int nomortab){
        super(fm);
        this.nomortab = nomortab;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MainFragment();
            case 1:
                return new BaseRPMFragment();
            case 2:
                return new LogFragment();
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return nomortab;
    }
}
