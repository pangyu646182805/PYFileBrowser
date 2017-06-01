package com.neuroandroid.pyfilebrowser.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.PhotoGalleryAdapter;
import com.neuroandroid.pyfilebrowser.adapter.ThumbnailsAdapter;
import com.neuroandroid.pyfilebrowser.base.BaseFragment;
import com.neuroandroid.pyfilebrowser.bean.ISelect;
import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.utils.L;
import com.neuroandroid.pyfilebrowser.utils.SystemUtils;
import com.neuroandroid.pyfilebrowser.utils.UIUtils;
import com.neuroandroid.pyfilebrowser.widget.TitleBar;
import com.neuroandroid.pyfilebrowser.widget.recyclerviewpager.RecyclerViewPager;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by NeuroAndroid on 2017/5/31.
 */

public class PhotoGalleryFragment extends BaseFragment implements RecyclerViewPager.OnPageChangedListener {
    @BindView(R.id.rv_photo_gallery)
    RecyclerViewPager mRvPhotoGallery;
    @BindView(R.id.rv_thumbnails)
    RecyclerView mRvThumbnails;
    @BindView(R.id.ll_thumbnails)
    LinearLayout mLlThumbnails;
    private ArrayList<PYFileBean> mPhotoGallery;
    private ThumbnailsAdapter mThumbnailsAdapter;
    private int mOffset;
    private PhotoGalleryAdapter mPhotoGalleryAdapter;
    private boolean show;
    private int mCurrentPosition;
    private int mCount;

    public static PhotoGalleryFragment newInstance(Bundle bundle) {
        PhotoGalleryFragment photoGalleryFragment = new PhotoGalleryFragment();
        photoGalleryFragment.setArguments(bundle);
        return photoGalleryFragment;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_photo_gallery;
    }

    @Override
    protected void initView() {
        initTitleBar("");
        initLeftAction(new TitleBar.ImageAction(R.drawable.ic_arrow_back) {
            @Override
            public void performAction(View view) {
                MyFileFragment myFileFragment = (MyFileFragment) getParentFragment();
                myFileFragment.handleBackPress();
            }
        });

        RecyclerView.ItemAnimator animator = mRvThumbnails.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mRvThumbnails.getItemAnimator().setChangeDuration(333);
        mRvThumbnails.getItemAnimator().setMoveDuration(333);
        mRvPhotoGallery.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRvThumbnails.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        SnapHelper snapHelper = new GravitySnapHelper(Gravity.END, true, position -> {
            L.e("snap position : " + position);
        });
        snapHelper.attachToRecyclerView(mRvThumbnails);
    }

    @Override
    protected void initData() {
        mOffset = (int) ((SystemUtils.getScreenWidth(mActivity) - UIUtils.getDimen(R.dimen.x180)) / 2);
        Bundle bundle = getArguments();
        mPhotoGallery = (ArrayList<PYFileBean>) bundle.getSerializable("photo_gallery");
        mCurrentPosition = bundle.getInt("current_position", 0);
        mCount = mPhotoGallery.size();

        mPhotoGalleryAdapter = new PhotoGalleryAdapter(mContext, mPhotoGallery);
        mRvPhotoGallery.setAdapter(mPhotoGalleryAdapter);
        mThumbnailsAdapter = new ThumbnailsAdapter(mContext, mPhotoGallery);
        mRvThumbnails.setAdapter(mThumbnailsAdapter);

        getTitleBar().setTitle((mCurrentPosition + 1) + "/" + mCount);
        mRvPhotoGallery.scrollToPosition(mCurrentPosition);
    }

    @Override
    protected void initListener() {
        mRvPhotoGallery.addOnPageChangedListener(this);
        mThumbnailsAdapter.setItemSelectedListener(new ISelect.OnItemSelectedListener<PYFileBean>() {
            @Override
            public void onItemSelected(View view, int position, boolean isSelected, PYFileBean pyFileBean) {
                if (isSelected) {
                    mRvPhotoGallery.scrollToPosition(position);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) mRvThumbnails.getLayoutManager();
                    layoutManager.scrollToPositionWithOffset(position, mOffset);
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        mPhotoGalleryAdapter.setItemClickListener((view, position, pyFileBean) -> {
            if (show) {
                fullScreen(false);
                ViewCompat.animate(getTitleBar()).translationY(0).setDuration(250).start();
                ViewCompat.animate(mLlThumbnails).translationY(0).setDuration(250).start();
            } else {
                fullScreen(true);
                ViewCompat.animate(getTitleBar()).translationY(-getTitleBar().getHeight()).setDuration(250).start();
                ViewCompat.animate(mLlThumbnails).translationY(mLlThumbnails.getHeight()).setDuration(250).start();
            }
            show = !show;
        });
    }

    @Override
    public void onPageChanged(int oldPosition, int newPosition) {
        L.e(oldPosition + " : " + newPosition);
        mThumbnailsAdapter.update(oldPosition, false);
        mThumbnailsAdapter.update(newPosition, true);
        mThumbnailsAdapter.notifyDataSetChanged();
        mThumbnailsAdapter.setCheckedPos(newPosition);
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRvThumbnails.getLayoutManager();
        layoutManager.scrollToPositionWithOffset(newPosition, mOffset);
        mCurrentPosition = newPosition + 1;
        getTitleBar().setTitle(mCurrentPosition + "/" + mCount);
    }

    @Override
    public void onPause() {
        // 恢复状态
        fullScreen(false);
        for (PYFileBean pyFileBean : mPhotoGallery) {
            pyFileBean.setSelected(false);
        }
        super.onPause();
    }

    /**
     * @param enable true : 全屏
     */
    private void fullScreen(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            mActivity.getWindow().setAttributes(lp);
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = mActivity.getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mActivity.getWindow().setAttributes(attr);
        }
    }
}
