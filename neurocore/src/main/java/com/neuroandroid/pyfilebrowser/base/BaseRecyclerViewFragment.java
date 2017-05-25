package com.neuroandroid.pyfilebrowser.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.ClassifyAdapter;
import com.neuroandroid.pyfilebrowser.adapter.StorageAdapter;
import com.neuroandroid.pyfilebrowser.adapter.base.SelectAdapter;
import com.neuroandroid.pyfilebrowser.bean.ClassifyBean;
import com.neuroandroid.pyfilebrowser.bean.ClassifyFileBean;
import com.neuroandroid.pyfilebrowser.bean.ISelect;
import com.neuroandroid.pyfilebrowser.listener.CategoryCallBack;
import com.neuroandroid.pyfilebrowser.utils.UIUtils;
import com.neuroandroid.pyfilebrowser.widget.LoadingLayout;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by NeuroAndroid on 2017/5/24.
 */

public abstract class BaseRecyclerViewFragment<ADAPTER extends SelectAdapter, LM extends RecyclerView.LayoutManager>
        extends BaseFragment implements CategoryCallBack {
    protected static final int CLASSIFY_FRAGMENT = 99;
    protected static final int STORAGE_FRAGMENT = 100;
    private static final int[] CLASSIFY_IMG = {R.mipmap.ic_category_audio, R.mipmap.ic_category_video, R.mipmap.ic_category_photo,
            R.mipmap.ic_category_doc, R.mipmap.ic_category_apk, R.mipmap.ic_category_zip,
            R.mipmap.ic_category_download, R.mipmap.ic_category_favorite};
    private static final String[] CLASSIFY_TITLE = {"音频", "视频", "图像", "文档", "安装包", "压缩", "下载", "收藏"};

    @Nullable
    @BindView(R.id.loading_layout)
    LoadingLayout mLoadingLayout;

    @Nullable
    @BindView(R.id.rv_file)
    FastScrollRecyclerView mRvFile;

    @Nullable
    @BindView(R.id.rv_root)
    RecyclerView mRvRoot;

    private ADAPTER mAdapter;
    private LM mLayoutManager;
    private ClassifyAdapter mClassifyAdapter;

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_base_recyclerview;
    }

    @Override
    protected void initView() {
        if (getCurrentFragment() == CLASSIFY_FRAGMENT) {
            initClassifyRecyclerView();
            initLayoutManager();
            initAdapter(new ArrayList<>());
            setUpRecyclerView();
        } else {
            initStorageRecyclerView();
        }
    }

    /**
     * 初始化分类Fragment的RecyclerView
     */
    private void initClassifyRecyclerView() {
        mRvRoot.setLayoutManager(new GridLayoutManager(mContext, 3));
        List<ClassifyBean> dataList = new ArrayList<>();
        ClassifyBean classifyBean;
        for (int i = 0; i < CLASSIFY_TITLE.length; i++) {
            classifyBean = new ClassifyBean(CLASSIFY_TITLE[i], CLASSIFY_IMG[i]);
            dataList.add(classifyBean);
        }
        mClassifyAdapter = new ClassifyAdapter(mContext, dataList);
        mRvRoot.setAdapter(mClassifyAdapter);
        mClassifyAdapter.setItemClickListener((view, position, bean) -> toDest(position, bean));
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mClassifyAdapter != null)
                    mClassifyAdapter.setTotalHeight(mRootView.getHeight());
            }
        });
    }

    /**
     * 初始化存储Fragment的RecyclerView
     */
    private void initStorageRecyclerView() {
        mRvRoot.setLayoutManager(new LinearLayoutManager(mContext));
        List<String> dataList = new ArrayList<>();
        dataList.add("");
        StorageAdapter storageAdapter = new StorageAdapter(mContext, dataList);
        mRvRoot.setAdapter(storageAdapter);
        storageAdapter.setItemClickListener((view, position, s) -> toDest(position, null));
    }

    private void initLayoutManager() {
        mLayoutManager = createLayoutManager();
    }

    private void initAdapter(ArrayList<ClassifyFileBean> dataList) {
        mAdapter = createAdapter(dataList);
        mAdapter.setSelectedMode(ISelect.MULTIPLE_MODE);
        mAdapter.updateSelectMode(false);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkIsEmpty();
            }
        });
    }

    /**
     * 检查RecyclerView是否为空
     */
    private void checkIsEmpty() {
        if (mLoadingLayout != null) {
            if (mAdapter == null || mAdapter.getItemCount() == 0) {
                showError(() -> {
                }, getEmptyMessage());
            }
        }
    }

    private void setUpRecyclerView() {
        mRvFile.setLayoutManager(new LinearLayoutManager(mContext));
        mRvFile.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .sizeResId(R.dimen.y2).colorResId(R.color.split).build());

        RecyclerView.ItemAnimator animator = mRvFile.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mRvFile.getItemAnimator().setChangeDuration(333);
        mRvFile.getItemAnimator().setMoveDuration(333);
        mRvFile.setLayoutManager(mLayoutManager);
        mRvFile.setAdapter(mAdapter);
    }

    protected String getEmptyMessage() {
        return UIUtils.getString(R.string.empty);
    }

    /**
     * 重新设置布局管理器
     */
    protected void invalidateLayoutManager() {
        initLayoutManager();
        mRvFile.setLayoutManager(mLayoutManager);
    }

    /**
     * 重新设置适配器
     */
    protected void invalidateAdapter(ArrayList<ClassifyFileBean> dataList) {
        initAdapter(dataList);
        checkIsEmpty();
        mRvFile.setAdapter(mAdapter);
    }

    protected ADAPTER getAdapter() {
        return mAdapter;
    }

    protected LM getLayoutManager() {
        return mLayoutManager;
    }

    protected int getCurrentFragment() {
        return CLASSIFY_FRAGMENT;
    }

    protected abstract LM createLayoutManager();

    @NonNull
    protected abstract ADAPTER createAdapter(ArrayList<ClassifyFileBean> dataList);

    /**
     * 去目的地
     */
    @Override
    public void toDest(int position, ClassifyBean classifyBean) {
        // 交给子类去实现
    }

    public void showRecyclerView() {
        mRvFile.setVisibility(View.VISIBLE);
    }

    public void hideRecyclerView() {
        mRvFile.setVisibility(View.GONE);
    }
}
