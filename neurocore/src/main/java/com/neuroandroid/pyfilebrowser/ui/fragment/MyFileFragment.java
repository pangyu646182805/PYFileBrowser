package com.neuroandroid.pyfilebrowser.ui.fragment;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.MyFilePagerAdapter;
import com.neuroandroid.pyfilebrowser.base.BaseFragment;
import com.neuroandroid.pyfilebrowser.base.BaseRecyclerViewFragment;
import com.neuroandroid.pyfilebrowser.base.BaseRecyclerViewGridSizeFragment;
import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.event.BaseEvent;
import com.neuroandroid.pyfilebrowser.event.SelectedEvent;
import com.neuroandroid.pyfilebrowser.event.StorageEvent;
import com.neuroandroid.pyfilebrowser.ui.activity.MainActivity;
import com.neuroandroid.pyfilebrowser.utils.FileUtils;
import com.neuroandroid.pyfilebrowser.utils.FragmentUtils;
import com.neuroandroid.pyfilebrowser.utils.L;
import com.neuroandroid.pyfilebrowser.utils.ShowUtils;
import com.neuroandroid.pyfilebrowser.utils.UIUtils;
import com.neuroandroid.pyfilebrowser.widget.TitleBar;
import com.neuroandroid.pyfilebrowser.widget.dialog.FileListDialog;
import com.neuroandroid.pyfilebrowser.widget.dialog.TitleDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by NeuroAndroid on 2017/5/23.
 */

