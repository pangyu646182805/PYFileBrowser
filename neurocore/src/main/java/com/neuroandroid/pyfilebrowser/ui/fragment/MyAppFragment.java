package com.neuroandroid.pyfilebrowser.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.MyAppAdapter;
import com.neuroandroid.pyfilebrowser.base.BaseFragment;
import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.loader.AppLoader;
import com.neuroandroid.pyfilebrowser.loader.WrappedAsyncTaskLoader;
import com.neuroandroid.pyfilebrowser.ui.activity.MainActivity;
import com.neuroandroid.pyfilebrowser.utils.UIUtils;
import com.neuroandroid.pyfilebrowser.widget.TitleBar;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by NeuroAndroid on 2017/5/23.
 */

public class MyAppFragment extends BaseFragment implements MainActivity.MainActivityFragmentCallbacks, LoaderManager.LoaderCallbacks<ArrayList<PYFileBean>> {
    @BindView(R.id.status_bar)
    View mStatusBar;
    @BindView(R.id.rv_my_app)
    FastScrollRecyclerView mRvMyApp;
    private TitleBar.ImageAction mMenuAction;
    private GridLayoutManager mLayoutManager;
    private MyAppAdapter mMyAppAdapter;
    private TitleBar.ImageAction mListAction;
    private TitleBar.ImageAction mSortAction;

    public static MyAppFragment newInstance() {
        return new MyAppFragment();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_my_app;
    }

    @Override
    protected void initView() {
        initTitleBar(UIUtils.getString(R.string.my_app), false);
        initImageAction();
        initLeftAction(mMenuAction);
        setStatusBar(mStatusBar);
        mLayoutManager = new GridLayoutManager(mContext, 1);
        mRvMyApp.setLayoutManager(mLayoutManager);
        mRvMyApp.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .sizeResId(R.dimen.y2).colorResId(R.color.split).build());
    }

    private void initImageAction() {
        mMenuAction = new TitleBar.ImageAction(R.drawable.ic_menu_white) {
            @Override
            public void performAction(View view) {
                getMainActivity().openDrawer();
            }
        };
        mListAction = new TitleBar.ImageAction(R.mipmap.ic_action_view_as_list) {
            @Override
            public void performAction(View view) {
                if (mLayoutManager.getSpanCount() == 1) {
                    mLayoutManager = new GridLayoutManager(mContext, 2);
                    mRvMyApp.addItemDecoration(new VerticalDividerItemDecoration.Builder(mContext)
                            .sizeResId(R.dimen.y2).colorResId(R.color.split).build());
                } else {
                    mLayoutManager = new GridLayoutManager(mContext, 1);
                    mRvMyApp.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                            .sizeResId(R.dimen.y2).colorResId(R.color.split).build());
                }
                mRvMyApp.setLayoutManager(mLayoutManager);
            }
        };
        mSortAction = new TitleBar.ImageAction(R.drawable.ic_sort_white) {
            @Override
            public void performAction(View view) {

            }
        };
    }

    @Override
    protected void initData() {
        showLoading();
        getLoaderManager().initLoader(ClassifyFragment.CLASSIFY_MY_APP, null, this);
        mMyAppAdapter = new MyAppAdapter(mContext, new ArrayList<>());
        mRvMyApp.setAdapter(mMyAppAdapter);
    }

    @Override
    public boolean handleBackPress() {
        return false;
    }

    @Override
    public Loader<ArrayList<PYFileBean>> onCreateLoader(int id, Bundle args) {
        return new AsyncAppLoader(mContext);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<PYFileBean>> loader, ArrayList<PYFileBean> data) {
        getTitleBar().removeAllRightActions();
        initRightAction(mSortAction);
        initRightAction(mListAction);
        hideLoading();
        mMyAppAdapter.replaceAll(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<PYFileBean>> loader) {
        mMyAppAdapter.replaceAll(new ArrayList<>());
    }

    private static class AsyncAppLoader extends WrappedAsyncTaskLoader<ArrayList<PYFileBean>> {
        public AsyncAppLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<PYFileBean> loadInBackground() {
            return AppLoader.getAllApps(getContext());
        }
    }
}
