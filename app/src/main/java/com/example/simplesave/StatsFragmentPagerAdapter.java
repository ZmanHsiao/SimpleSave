package com.example.simplesave;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class StatsFragmentPagerAdapter extends FragmentPagerAdapter{

    public StatsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new StatsProjectionFragment();
            case 1:
                return new StatsSpendingsFragment();
            case 2:
                return new StatsCategoriesFragment();
            case 3:
                return new StatsOverviewFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "Projections";
            case 1:
                return "Spendings";
            case 2:
                return "Categories";
            case 3:
                return "Overview";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
