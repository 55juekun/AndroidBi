package com.aliyun.vodplayerview.view.speed;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alivc.player.VcPlayerLog;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayerview.theme.ITheme;
import com.aliyun.vodplayerview.widget.AliyunScreenMode;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.bi.R;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 倍速播放界面。用于控制倍速。
 * 在{@link AliyunVodPlayerView}中使用。
 */

public class SpeedView extends RelativeLayout implements ITheme {

    private static final String TAG = SpeedView.class.getSimpleName();

    private SpeedValue mSpeedValue;

    private View mMainSpeedView;
    //显示动画
    private Animation showAnim;
    //隐藏动画
    private Animation hideAnim;
    //动画是否结束
    private boolean animEnd = true;

    //0.25倍速
    private RadioButton mQuarternBtn;
    //0.5倍速
    private RadioButton mHalfBtn;
    // 正常倍速
    private RadioButton mNormalBtn;
    //2倍速
    private RadioButton mTwoTimeBtn;
    //4倍速
    private RadioButton mFourTimeBtn;
    //8倍速
    private RadioButton mEightTimeBtn;

    //切换结果的提示
    private TextView mSpeedTip;
    //屏幕模式
    private AliyunScreenMode mScreenMode;
    //倍速选择事件
    private OnSpeedClickListener mOnSpeedClickListener = null;
    //倍速是否变化
    private boolean mSpeedChanged = false;
    //选中的倍速的指示点的方块
    private int mSpeedDrawable = R.drawable.alivc_speed_dot_blue;
    //选中的倍速的指示点的文字
    private int mSpeedTextColor = R.color.alivc_blue;

    public SpeedView(Context context) {
        super(context);
        init();
    }

    public SpeedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public SpeedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化布局
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_view_speed, this, true);
        mMainSpeedView = findViewById(R.id.speed_view);
        mMainSpeedView.setVisibility(INVISIBLE);

        //找出控件
        mQuarternBtn = (RadioButton) findViewById(R.id.quarter);
        mHalfBtn = (RadioButton) findViewById(R.id.half);
        mNormalBtn = (RadioButton) findViewById(R.id.normal);
        mTwoTimeBtn = (RadioButton) findViewById(R.id.two);
        mFourTimeBtn=findViewById(R.id.four);
        mEightTimeBtn=findViewById(R.id.eight);

        mSpeedTip = (TextView) findViewById(R.id.speed_tip);
        mSpeedTip.setVisibility(INVISIBLE);

        //对每个倍速项做点击监听
        mQuarternBtn.setOnClickListener(mClickListener);
        mNormalBtn.setOnClickListener(mClickListener);
        mHalfBtn.setOnClickListener(mClickListener);
        mTwoTimeBtn.setOnClickListener(mClickListener);
        mFourTimeBtn.setOnClickListener(mClickListener);
        mEightTimeBtn.setOnClickListener(mClickListener);

        //倍速view使用到的动画
        showAnim = AnimationUtils.loadAnimation(getContext(), R.anim.view_speed_show);
        hideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.view_speed_hide);
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //显示动画开始的时候，将倍速view显示出来
                animEnd = false;
                mMainSpeedView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animEnd = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animEnd = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //隐藏动画结束的时候，将倍速view隐藏掉
                mMainSpeedView.setVisibility(INVISIBLE);
                if (mOnSpeedClickListener != null) {
                    mOnSpeedClickListener.onHide();
                }

                //如果倍速有变化，会提示倍速变化的消息
                if (mSpeedChanged) {
                    String times = "";
                    if (mSpeedValue == SpeedValue.Quartern) {
                        times = getResources().getString(R.string.alivc_speed_quarter_times);
                    } else if (mSpeedValue == SpeedValue.Half) {
                        times = getResources().getString(R.string.alivc_speed_half_times);
                    } else if (mSpeedValue == SpeedValue.Normal) {
                        times = getResources().getString(R.string.alivc_speed_one_times);
                    } else if (mSpeedValue == SpeedValue.Twice) {
                        times = getResources().getString(R.string.alivc_speed_twice_times);
                    } else if (mSpeedValue == SpeedValue.Four) {
                        times = getResources().getString(R.string.alivc_speed_four_times);
                    } else if (mSpeedValue == SpeedValue.Eight) {
                        times = getResources().getString(R.string.alivc_speed_eight_times);
                    }
                    String tips = getContext().getString(R.string.alivc_speed_tips, times);
                    mSpeedTip.setText(tips);
                    mSpeedTip.setVisibility(VISIBLE);
                    mSpeedTip.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSpeedTip.setVisibility(INVISIBLE);
                        }
                    }, 1000);
                }
                animEnd = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        setSpeed(SpeedValue.Normal);
