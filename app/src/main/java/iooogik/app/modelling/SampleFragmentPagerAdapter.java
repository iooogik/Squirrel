package iooogik.app.modelling;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    private int PAGE_COUNT;
    private String[] tabTitles = new String[]{"1", "2", "3", "4", "5", "6"};

    SampleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        PAGE_COUNT = tabTitles.length;
    }

    @Override public int getCount() {
        return PAGE_COUNT;
    }

    @NonNull
    @Override public Fragment getItem(int position) {
        return PageFragment.newInstance(position + 1);
    }

    @Override public CharSequence getPageTitle(int position) {
        // генерируем заголовок в зависимости от позиции
        return tabTitles[position];
    }
}
