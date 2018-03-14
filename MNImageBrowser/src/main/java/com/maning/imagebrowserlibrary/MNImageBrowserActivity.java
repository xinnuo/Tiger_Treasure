package com.maning.imagebrowserlibrary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

/**
 * 图片浏览的页面
 */
public class MNImageBrowserActivity extends AppCompatActivity {

    public final static String IntentKey_ImageList = "IntentKey_ImageList";
    public final static String IntentKey_CurrentPosition = "IntentKey_CurrentPosition";

    private Context context;

    private MNGestureView mnGestureView;
    private ViewPager viewPagerBrowser;
    private TextView tvNumShow;
    private RelativeLayout rl_black_bg;

    private ArrayList<String> imageUrlList = new ArrayList<>();
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFullScreen();
        setContentView(R.layout.activity_mnimage_browser);
        context = this;

        initIntent();

        initViews();

        initData();

        initViewPager();

    }

    private void setWindowFullScreen() {
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 19) {
            // 虚拟导航栏透明
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void initIntent() {
        imageUrlList = getIntent().getStringArrayListExtra(IntentKey_ImageList);
        currentPosition = getIntent().getIntExtra(IntentKey_CurrentPosition, 1);
    }

    private void initViews() {
        viewPagerBrowser = findViewById(R.id.viewPagerBrowser);
        mnGestureView = findViewById(R.id.mnGestureView);
        tvNumShow = findViewById(R.id.tvNumShow);
        rl_black_bg = findViewById(R.id.rl_black_bg);
    }

    private void initData(){
        tvNumShow.setText(String.valueOf((currentPosition + 1) + "/" + imageUrlList.size()));
    }

    private void initViewPager() {
        viewPagerBrowser.setAdapter(new MyAdapter());
        viewPagerBrowser.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPagerBrowser.setCurrentItem(currentPosition);
        viewPagerBrowser.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvNumShow.setText(String.valueOf((position + 1) + "/" + imageUrlList.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mnGestureView.setOnSwipeListener(new MNGestureView.OnSwipeListener() {
            @Override
            public void downSwipe() {
                finishBrowser();
            }

            @Override
            public void onSwiping(float deltaY) {
                tvNumShow.setVisibility(View.GONE);

                float mAlpha = 1 - deltaY / 500;
                if (mAlpha < 0.3) {
                    mAlpha = 0.3f;
                }
                if (mAlpha > 1) {
                    mAlpha = 1;
                }
                rl_black_bg.setAlpha(mAlpha);
            }

            @Override
            public void overSwipe() {
                tvNumShow.setVisibility(View.VISIBLE);
                rl_black_bg.setAlpha(1);
            }
        });
    }

    private void finishBrowser() {
        tvNumShow.setVisibility(View.GONE);
        rl_black_bg.setAlpha(0);
        finish();
        this.overridePendingTransition(0, R.anim.browser_exit_anim);
    }

    @Override
    public void onBackPressed() {
        finishBrowser();
    }


    private class MyAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyAdapter() {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return imageUrlList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View inflate = layoutInflater.inflate(R.layout.mn_image_browser_item_show_image, container, false);
            final PhotoView imageView = inflate.findViewById(R.id.imageView);
            RelativeLayout rl_browser_root = inflate.findViewById(R.id.rl_browser_root);
            final ProgressWheel progressWheel = inflate.findViewById(R.id.progressWheel);
            final RelativeLayout rl_image_placeholder_bg = inflate.findViewById(R.id.rl_image_placeholder_bg);
            final ImageView iv_fail = inflate.findViewById(R.id.iv_fail);

            iv_fail.setVisibility(View.GONE);

            String url = imageUrlList.get(position);
            Glide.with(context).load(url).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    imageView.setImageDrawable(resource);

                    progressWheel.setVisibility(View.GONE);
                    rl_image_placeholder_bg.setVisibility(View.GONE);
                    iv_fail.setVisibility(View.GONE);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    progressWheel.setVisibility(View.GONE);
                    iv_fail.setVisibility(View.VISIBLE);
                }
            });

            rl_browser_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishBrowser();
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishBrowser();
                }
            });

            container.addView(inflate);
            return inflate;
        }
    }

}
