package com.devin.app.store;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.devin.app.store.base.BaseActivity;
import com.devin.app.store.base.widget.BottomTabsLayout;

/**
 * @author Devin
 */
public class MainActivity extends BaseActivity {

    private BottomTabsLayout btl;
    private ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btl = findViewById(R.id.btl);
        btl.add("推荐", R.mipmap.ic_community, R.mipmap.ic_community_clicked)
                .add("分类", R.mipmap.ic_space, R.mipmap.ic_space_clicked)
                .add("更新", R.mipmap.ic_my, R.mipmap.ic_my_clicked)
                .build();
        btl.setChecked(0, true);
        btl.show(2, "10");
        vp = findViewById(R.id.vp);
        vp.setAdapter(new MainAdapter(getSupportFragmentManager()));
        vp.setOffscreenPageLimit(3);
        btl.setOnBottomTabClickListener(position -> vp.setCurrentItem(position));
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        btl.setChecked(0, true);
                        break;
                    case 1:
                        btl.setChecked(1, true);
                        break;
                    case 2:
                        btl.setChecked(2, true);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
