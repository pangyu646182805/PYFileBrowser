package com.neuroandroid.pyfilebrowser.ui.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.MyFilePagerAdapter;
import com.neuroandroid.pyfilebrowser.base.BaseFragment;
import com.neuroandroid.pyfilebrowser.event.BaseEvent;
import com.neuroandroid.pyfilebrowser.ui.activity.MainActivity;
import com.neuroandroid.pyfilebrowser.utils.UIUtils;
import com.neuroandroid.pyfilebrowser.widget.TitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * Created by NeuroAndroid on 2017/5/23.
 */

public class MyFileFragment extends BaseFragment implements MainActivity.MainActivityFragmentCallbacks {
    @BindView(R.id.status_bar)
    View mStatusBar;
    @BindView(R.id.vp_content)
    ViewPager mVpContent;
    @BindView(R.id.tabs)
    TabLayout mTabs;

    private TitleBar.ImageAction mMenuAction;
    private TitleBar.ImageAction mSpinnerAction;
    private MyFilePagerAdapter mFilePagerAdapter;
    private boolean root = true;  // 判断返回事件(是否在根目录)
    private TitleBar.ImageAction mUpAction;
    private TitleBar.ImageAction mSortAction;

    public static MyFileFragment newInstance() {
        return new MyFileFragment();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_my_file;
    }

    @Override
    protected void initView() {
        initTitleBar(UIUtils.getString(R.string.app_name), false);
        initImageAction();
        initLeftAction(mMenuAction);
        setStatusBar(mStatusBar);
        setUpViewPager();
    }

    private void initImageAction() {
        mMenuAction = new TitleBar.ImageAction(R.drawable.ic_menu_white) {
            @Override
            public void performAction(View view) {
                getMainActivity().openDrawer();
            }
        };
        mSpinnerAction = new TitleBar.ImageAction(R.drawable.ic_more_vert_white) {
            @Override
            public void performAction(View view) {

            }
        };
        mUpAction = new TitleBar.ImageAction(R.drawable.ic_action_up) {
            @Override
            public void performAction(View view) {
                root = true;
                ClassifyFragment classifyFragment = (ClassifyFragment) getCurrentFragment();
                classifyFragment.handleBackPress();
                closeMenu();
            }
        };
        mSortAction = new TitleBar.ImageAction(R.drawable.ic_sort_white) {
            @Override
            public void performAction(View view) {

            }
        };
    }

    /**
     * ViewPager相关设置
     */
    private void setUpViewPager() {
        mFilePagerAdapter = new MyFilePagerAdapter(mContext, getChildFragmentManager());
        mVpContent.setAdapter(mFilePagerAdapter);
        mVpContent.setOffscreenPageLimit(mFilePagerAdapter.getCount() - 1);
        mTabs.setupWithViewPager(mVpContent);
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BaseEvent baseEvent) {
        if (baseEvent != null) {
            switch (baseEvent.getEventFlag()) {
                case BaseEvent.EVENT_CLASSIFY:
                    root = false;
                    openMenu();
                    break;
            }
        }
    }

    private void openMenu() {
        initRightAction(mUpAction);
        initRightAction(mSortAction);
        initRightAction(mSpinnerAction);
    }

    private void closeMenu() {
        getTitleBar().removeRightAction(mUpAction);
        getTitleBar().removeRightAction(mSortAction);
        getTitleBar().removeRightAction(mSpinnerAction);
    }

    /**
     * 返回当前Fragment
     */
    public Fragment getCurrentFragment() {
        return getFragment(mVpContent.getCurrentItem());
    }

    /**
     * 根据position返回Fragment
     */
    public Fragment getFragment(int position) {
        return mFilePagerAdapter.getFragment(position);
    }

    /**
     * 处理返回事件
     */
    @Override
    public boolean handleBackPress() {
        if (!root) {
            root = true;
            ClassifyFragment classifyFragment = (ClassifyFragment) getCurrentFragment();
            classifyFragment.handleBackPress();
            closeMenu();
            return true;
        }
        return false;
    }
}
