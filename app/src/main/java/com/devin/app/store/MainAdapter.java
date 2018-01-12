package com.devin.app.store;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.devin.app.store.classify.ClassifyFragment;
import com.devin.app.store.index.IndexFragment;
import com.devin.app.store.mine.MineFragment;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();

    public MainAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new IndexFragment());
        fragments.add(new ClassifyFragment());
        fragments.add(new MineFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
