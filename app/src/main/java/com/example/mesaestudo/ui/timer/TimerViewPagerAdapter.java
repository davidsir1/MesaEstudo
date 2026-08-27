package com.example.mesaestudo.ui.timer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TimerViewPagerAdapter extends FragmentStateAdapter {

    public TimerViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new LogStudyFragment();
            case 2:
                return new RecordFragment();
            case 0:
            default:
                return new TimerClockFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
