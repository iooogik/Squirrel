package iooogik.app.modelling;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    int PAGE_COUNT;
    private String tabTitles[] = new String[] { "1", "2", "3", "4", "5", "6" };

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        PAGE_COUNT = tabTitles.length;
    }

    @Override public int getCount() {
        return PAGE_COUNT;
    }

    @Override public Fragment getItem(int position) {
        return PageFragment.newInstance(position + 1);
    }

    @Override public CharSequence getPageTitle(int position) {
        // генерируем заголовок в зависимости от позиции
        return tabTitles[position];
    }
}
