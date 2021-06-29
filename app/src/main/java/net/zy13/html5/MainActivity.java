package net.zy13.html5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import net.zy13.html5.utils.StatusBarUtils;
import net.zy13.library.OmgPermission;
import net.zy13.library.PermissionFail;
import net.zy13.library.PermissionSuccess;

public class MainActivity extends AppCompatActivity {
    private WebView webview;
    //存储请求码
    private final int REQUEST_STORAGE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这是为了应用程序安装完后直接打开，按home键退出后，再次打开程序出现的BUG
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            //结束你的activity
            return;
        }
        // 隐藏标题栏，在加载布局之前设置(兼容Android2.3.3版本)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        /**
         * 沉浸式(透明)状态栏
         * 说明：需要在setContentView之后才可以调用
         */
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtils.setRootViewFitsSystemWindows(this, false);
        //设置状态栏透明
        StatusBarUtils.setTranslucentStatus(this);
        //设置状态使用深色文字图标风格
        if (!StatusBarUtils.setStatusBarDarkTheme(this, true)) {
            //设置一个半透明（半透明+白=灰）颜色的状态栏
            StatusBarUtils.setStatusBarColor(this, 0x55000000);
        }
        //隐藏导航栏（虚拟按键）
        hideBottomUIMenu();
        //请求权限
        requestPermission();
        
        //加载webview控件
        loadWebview();

        //隐藏活动窗口
        //setContentView(R.layout.activity_main);
    }

    /**
     * 隐藏虚拟按键，并且全屏（滑动屏幕可重新显示出来）
     */
    protected void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void requestPermission() {
        /**
         * 请求权限
         * request()方法的参数可以有也可以没有，有且不为空，就会回调PermissionCallback的响应的回调方法，没有或为空，则回调响应的注解方法。
         */
        OmgPermission.with(MainActivity.this)
                ////添加请求码
                .addRequestCode(REQUEST_STORAGE)
                //单独申请一个权限
                .permissions(Manifest.permission.INTERNET)
                .request();
    }
    /**
     * 回调注解方法
     * 当request()没有参数的时候，就会在当前类里面寻找相应的注解方法
     */
    @PermissionSuccess(requestCode = REQUEST_STORAGE)
    public void permissionSuccess() {
        //Toast.makeText(MainActivity.this, "成功获取网络访问权限" , Toast.LENGTH_SHORT).show();
    }
    @PermissionFail(requestCode = REQUEST_STORAGE)
    public void permissionFail() {
        Toast.makeText(MainActivity.this, "获取网络访问权限失败" , Toast.LENGTH_SHORT).show();
    }
    /**
     * 申请权限的系统回调方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        OmgPermission.onRequestPermissionsResult(MainActivity.this, requestCode, permissions, grantResults);
    }

    private void loadWebview() {
        //实例化WebView对象
        webview = new WebView(this);
        //设置WebView属性
        WebSettings ws = webview.getSettings();
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);//设置布局，会引起WebView的重新布局（relayout）,默认值NARROW_COLUMNS
        ws.setLoadsImagesAutomatically(true);//自动加载图片资源
        ws.setUseWideViewPort(true);//支持HTML的“viewport”标签或者使用wide viewport
        ws.setLoadWithOverviewMode(true);//缩小内容以适应屏幕宽度
        ws.setJavaScriptEnabled(true);//执行javascript脚本
        ws.setGeolocationEnabled(true);//启用定位
        ws.setDomStorageEnabled(true);//启用DOM存储API
        webview.requestFocus();
        webview.canGoForward();
        webview.canGoBack();
        webview.setScrollBarStyle(0);
        //加载需要显示的网页
        webview.loadUrl("http://37.zy13.net/");
        //设置Web视图
        setContentView(webview);
    }

    /**
     * 设置回退
     * 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        return false;
    }
}
