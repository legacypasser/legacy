package com.androider.legacy.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


import com.androider.legacy.R;
import com.androider.legacy.adapter.FragmentViewPagerAdapter;
import com.androider.legacy.common.database.DatabaseHelper;
import com.androider.legacy.controller.StateController;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Nicker;
import com.androider.legacy.data.User;
import com.androider.legacy.fragment.LoginFragment;
import com.androider.legacy.fragment.MyPostListFragment;
import com.androider.legacy.fragment.PostDetailFragment;
import com.androider.legacy.fragment.RecommendFragment;
import com.androider.legacy.fragment.SessionListFragment;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.net.Receiver;
import com.androider.legacy.net.Sender;
import com.androider.legacy.service.NetService;
import com.androider.legacy.util.DensityUtil;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.gc.materialdesign.views.ButtonFloat;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

import org.apache.http.cookie.params.CookieSpecPNames;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    public static String filePath;
    private MaterialMenuIconToolbar materialMenu;
    private Toolbar overallToolBar;
    private ButtonFloat overButton;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    public static SQLiteDatabase db;
    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        overallToolBar = (Toolbar)findViewById(R.id.overall_toolbar);
        setSupportActionBar(overallToolBar);
        overallToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backControl();
            }
        });
        materialMenu = new MaterialMenuIconToolbar(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN) {
            @Override
            public int getToolbarViewId() {
                return R.id.overall_toolbar;
            }
        };
        materialMenu.setNeverDrawTouch(true);
        db = new DatabaseHelper(this).getWritableDatabase();
        Nicker.initNick(this);
        filePath = this.getApplicationContext().getFilesDir() + "/";
        StateController.change(Constants.mainState);
        User.drag();
        autoLogin();
        initView();
    }

    private void backControl(){
        if(StateController.getCurrent() == Constants.detailState){
            getSupportFragmentManager().popBackStack();
            materialMenu.animateState(MaterialMenuDrawable.IconState.BURGER);
            StateController.goBack();
        }else{
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        backControl();
    }

    private void autoLogin(){
        Log.v("panbo", "" + "user id is:" + User.id);
            Intent intent = new Intent(this, NetService.class);
            intent.putExtra(Constants.intentType, Constants.loginAttempt);
            startService(intent);
    }
    private void initView(){
        DisplayImageOptions options  = new DisplayImageOptions.Builder().
                cacheOnDisk(true).
                displayer(new SimpleBitmapDisplayer()).
                build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config);
        overButton = (ButtonFloat)findViewById(R.id.all_over_button);
        overButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.instance, PublishActivity.class);
                MainActivity.instance.startActivity(intent);
            }
        });

        fragmentList = new ArrayList<>();
        fragmentList.add(RecommendFragment.newInstance("", ""));
        fragmentList.add(MyPostListFragment.newInstance("",""));
        fragmentList.add(SessionListFragment.newInstance("",""));
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        FragmentViewPagerAdapter pagerAdapter = new FragmentViewPagerAdapter( fragmentList,this.getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        LinePageIndicator indicator = (LinePageIndicator)findViewById(R.id.pager_indicator);
        indicator.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (overButton.getVisibility() == View.INVISIBLE)
                    overButton.setVisibility(View.VISIBLE);
                if (position == 1){
                    if(!overButton.isShow)
                        overButton.show();
                }
                else
                    overButton.hide();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        overButton.setVisibility(View.INVISIBLE);
    }

    public void switchFragment(String fragmentName){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentName);
        if(fragment == null){
            if(fragmentName.equals(LoginFragment.class.getSimpleName())){
                fragment = LoginFragment.newInstance("", "");
            }else if(fragmentName.equals(PostDetailFragment.class.getSimpleName())){
                fragment = PostDetailFragment.newInstance("", "");
            }
        }

        materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment, fragmentName);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(id == R.id.action_register){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_login){
            switchFragment(LoginFragment.class.getSimpleName());
            return true;
        }
        if(id == R.id.lets_search){
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public NetHandler netHandler = new NetHandler(instance);

    private static class NetHandler extends Handler{
        WeakReference<MainActivity> activityWeakReference;
        NetHandler(MainActivity mainActivity){
            activityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.recommendAdded:
                    RecommendFragment.instance.refreshList();
                break;
                case Constants.detailRequest:
                    PostDetailFragment.instance.setView();
                    break;
                case Constants.registrationSent:
                    MainActivity.instance.autoLogin();
                    break;
                case Constants.loginAttempt:
                    if(User.alreadLogin){
                        Toast.makeText(instance, "登陆成功 " + User.nickname, Toast.LENGTH_SHORT).show();
                        Thread backSender = new Thread(Sender.getInstance());
                        Thread backReceiver = new Thread(Receiver.getInstance());
                        backReceiver.start();
                        backSender.start();
                        SessionListFragment.instance.startPull();
                    }
                    break;
                case Constants.pullMsg:
                    SessionListFragment.instance.refreshSessions();
                    break;
                case Constants.myPublish:
                    MyPostListFragment.instance.addItem();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
