package com.neuroandroid.pyfilebrowser.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.base.BaseActivity;
import com.neuroandroid.pyfilebrowser.base.BaseFragment;
import com.neuroandroid.pyfilebrowser.ui.fragment.MyAppFragment;
import com.neuroandroid.pyfilebrowser.ui.fragment.MyFileFragment;
import com.neuroandroid.pyfilebrowser.utils.ShowUtils;
import com.neuroandroid.pyfilebrowser.utils.UIUtils;

import butterknife.BindView;
import didikee.com.permissionshelper.PermissionsHelper;
import didikee.com.permissionshelper.permission.DangerousPermissions;

public class MainActivity extends BaseActivity {
    private static final int FRAGMENT_MY_FILE = 0;
    private static final int FRAGMENT_MY_APP = 1;

    /**
     * 需要动态申请的权限
     */
    private static final String[] PERMISSIONS = new String[]{
            DangerousPermissions.STORAGE,
    };

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_layout)
    NavigationView mNavLayout;

    private BaseFragment mCurrentFragment;
    private PermissionsHelper mPermissionsHelper;

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mPermissionsHelper = new PermissionsHelper(this, PERMISSIONS);
        checkPermission();
    }

    @Override
    protected void initListener() {
        mNavLayout.setNavigationItemSelectedListener(item -> {
            mDrawerLayout.closeDrawers();
            switch (item.getItemId()) {
                case R.id.nav_my_file:
                    UIUtils.getHandler().postDelayed(() -> setChooser(FRAGMENT_MY_FILE), 150);
                    break;
                case R.id.nav_my_app:
                    UIUtils.getHandler().postDelayed(() -> setChooser(FRAGMENT_MY_APP), 150);
                    break;
            }
            return true;
        });
    }

    /**
     * 跳转到Fragment
     * {@link MainActivity#FRAGMENT_MY_FILE}
     * {@link MainActivity#FRAGMENT_MY_APP}
     */
    private void setChooser(int key) {
        switch (key) {
            case FRAGMENT_MY_FILE:
                mNavLayout.setCheckedItem(R.id.nav_my_file);
                setCurrentFragment(MyFileFragment.newInstance());
                break;
            case FRAGMENT_MY_APP:
                mNavLayout.setCheckedItem(R.id.nav_my_app);
                setCurrentFragment(MyAppFragment.newInstance());
                break;
        }
    }

    /**
     * 设置当前Fragment
     */
    private void setCurrentFragment(@SuppressWarnings("NullableProblems") Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_drawer_container, fragment, null).commit();
        mCurrentFragment = (BaseFragment) fragment;
    }

    /**
     * 打开侧滑菜单
     */
    public void openDrawer() {
        if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * 检查有没有权限
     * 没有则去动态申请权限
     */
    private void checkPermission() {
        if (mPermissionsHelper.checkAllPermissions(PERMISSIONS)) {
            setChooser(FRAGMENT_MY_FILE);
            mPermissionsHelper.onDestroy();
        } else {
            // 申请权限
            mPermissionsHelper.startRequestNeedPermissions();
        }
        mPermissionsHelper.setonAllNeedPermissionsGrantedListener(new PermissionsHelper.onAllNeedPermissionsGrantedListener() {
            @Override
            public void onAllNeedPermissionsGranted() {
                ShowUtils.showToast("权限申请成功");
                setChooser(FRAGMENT_MY_FILE);
            }

            @Override
            public void onPermissionsDenied() {
                ShowUtils.showToast("权限申请失败");
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionsHelper.onActivityResult(requestCode, resultCode, data);
    }
}
