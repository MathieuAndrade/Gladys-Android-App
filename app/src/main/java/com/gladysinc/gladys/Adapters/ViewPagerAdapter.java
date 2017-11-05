package com.gladysinc.gladys.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gladysinc.gladys.BrainTabFragment.AllSentencesFragment;
import com.gladysinc.gladys.BrainTabFragment.ApprovedSentencesFragment;
import com.gladysinc.gladys.BrainTabFragment.RejectedSentencesFragment;
import com.gladysinc.gladys.BrainTabFragment.PendingSentencesFragment;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return AllSentencesFragment.newInstance();
            case 1:
                return PendingSentencesFragment.newInstance();
            case 2:
                return ApprovedSentencesFragment.newInstance();
            case 3:
                return RejectedSentencesFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return AllSentencesFragment.Title;

            case 1:
                return PendingSentencesFragment.Title;

            case 2:
                return ApprovedSentencesFragment.Title;

            case 3:
                return RejectedSentencesFragment.Title;
        }
        return super.getPageTitle(position);
    }
}
