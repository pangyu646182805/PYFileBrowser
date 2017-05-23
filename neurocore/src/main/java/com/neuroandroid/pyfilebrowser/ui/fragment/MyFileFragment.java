package com.neuroandroid.pyfilebrowser.ui.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.MyFilePagerAdapter;
import com.neuroandroid.pyfilebrowser.base.BaseFragment;
import com.neuroandroid.pyfilebrowser.utils.UIUtils;
import com.neuroandroid.pyfilebrowser.widget.TitleBar;

import butterknife.BindView;

/**
 * Created by NeuroAndroid on 2017/5/23.
 */

public class MyFileFragment extends BaseFragment {
    @BindView(R.id.status_bar)
    View mStatusBar;
    @BindView(R.id.vp_content)
    ViewPager mVpContent;
    @BindView(R.id.tabs)
    TabLayout mTabs;

    private TitleBar.ImageAction mMenuAction;
    private TitleBar.ImageAction mSpinnerAction;
    private MyFilePagerAdapter mFilePagerAdapter;

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
        initRightAction(mSpinnerAction);
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
}
