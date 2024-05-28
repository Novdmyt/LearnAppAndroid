package com.example.learnwordapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private static final int CREATE_CARDS_TAB_INDEX = 0;
    private static final int ADD_WORDS_TAB_INDEX = 1;
    private static final int TEST_WORDS_TAB_INDEX = 2;
    private static final int TAB_COUNT = 3;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        viewPager.setAdapter(new ScreenSlidePagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case CREATE_CARDS_TAB_INDEX:
                    tab.setText(R.string.create_cards_tab);
                    break;
                case ADD_WORDS_TAB_INDEX:
                    tab.setText(R.string.add_words_tab);
                    break;
                case TEST_WORDS_TAB_INDEX:
                    tab.setText(R.string.test_words_tab);
                    break;
            }
        }).attach();
    }

    private static class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(@NonNull AppCompatActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case CREATE_CARDS_TAB_INDEX:
                    return new FirstFragment();
                case ADD_WORDS_TAB_INDEX:
                    return new SecondFragment();
                case TEST_WORDS_TAB_INDEX:
                    return new ThirdFragment();
                default:
                    throw new IllegalArgumentException("Invalid position: " + position);
            }
        }

        @Override
        public int getItemCount() {
            return TAB_COUNT;
        }
    }
}
