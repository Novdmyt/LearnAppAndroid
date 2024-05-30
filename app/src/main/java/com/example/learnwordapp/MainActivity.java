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
    private static final int VIEW_WORDS_TAB_INDEX = 3;
    private static final int TAB_COUNT = 4;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Установка адаптера
        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Подключение TabLayoutMediator после установки адаптера
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
                case VIEW_WORDS_TAB_INDEX:
                    tab.setText(R.string.view_words_tab);
                    break;
            }
        }).attach();
    }

    public void refreshTableNamesInFragments() {
        for (int i = 0; i < TAB_COUNT; i++) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + i);
            if (fragment instanceof SecondFragment) {
                ((SecondFragment) fragment).refreshTableNames();
            }if (fragment instanceof ThirdFragment) {
                ((ThirdFragment) fragment).refreshTableNames();
            }if (fragment instanceof FourthFragment) {
                ((FourthFragment) fragment).refreshTableNames();
            }
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
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
                case VIEW_WORDS_TAB_INDEX:
                    return new FourthFragment();
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
