package com.merchant.explorer.marcopolo;

import androidx.fragment.app.Fragment;

public class CarouselActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return CarouselFragment.newInstance();
    }
}