//监听view的Layout事件
        getViewTreeObserver().addOnGlobalLayoutListener(new MyLayoutListener());
    }


    /**
     * 设置主题
     *
     * @param theme 支持的主题
     */
    @Override
    public void setTheme(AliyunVodPlayerView.Theme theme) {

        mSpeedDrawable = R.drawable.alivc_speed_dot_blue;
        mSpeedTextColor = R.color.alivc_blue;
        //根据主题变化对应的颜色
        if (theme == AliyunVodPlayerView.Theme.Blue) {
            mSpeedDrawable = R.drawable.alivc_speed_dot_blue;
            mSpeedTextColor = R.color.alivc_blue;
        } else if (theme == AliyunVodPlayerView.Theme.Green) {
            mSpeedDrawable = R.drawable.alivc_speed_dot_green;
            mSpeedTextColor = R.color.alivc_green;
        } else if (theme == AliyunVodPlayerView.Theme.Orange) {
            mSpeedDrawable = R.drawable.alivc_speed_dot_orange;
            mSpeedTextColor = R.color.alivc_orange;
        } else if (theme == AliyunVodPlayerView.Theme.Red) {
            mSpeedDrawable = R.drawable.alivc_speed_dot_red;
            mSpeedTextColor = R.color.alivc_red;
        }

        updateBtnTheme();
    }

    /**
     * 更新按钮的颜色之类的
     */
    private void setRadioButtonTheme(RadioButton button) {
        if (button.isChecked()) {
            button.setCompoundDrawablesWithIntrinsicBounds(0, mSpeedDrawable, 0, 0);
            button.setTextColor(getResources().getColor(mSpeedTextColor));
        } else {
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            button.setTextColor(getResources().getColor(R.color.alivc_white));
        }
    }

    private class MyLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private AliyunScreenMode lastLayoutMode = null;

        @Override
        public void onGlobalLayout() {
            if (mMainSpeedView.getVisibility() == VISIBLE) {

                //防止重复设置
                if (lastLayoutMode == mScreenMode) {
                    return;
                }

                setScreenMode(mScreenMode);
                lastLayoutMode = mScreenMode;
            }
        }
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mOnSpeedClickListener == null) {
                return;
            }

            if (view == mNormalBtn) {
                mOnSpeedClickListener.onSpeedClick(SpeedValue.Normal);
            } else if (view == mQuarternBtn) {
                mOnSpeedClickListener.onSpeedClick(SpeedValue.Quartern);
            } else if (view == mHalfBtn) {
                mOnSpeedClickListener.onSpeedClick(SpeedValue.Half);
            } else if (view == mTwoTimeBtn) {
                mOnSpeedClickListener.onSpeedClick(SpeedValue.Twice);
            } else if (view == mFourTimeBtn) {
                mOnSpeedClickListener.onSpeedClick(SpeedValue.Four);
            } else if (view == mEightTimeBtn) {
                mOnSpeedClickListener.onSpeedClick(SpeedValue.Eight);
            }
        }

    };

    /**
     * 设置倍速点击事件
     *
     * @param l
     */
    public void setOnSpeedClickListener(OnSpeedClickListener l) {
        mOnSpeedClickListener = l;
    }

    /**
     * 设置当前屏幕模式。不同的模式，speedView的大小不一样
     *
     * @param screenMode
     */
    public void setScreenMode(AliyunScreenMode screenMode) {
        ViewGroup.LayoutParams speedViewParam = mMainSpeedView.getLayoutParams();


        if (screenMode == AliyunScreenMode.Small) {
            //小屏的时候，是铺满整个播放器的
            speedViewParam.width = getWidth();
            speedViewParam.height = getHeight();
        } else if (screenMode == AliyunScreenMode.Full) {
            //如果是全屏的，就显示一半
            AliyunVodPlayerView parentView = (AliyunVodPlayerView) getParent();
            IAliyunVodPlayer.LockPortraitListener lockPortraitListener = parentView.getLockPortraitMode();
            if (lockPortraitListener == null) {
                //没有设置这个监听，说明不是固定模式，按正常的界面显示就OK
                speedViewParam.width = getWidth() / 2;
            } else {
                speedViewParam.width = getWidth();
            }
            speedViewParam.height = getHeight();
        }

        VcPlayerLog.d(TAG, "setScreenModeStatus screenMode = " + screenMode.name() + " , width = " + speedViewParam.width + " , height = " + speedViewParam.height);
        mScreenMode = screenMode;
        mMainSpeedView.setLayoutParams(speedViewParam);
    }

    /**
     * 倍速监听
     */
    public interface OnSpeedClickListener {
        /**
         * 选中某个倍速
         *
         * @param value 倍速值
         */
        void onSpeedClick(SpeedValue value);

        /**
         * 倍速界面隐藏
         */
        void onHide();
    }

    /**
     * 倍速值
     */
    public static enum SpeedValue {
        /**
         * 0.25倍速
         */
        Quartern,
        /**
         * 0.5倍速
         */
        Half,
        /**
         * 正常倍速
         */
        Normal,
        /**
         * 2倍速
         */
        Twice,

        /**
         * 4倍速
         */
        Four,
        /**
         * 8倍速
         */
        Eight,
    }


    /**
     * 设置显示的倍速
     *
     * @param speedValue 倍速值
     */
    public void setSpeed(SpeedValue speedValue) {
        if (speedValue == null) {
            return;
        }

        if (mSpeedValue != speedValue) {
            mSpeedValue = speedValue;
            mSpeedChanged = true;
            updateSpeedCheck();
        } else {
            mSpeedChanged = false;
        }

        hide();

    }

    /**
     * 更新倍速选项的状态
     */
    private void updateSpeedCheck() {
        mQuarternBtn.setChecked(mSpeedValue == SpeedValue.Quartern);
        mHalfBtn.setChecked(mSpeedValue == SpeedValue.Half);
        mNormalBtn.setChecked(mSpeedValue == SpeedValue.Normal);
        mTwoTimeBtn.setChecked(mSpeedValue == SpeedValue.Twice);
        mFourTimeBtn.setChecked(mSpeedValue == SpeedValue.Four);
        mEightTimeBtn.setChecked(mSpeedValue == SpeedValue.Eight);

        updateBtnTheme();
    }

    /**
     * 更新选项的Theme
     */
    private void updateBtnTheme() {
        setRadioButtonTheme(mQuarternBtn);
        setRadioButtonTheme(mHalfBtn);
        setRadioButtonTheme(mNormalBtn);
        setRadioButtonTheme(mTwoTimeBtn);
        setRadioButtonTheme(mFourTimeBtn);
        setRadioButtonTheme(mEightTimeBtn);
    }

    /**
     * 显示倍速view
     *
     * @param screenMode 屏幕模式
     */
    public void show(AliyunScreenMode screenMode) {

        setScreenMode(screenMode);

        mMainSpeedView.startAnimation(showAnim);

    }

    /**
     * 隐藏
     */
    private void hide() {
        if (mMainSpeedView.getVisibility() == VISIBLE) {
            mMainSpeedView.startAnimation(hideAnim);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //动画没有结束的时候，触摸是没有效果的
        if (mMainSpeedView.getVisibility() == VISIBLE && animEnd) {
            hide();
            return true;
        }

        return super.onTouchEvent(event);
    }
}