public class MyFileFragment extends BaseFragment implements MainActivity.MainActivityFragmentCallbacks, ViewPager.OnPageChangeListener {
    @BindView(R.id.status_bar)
    View mStatusBar;
    @BindView(R.id.vp_content)
    ViewPager mVpContent;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.btn_add)
    FloatingActionButton mBtnAdd;
    @BindView(R.id.fl_container)
    FrameLayout mFlContainer;

    private TitleBar.ImageAction mMenuAction;
    private TitleBar.ImageAction mSpinnerAction;
    private MyFilePagerAdapter mFilePagerAdapter;
    private boolean classifyRoot = true;  // 判断返回事件(是否在根目录)
    private boolean storageRoot = true;  // 判断返回事件(是否在根目录)
    private boolean selectedMenuOpen;  // 判断返回事件(是否是选择模式)
    private TitleBar.ImageAction mUpAction;
    private TitleBar.ImageAction mSortAction;
    private TitleBar.ImageAction mCloseAction;
    private TitleBar.ImageAction mDelAction;
    private TitleBar.ImageAction mCopyAction;

    private BaseFragment mContainerFragment;

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
        mBtnAdd.setVisibility(View.GONE);
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
                PopupMenu popupMenu = new PopupMenu(mContext, mStatusBar, GravityCompat.END);
                popupMenu.inflate(selectedMenuOpen ? R.menu.menu_classify_selected : R.menu.menu_classify);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    BaseRecyclerViewGridSizeFragment recyclerViewGridSizeFragment = (BaseRecyclerViewGridSizeFragment) getFragment(0);
                    switch (menuItem.getItemId()) {
                        case R.id.action_switch_display:
                            handleGridSizeMenuItem(recyclerViewGridSizeFragment);
                            break;
                        case R.id.action_search:

                            break;
                        case R.id.action_select_all:
                            recyclerViewGridSizeFragment.selectAll();
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            }
        };
        mUpAction = new TitleBar.ImageAction(R.drawable.ic_action_up) {
            @Override
            public void performAction(View view) {
                switch (mVpContent.getCurrentItem()) {
                    case 0:
                        classifyRoot = true;
                        ClassifyFragment classifyFragment = (ClassifyFragment) getFragment(0);
                        classifyFragment.handleBackPress();
                        closeMenu();
                        break;
                    case 1:
                        StorageFragment storageFragment = (StorageFragment) getFragment(1);
                        int currentLevel = storageFragment.getCurrentLevel();
                        if (currentLevel < 0) {
                        } else {
                            if (currentLevel == 1) {
                                storageRoot = true;
                                closeMenu();
                                mBtnAdd.setVisibility(View.GONE);
                            }
                            EventBus.getDefault().post(new StorageEvent().setEventFlag(BaseEvent.EVENT_STORAGE_BACK));
                        }
                        break;
                }
            }
        };
        mSortAction = new TitleBar.ImageAction(R.drawable.ic_sort_white) {
            @Override
            public void performAction(View view) {

            }
        };
        mCloseAction = new TitleBar.ImageAction(R.drawable.ic_close_white) {
            @Override
            public void performAction(View view) {
                if (selectedMenuOpen) {
                    selectedMenuOpen = false;
                    closeSelectedMenu();
                    clearSelected();
                }
            }
        };
        mDelAction = new TitleBar.ImageAction(R.drawable.ic_delete) {
            @Override
            public void performAction(View view) {
                new TitleDialog(mContext).setCustomTitle("删除文件?")
                        .setConfirmClickListener((dialog, v) -> {
                            Fragment fragment = getFragment(mVpContent.getCurrentItem());
                            if (fragment instanceof ClassifyFragment) {
                                ClassifyFragment classifyFragment = (ClassifyFragment) fragment;
                                new DelAsyncTask(classifyFragment).execute(classifyFragment.getSelectedDataList());
                                dialog.dismiss();
                            }
                        }).show();
            }
        };
        mCopyAction = new TitleBar.ImageAction(R.drawable.ic_content_copy) {
            @Override
            public void performAction(View view) {
                new FileListDialog(mContext, null).setCustomTitle("复制至")
                        .setCompleteClickListener((v, fileListDialog, destFile) -> {
                            Fragment fragment = getFragment(mVpContent.getCurrentItem());
                            if (fragment instanceof ClassifyFragment) {
                                ClassifyFragment classifyFragment = (ClassifyFragment) fragment;
                                ArrayList<PYFileBean> selectedDataList = classifyFragment.getSelectedDataList();
                                for (PYFileBean pyFileBean : selectedDataList) {
                                    String path = pyFileBean.getPath();
                                    String title = path.substring(path.lastIndexOf("/") + 1, path.length());
                                    destFile = new File(destFile.getAbsolutePath() + "/" + title);
                                    boolean copyFile = FileUtils.copyFile(pyFileBean.getFile(), destFile);
                                    if (copyFile) {
                                        // 复制成功
                                        L.e("复制成功");
                                        MediaScannerConnection.scanFile(mContext, new String[]{destFile.getAbsolutePath()}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                                            @Override
                                            public void onMediaScannerConnected() {
                                                L.e("onMediaScannerConnected");
                                            }

                                            @Override
                                            public void onScanCompleted(String path, Uri uri) {
                                                L.e("path : " + path + " uri : " + uri);
                                            }
                                        });
                                    }
                                }
                            }
                        }).show();
            }
        };
    }

    private void handleGridSizeMenuItem(BaseRecyclerViewGridSizeFragment recyclerViewGridSizeFragment) {
        int gridSize = recyclerViewGridSizeFragment.getGridSize();
        recyclerViewGridSizeFragment.setGridSize(gridSize == 1 ? 2 : 1);
    }

    /**
     * ViewPager相关设置
     */
    private void setUpViewPager() {
        mFilePagerAdapter = new MyFilePagerAdapter(mContext, getChildFragmentManager());
        mVpContent.setAdapter(mFilePagerAdapter);
        mVpContent.setOffscreenPageLimit(mFilePagerAdapter.getCount() - 1);
        mTabs.setupWithViewPager(mVpContent);
        mVpContent.addOnPageChangeListener(this);
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
                    classifyRoot = false;
                    openMenu();
                    break;
                case BaseEvent.EVENT_STORAGE:
                    if (storageRoot) {
                        mBtnAdd.setVisibility(View.VISIBLE);
                        openMenu();
                    }
                    storageRoot = false;
                    break;
                case BaseEvent.EVENT_SELECTED_MODE:
                    SelectedEvent selectedEvent = (SelectedEvent) baseEvent;
                    List<PYFileBean> pyFileBeanList = selectedEvent.getSelectedBeans();
                    int size = pyFileBeanList.size();
                    handleSelectedEvent(size, size <= 0 ? null : pyFileBeanList.get(0).getTitle());
                    break;
            }
        }
    }

    /**
     * 处理多选事件
     */
    private void handleSelectedEvent(int selectedSize, String selectedSizeIsOneTitle) {
        if (selectedSize <= 0) {
            if (selectedMenuOpen) {
                selectedMenuOpen = false;
                closeSelectedMenu();
            }
            getTitleBar().setTitle(UIUtils.getString(R.string.app_name));
        } else if (selectedSize == 1) {
            if (!selectedMenuOpen) {
                selectedMenuOpen = true;
                openSelectedMenu();
            }
            getTitleBar().setTitle(selectedSizeIsOneTitle);
        } else {
            getTitleBar().setTitle(UIUtils.getString(R.string.x_selected, selectedSize));
        }
    }

    private void openMenu() {
        initRightAction(mUpAction);
        initRightAction(mSortAction);
        initRightAction(mSpinnerAction);
    }

    private void openSelectedMenu() {
        getTitleBar().removeLeftAction(mMenuAction);
        initLeftAction(mCloseAction);

        closeMenu();
        initRightAction(mDelAction);
        initRightAction(mCopyAction);
        initRightAction(mSpinnerAction);
    }

    private void closeSelectedMenu() {
        getTitleBar().removeLeftAction(mCloseAction);
        initLeftAction(mMenuAction);

        getTitleBar().removeAllRightActions();
        openMenu();
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
     * 清除指定Fragment的选择项
     */
    private void clearSelected() {
        getTitleBar().setTitle(UIUtils.getString(R.string.app_name));
        BaseRecyclerViewFragment recyclerViewFragment = (BaseRecyclerViewFragment) getFragment(mVpContent.getCurrentItem());
        recyclerViewFragment.clearSelected();
    }

    public void replaceFragment(Bundle bundle) {
        mContainerFragment = PhotoGalleryFragment.newInstance(bundle);
        FragmentUtils.replaceFragment(getChildFragmentManager(), mContainerFragment, R.id.fl_container, true);
    }

    /**
     * 处理返回事件
     */
    @Override
    public boolean handleBackPress() {
        if (mContainerFragment != null) {
            // FragmentUtils.removeFragment(mContainerFragment);
            FragmentUtils.popFragment(getChildFragmentManager());
            mContainerFragment = null;
            return true;
        }
        switch (mVpContent.getCurrentItem()) {
            case 0:
                if (selectedMenuOpen) {
                    selectedMenuOpen = false;
                    closeSelectedMenu();
                    clearSelected();
                    return true;
                }
                if (!classifyRoot) {
                    classifyRoot = true;
                    ClassifyFragment classifyFragment = (ClassifyFragment) getFragment(0);
                    classifyFragment.handleBackPress();
                    closeMenu();
                    return true;
                }
            case 1:
                if (!storageRoot) {
                    StorageFragment storageFragment = (StorageFragment) getFragment(1);
                    int currentLevel = storageFragment.getCurrentLevel();
                    if (currentLevel < 0) {
                        return false;
                    } else {
                        if (currentLevel == 1) {
                            storageRoot = true;
                            closeMenu();
                            mBtnAdd.setVisibility(View.GONE);
                        }
                        EventBus.getDefault().post(new StorageEvent().setEventFlag(BaseEvent.EVENT_STORAGE_BACK));
                        return true;
                    }
                }
        }
        // 返回false能够退出应用
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mBtnAdd.setVisibility(position == 1 ? storageRoot ? View.GONE : View.VISIBLE : View.GONE);
        switch (position) {
            case 0:
                if (classifyRoot) {
                    closeMenu();
                } else {
                    if (storageRoot)
                        openMenu();
                }
                break;
            case 1:
                if (storageRoot) {
                    closeMenu();
                } else {
                    if (classifyRoot)
                        openMenu();
                }
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class DelAsyncTask extends AsyncTask<ArrayList<PYFileBean>, Void, Void> {
        private BaseRecyclerViewFragment mRecyclerViewFragment;

        public DelAsyncTask(BaseRecyclerViewFragment recyclerViewFragment) {
            mRecyclerViewFragment = recyclerViewFragment;
        }

        @Override
        protected Void doInBackground(ArrayList<PYFileBean>... arrayLists) {
            ArrayList<PYFileBean> delList = arrayLists[0];
            for (PYFileBean pyFileBean : delList) {
                FileUtils.deleteFile(pyFileBean.getFile());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ShowUtils.showToast("删除成功");
            if (selectedMenuOpen) {
                selectedMenuOpen = false;
                closeSelectedMenu();
                clearSelected();
            }
            if (mRecyclerViewFragment instanceof ClassifyFragment) {
                ClassifyFragment classifyFragment = (ClassifyFragment) mRecyclerViewFragment;
                classifyFragment.restartLoader();
            }
        }
    }
}
