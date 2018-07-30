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
                return new ProjectionFragment();
            case 1:
                return new SpendingsFragment();
            case 2:
                return new CategoriesFragment();
            case 3:
                return new BudgetOverviewFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return getString(R.string.projections_title);
            case 1:
                return getString(R.string.spendings_title);
            case 2:
                return getString(R.string.categories_title);
            case 3:
                return getString(R.string.overview_title);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
