package com.cwc.vplayer.view.display;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.cwc.vplayer.R;
import com.cwc.vplayer.utils.CommonUtil;
import com.cwc.vplayer.utils.Debuger;
import com.cwc.vplayer.utils.OrientationOption;
import com.cwc.vplayer.utils.OrientationUtils;

import java.lang.reflect.Constructor;

import static com.cwc.vplayer.utils.CommonUtil.getActionBarHeight;
import static com.cwc.vplayer.utils.CommonUtil.getStatusBarHeight;
import static com.cwc.vplayer.utils.CommonUtil.hideNavKey;
import static com.cwc.vplayer.utils.CommonUtil.hideSupportActionBar;
import static com.cwc.vplayer.utils.CommonUtil.showNavKey;
import static com.cwc.vplayer.utils.CommonUtil.showSupportActionBar;

/**
 * 处理全屏和小屏幕逻辑
 */

public abstract class BaseVideoPlayer extends VideoControlView {

    //保存系统状态ui
    protected int mSystemUiVisibility;

    //当前item框的屏幕位置
    protected int[] mListItemRect;

    //当前item的大小
    protected int[] mListItemSize;

    //是否需要在利用window实现全屏幕的时候隐藏actionbar
    protected boolean mActionBar = false;

    //是否需要在利用window实现全屏幕的时候隐藏statusbar
    protected boolean mStatusBar = false;

    //是否自动旋转
    protected boolean mRotateViewAuto = true;

    //旋转使能后是否跟随系统设置
    protected boolean mRotateWithSystem = true;

    //当前全屏是否锁定全屏
    protected boolean mLockLand = false;

    //是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏，注意，这时候默认旋转无效
    protected boolean mAutoFullWithSize = false;

    //是否需要竖屏全屏的时候判断状态栏
    protected boolean isNeedAutoAdaptation = false;

    //全屏动画是否结束了
    protected boolean mFullAnimEnd = true;

    //旋转工具类
    protected OrientationUtils mOrientationUtils;

    private boolean mIsOnlyRotateLand = false;
    //全屏返回监听，如果设置了，默认返回无效
    protected View.OnClickListener mBackFromFullScreenListener;
    protected Handler mInnerHandler = new Handler();

    public BaseVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public BaseVideoPlayer(Context context) {
        super(context);
    }

    public BaseVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
    }


    @Override
    public void onBackFullscreen() {
        clearFullscreenLayout();
    }

    /**
     * 小窗口
     **/
    @Override
    protected void setSmallVideoTextureView() {
        if (mProgressBar != null) {
            mProgressBar.setOnTouchListener(null);
            mProgressBar.setVisibility(INVISIBLE);
        }
        if (mFullscreenButton != null) {
            mFullscreenButton.setOnTouchListener(null);
            mFullscreenButton.setVisibility(INVISIBLE);
        }
        if (mCurrentTimeTextView != null) {
            mCurrentTimeTextView.setVisibility(INVISIBLE);
        }
        if (mRenderViewContainer != null) {
            mRenderViewContainer.setOnClickListener(null);
        }
    }

    /**
     * 处理锁屏屏幕触摸逻辑
     */
    @Override
    protected void lockTouchLogic() {
        super.lockTouchLogic();
        if (!mLockCurScreen) {
            if (mOrientationUtils != null)
                mOrientationUtils.setEnable(isRotateViewAuto());
        } else {
            if (mOrientationUtils != null)
                mOrientationUtils.setEnable(false);
        }
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        //确保开启竖屏检测的时候正常全屏
        checkAutoFullSizeWhenFull();
    }

    @Override
    public void onInfo(int what, int extra) {
        super.onInfo(what, extra);
        if (what == getVideoManager().getRotateInfoFlag()) {
            checkAutoFullSizeWhenFull();
        }
    }


    private ViewGroup getViewGroup() {
        return (ViewGroup) (CommonUtil.scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
    }

    /**
     * 移除没用的
     */
    private void removeVideo(ViewGroup vp, int id) {
        View old = vp.findViewById(id);
        if (old != null) {
            if (old.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) old.getParent();
                vp.removeView(viewGroup);
            }
        }
    }

    /**
     * 保存大小和状态
     */
    private void saveLocationStatus(Context context, boolean statusBar, boolean actionBar) {
        getLocationOnScreen(mListItemRect);
        if (context instanceof Activity) {
            int statusBarH = getStatusBarHeight(context);
            int actionBerH = getActionBarHeight((Activity) context);
            boolean isTranslucent = ((WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS & ((Activity) context).getWindow().getAttributes().flags)
                    == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            Debuger.printfLog("*************isTranslucent*************** " + isTranslucent);
            if (statusBar && !isTranslucent) {
                mListItemRect[1] = mListItemRect[1] - statusBarH;
            }
            if (actionBar) {
                mListItemRect[1] = mListItemRect[1] - actionBerH;
            }
        }
        mListItemSize[0] = getWidth();
        mListItemSize[1] = getHeight();
    }

    /**
     * 克隆切换参数
     *
     * @param from
     * @param to
     */
    protected void cloneParams(BaseVideoPlayer from, BaseVideoPlayer to) {
        to.mHadPlay = from.mHadPlay;
        to.mPlayTag = from.mPlayTag;
        to.mPlayPosition = from.mPlayPosition;
        to.mFullPauseBitmap = from.mFullPauseBitmap;
        to.mNeedShowWifiTip = from.mNeedShowWifiTip;
        to.mShrinkImageRes = from.mShrinkImageRes;
        to.mEnlargeImageRes = from.mEnlargeImageRes;
        to.mRotate = from.mRotate;
        to.mShowPauseCover = from.mShowPauseCover;
        to.mDismissControlTime = from.mDismissControlTime;
        to.mSeekRatio = from.mSeekRatio;
        to.mNetChanged = from.mNetChanged;
        to.mNetSate = from.mNetSate;
        to.mRotateWithSystem = from.mRotateWithSystem;
        to.mBackUpPlayingBufferState = from.mBackUpPlayingBufferState;
        to.mBackFromFullScreenListener = from.mBackFromFullScreenListener;
        to.mVideoProgressListener = from.mVideoProgressListener;
        to.mHadPrepared = from.mHadPrepared;
        to.mStartAfterPrepared = from.mStartAfterPrepared;
        to.mPauseBeforePrepared = from.mPauseBeforePrepared;
        to.mReleaseWhenLossAudio = from.mReleaseWhenLossAudio;
        to.mVideoAllCallBack = from.mVideoAllCallBack;
        to.mActionBar = from.mActionBar;
        to.mStatusBar = from.mStatusBar;
        to.mAutoFullWithSize = from.mAutoFullWithSize;
        to.mOverrideExtension = from.mOverrideExtension;
        if (from.mSetUpLazy) {
            to.setUpLazy(from.mOriginUrl, from.mCache, from.mCachePath,  from.mTitle);
            to.mUrl = from.mUrl;
        } else {
            to.setUp(from.mOriginUrl, from.mCache, from.mCachePath, from.mTitle);
        }
        to.setLooping(from.isLooping());
        to.setIsTouchWigetFull(from.mIsTouchWigetFull);
        to.setSpeed(from.getSpeed(), from.mSoundTouch);
        to.setStateAndUi(from.mCurrentState);
    }

    /**
     * 全屏的暂停的时候返回页面不黑色
     */
    private void pauseFullCoverLogic() {
        if (mCurrentState == VideoPlayer.CURRENT_STATE_PAUSE && mRenderView != null
                && (mFullPauseBitmap == null || mFullPauseBitmap.isRecycled()) && mShowPauseCover) {
            try {
                initCover();
            } catch (Exception e) {
                e.printStackTrace();
                mFullPauseBitmap = null;
            }
        }
    }

    /**
     * 全屏的暂停返回的时候返回页面不黑色
     */
    private void pauseFullBackCoverLogic(BaseVideoPlayer videoPlayer) {
        //如果是暂停状态
        if (videoPlayer.mCurrentState == VideoPlayer.CURRENT_STATE_PAUSE
                && videoPlayer.mRenderView != null && mShowPauseCover) {
            //全屏的位图还在，说明没播放，直接用原来的
            if (videoPlayer.mFullPauseBitmap != null
                    && !videoPlayer.mFullPauseBitmap.isRecycled() && mShowPauseCover) {
                mFullPauseBitmap = videoPlayer.mFullPauseBitmap;
            } else if (mShowPauseCover) {
                //不在了说明已经播放过，还是暂停的话，我们拿回来就好
                try {
                    initCover();
                } catch (Exception e) {
                    e.printStackTrace();
                    mFullPauseBitmap = null;
                }
            }
        }
    }

    /**
     * 全屏
     */
    protected void resolveFullVideoShow(Context context, final BaseVideoPlayer videoPlayer, final FrameLayout frameLayout) {
        LayoutParams lp = (LayoutParams) videoPlayer.getLayoutParams();
        lp.setMargins(0, 0, 0, 0);
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        videoPlayer.setLayoutParams(lp);
        videoPlayer.setIfCurrentIsFullscreen(true);
        mOrientationUtils = new OrientationUtils((Activity) context, videoPlayer, getOrientationOption());
        mOrientationUtils.setEnable(isRotateViewAuto());
        mOrientationUtils.setRotateWithSystem(mRotateWithSystem);
        mOrientationUtils.setOnlyRotateLand(mIsOnlyRotateLand);
        videoPlayer.mOrientationUtils = mOrientationUtils;

        final boolean isVertical = isVerticalFullByVideoSize();
        final boolean isLockLand = isLockLandByAutoFullSize();

        if (!isVertical && isLockLand && mOrientationUtils != null) {
            mOrientationUtils.resolveByClick();
        }
        videoPlayer.setVisibility(VISIBLE);
        frameLayout.setVisibility(VISIBLE);


        if (mVideoAllCallBack != null) {
            Debuger.printfError("onEnterFullscreen");
            mVideoAllCallBack.onEnterFullscreen(mOriginUrl, mTitle, videoPlayer);
        }
        mIfCurrentIsFullscreen = true;

        checkoutState();

    }

    /**
     * 恢复
     */
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, VideoPlayer videoPlayer) {

        if (oldF != null && oldF.getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) oldF.getParent();
            vp.removeView(viewGroup);
        }
        mCurrentState = getVideoManager().getLastState();
        if (videoPlayer != null) {
            cloneParams(videoPlayer, this);
        }
        getVideoManager().setListener(getVideoManager().lastListener());
        getVideoManager().setLastListener(null);
        setStateAndUi(mCurrentState);
        addTextureView();
        mSaveChangeViewTIme = System.currentTimeMillis();
        if (mVideoAllCallBack != null) {
            Debuger.printfError("onQuitFullscreen");
            mVideoAllCallBack.onQuitFullscreen(mOriginUrl, mTitle, this);
        }
        mIfCurrentIsFullscreen = false;
        if (mHideKey) {
            showNavKey(mContext, mSystemUiVisibility);
        }
        showSupportActionBar(mContext, mActionBar, mStatusBar);
        if (getFullscreenButton() != null) {
            getFullscreenButton().setImageResource(getEnlargeImageRes());
        }
    }


    /**
     * 退出window层播放全屏效果
     */
    @SuppressWarnings("ResourceType")
    protected void clearFullscreenLayout() {
        if (!mFullAnimEnd) {
            return;
        }
        mIfCurrentIsFullscreen = false;
        int delay = 0;
        if (mOrientationUtils != null) {
            delay = mOrientationUtils.backToProtVideo();
            mOrientationUtils.setEnable(false);
            if (mOrientationUtils != null) {
                mOrientationUtils.releaseListener();
                mOrientationUtils = null;
            }
        }

        delay = 0;

        final ViewGroup vp = getViewGroup();
        final View oldF = vp.findViewById(getFullId());
        if (oldF != null) {
            //此处fix bug#265，推出全屏的时候，虚拟按键问题
            VideoPlayer player = (VideoPlayer) oldF;
            player.mIfCurrentIsFullscreen = false;
        }

        mInnerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                backToNormal();
            }
        }, delay);

    }

    /**
     * 回到正常效果
     */
    @SuppressWarnings("ResourceType")
    protected void backToNormal() {

        final ViewGroup vp = getViewGroup();

        final View oldF = vp.findViewById(getFullId());
        final VideoPlayer videoPlayer;
        if (oldF != null) {
            videoPlayer = (VideoPlayer) oldF;
            //如果暂停了
            pauseFullBackCoverLogic(videoPlayer);
            resolveNormalVideoShow(oldF, vp, videoPlayer);
        } else {
            resolveNormalVideoShow(null, vp, null);
        }
    }

    protected Runnable mCheckoutTask = new Runnable() {
        @Override
        public void run() {
            VideoPlayer videoPlayer = getFullWindowPlayer();
            if (videoPlayer != null
                    && videoPlayer.mCurrentState != mCurrentState) {
                if (videoPlayer.mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START
                        && mCurrentState != CURRENT_STATE_PREPAREING) {
                    videoPlayer.setStateAndUi(mCurrentState);
                }
            }
        }
    };

    /**
     * 检查状态
     */
    protected void checkoutState() {
        removeCallbacks(mCheckoutTask);
        mInnerHandler.postDelayed(mCheckoutTask, 500);
    }

    /**
     * 是否竖屏模式的竖屏
     */
    protected boolean isVerticalVideo() {
        boolean isVertical = false;
        int videoHeight = getCurrentVideoHeight();
        int videoWidth = getCurrentVideoWidth();
        Debuger.printfLog("VideoBase isVerticalVideo  videoHeight " + videoHeight + " videoWidth " + videoWidth);
        Debuger.printfLog("VideoBase isVerticalVideo  mRotate " + mRotate);
        if (videoHeight > 0 && videoWidth > 0) {
            if (mRotate == 90 || mRotate == 270) {
                isVertical = videoWidth > videoHeight;
            } else {
                isVertical = videoHeight > videoWidth;
            }
        }
        return isVertical;
    }

    /**
     * 是否根据autoFullSize调整lockLand
     */
    protected boolean isLockLandByAutoFullSize() {
        boolean isLockLand = mLockLand;
        if (isAutoFullWithSize()) {
            isLockLand = true;
        }
        return isLockLand;
    }

    /**
     * 确保开启竖屏检测的时候正常全屏
     */
    protected void checkAutoFullSizeWhenFull() {
        if (mIfCurrentIsFullscreen) {
            //确保开启竖屏检测的时候正常全屏
            boolean isV = isVerticalFullByVideoSize();
            Debuger.printfLog("VideoBase onPrepared isVerticalFullByVideoSize " + isV);
            if (isV) {
                if (mOrientationUtils != null) {
                    mOrientationUtils.backToProtVideo();
                }
            }
        }
    }

    protected abstract int getFullId();

    protected abstract int getSmallId();


    /************************* 开放接口 *************************/

    /**
     * 是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏，注意，这时候默认旋转无效
     */
    public boolean isVerticalFullByVideoSize() {
        return isVerticalVideo() && isAutoFullWithSize();
    }

    /**
     * 旋转处理
     *
     * @param activity         页面
     * @param newConfig        配置
     * @param orientationUtils 旋转工具类
     */
    public void onConfigurationChanged(Activity activity, Configuration newConfig, OrientationUtils orientationUtils) {
        onConfigurationChanged(activity, newConfig, orientationUtils, true, true);

    }

    /**
     * 旋转处理
     *
     * @param activity         页面
     * @param newConfig        配置
     * @param orientationUtils 旋转工具类
     * @param hideActionBar    是否隐藏actionbar
     * @param hideStatusBar    是否隐藏statusbar
     */
    public void onConfigurationChanged(Activity activity, Configuration newConfig, OrientationUtils orientationUtils, boolean hideActionBar, boolean hideStatusBar) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            if (!isIfCurrentIsFullscreen()) {
                startWindowFullscreen(activity, hideActionBar, hideStatusBar);
            }
        } else {
            //新版本isIfCurrentIsFullscreen的标志位内部提前设置了，所以不会和手动点击冲突
            if (isIfCurrentIsFullscreen() && !isVerticalFullByVideoSize()) {
                backFromFull(activity);
            }
            if (orientationUtils != null) {
                orientationUtils.setEnable(true);
            }
        }

    }

    /**
     * 可配置旋转 OrientationUtils
     */
    public OrientationOption getOrientationOption() {
        return null;
    }

    /**
     * 利用window层播放全屏效果
     *
     * @param context
     * @param actionBar 是否有actionBar，有的话需要隐藏
     * @param statusBar 是否有状态bar，有的话需要隐藏
     */
    @SuppressWarnings("ResourceType, unchecked")
    public BaseVideoPlayer startWindowFullscreen(final Context context, final boolean actionBar, final boolean statusBar) {


        mSystemUiVisibility = ((Activity) context).getWindow().getDecorView().getSystemUiVisibility();

        hideSupportActionBar(context, actionBar, statusBar);

        if (mHideKey) {
            hideNavKey(context);
        }

        this.mActionBar = actionBar;

        this.mStatusBar = statusBar;

        mListItemRect = new int[2];

        mListItemSize = new int[2];

        final ViewGroup vp = getViewGroup();

        removeVideo(vp, getFullId());

        //处理暂停的逻辑
        pauseFullCoverLogic();

        if (mRenderViewContainer.getChildCount() > 0) {
            mRenderViewContainer.removeAllViews();
        }

        saveLocationStatus(context, statusBar, actionBar);

        //切换时关闭非全屏定时器
        cancelProgressTimer();

        boolean hadNewConstructor = true;

        try {
            BaseVideoPlayer.this.getClass().getConstructor(Context.class, Boolean.class);
        } catch (Exception e) {
            hadNewConstructor = false;
        }

        try {
            //通过被重载的不同构造器来选择
            Constructor<BaseVideoPlayer> constructor;
            final BaseVideoPlayer videoPlayer;
            if (!hadNewConstructor) {
                constructor = (Constructor<BaseVideoPlayer>) BaseVideoPlayer.this.getClass().getConstructor(Context.class);
                videoPlayer = constructor.newInstance(mContext);
            } else {
                constructor = (Constructor<BaseVideoPlayer>) BaseVideoPlayer.this.getClass().getConstructor(Context.class, Boolean.class);
                videoPlayer = constructor.newInstance(mContext, true);
            }

            videoPlayer.setId(getFullId());
            videoPlayer.setIfCurrentIsFullscreen(true);
            videoPlayer.setVideoAllCallBack(mVideoAllCallBack);

            cloneParams(this, videoPlayer);

            if (videoPlayer.getFullscreenButton() != null) {
                videoPlayer.getFullscreenButton().setImageResource(getShrinkImageRes());
                videoPlayer.getFullscreenButton().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mBackFromFullScreenListener == null) {
                            clearFullscreenLayout();
                        } else {
                            mBackFromFullScreenListener.onClick(v);
                        }
                    }
                });
            }

            if (videoPlayer.getBackButton() != null) {
                videoPlayer.getBackButton().setVisibility(VISIBLE);
                videoPlayer.getBackButton().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mBackFromFullScreenListener == null) {
                            clearFullscreenLayout();
                        } else {
                            mBackFromFullScreenListener.onClick(v);
                        }
                    }
                });
            }

            final LayoutParams lpParent = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.setBackgroundColor(Color.BLACK);

            LayoutParams lp = new LayoutParams(getWidth(), getHeight());
            frameLayout.addView(videoPlayer, lp);
            vp.addView(frameLayout, lpParent);
            videoPlayer.setVisibility(INVISIBLE);
            frameLayout.setVisibility(INVISIBLE);
            resolveFullVideoShow(context, videoPlayer, frameLayout);

            videoPlayer.addTextureView();

            videoPlayer.startProgressTimer();

            getVideoManager().setLastListener(this);
            getVideoManager().setListener(videoPlayer);

            checkoutState();
            return videoPlayer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean isRotateViewAuto() {
        if (mAutoFullWithSize) {
            return false;
        }
        return mRotateViewAuto;
    }

    /**
     * 是否开启自动旋转
     */
    public void setRotateViewAuto(boolean rotateViewAuto) {
        this.mRotateViewAuto = rotateViewAuto;
        if (mOrientationUtils != null) {
            mOrientationUtils.setEnable(rotateViewAuto);
        }
    }

    public boolean isLockLand() {
        return mLockLand;
    }

    /**
     * 一全屏就锁屏横屏，默认false竖屏，可配合setRotateViewAuto使用
     */
    public void setLockLand(boolean lockLand) {
        this.mLockLand = lockLand;
    }


    public boolean isRotateWithSystem() {
        return mRotateWithSystem;
    }

    /**
     * 是否更新系统旋转，false的话，系统禁止旋转也会跟着旋转
     *
     * @param rotateWithSystem 默认true
     */
    public void setRotateWithSystem(boolean rotateWithSystem) {
        this.mRotateWithSystem = rotateWithSystem;
        if (mOrientationUtils != null) {
            mOrientationUtils.setRotateWithSystem(rotateWithSystem);
        }
    }

    /**
     * 获取全屏播放器对象
     */
    @SuppressWarnings("ResourceType")
    public VideoPlayer getFullWindowPlayer() {
        ViewGroup vp = (ViewGroup) (CommonUtil.scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
        final View full = vp.findViewById(getFullId());
        VideoPlayer videoPlayer = null;
        if (full != null) {
            videoPlayer = (VideoPlayer) full;
        }
        return videoPlayer;
    }

    /**
     * 获取小窗口播放器对象
     */
    @SuppressWarnings("ResourceType")
    public VideoPlayer getSmallWindowPlayer() {
        ViewGroup vp = (ViewGroup) (CommonUtil.scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
        final View small = vp.findViewById(getSmallId());
        VideoPlayer videoPlayer = null;
        if (small != null) {
            videoPlayer = (VideoPlayer) small;
        }
        return videoPlayer;
    }

    /**
     * 获取当前长在播放的播放控件
     */
    public BaseVideoPlayer getCurrentPlayer() {
        if (getFullWindowPlayer() != null) {
            return getFullWindowPlayer();
        }
        if (getSmallWindowPlayer() != null) {
            return getSmallWindowPlayer();
        }
        return this;
    }

    /**
     * 全屏返回监听，如果设置了，默认返回动作无效
     * 包含返回键和全屏返回按键，前提是这两个按键存在
     */
    public void setBackFromFullScreenListener(OnClickListener backFromFullScreenListener) {
        this.mBackFromFullScreenListener = backFromFullScreenListener;
    }

    public void setFullHideActionBar(boolean actionBar) {
        this.mActionBar = actionBar;
    }

    public void setFullHideStatusBar(boolean statusBar) {
        this.mStatusBar = statusBar;
    }

    public boolean isFullHideActionBar() {
        return mActionBar;
    }

    public boolean isFullHideStatusBar() {
        return mStatusBar;
    }

    public int getSaveBeforeFullSystemUiVisibility() {
        return mSystemUiVisibility;
    }

    public void setSaveBeforeFullSystemUiVisibility(int systemUiVisibility) {
        this.mSystemUiVisibility = systemUiVisibility;
    }

    public boolean isAutoFullWithSize() {
        return mAutoFullWithSize;
    }

    /**
     * 是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏，注意，这时候默认旋转无效
     *
     * @param autoFullWithSize 默认false
     */
    public void setAutoFullWithSize(boolean autoFullWithSize) {
        this.mAutoFullWithSize = autoFullWithSize;
    }


    public boolean isNeedAutoAdaptation() {
        return isNeedAutoAdaptation;
    }

    /**
     * 是否需要适配在竖屏横屏时，由于刘海屏或者打孔屏占据空间，导致标题显示被遮盖的问题
     *
     * @param needAutoAdaptation 默认false
     */
    public void setNeedAutoAdaptation(boolean needAutoAdaptation) {
        isNeedAutoAdaptation = needAutoAdaptation;
    }

    public boolean isOnlyRotateLand() {
        return mIsOnlyRotateLand;
    }

    /**
     * 旋转时仅处理横屏
     */
    public void setOnlyRotateLand(boolean onlyRotateLand) {
        this.mIsOnlyRotateLand = onlyRotateLand;
        if (mOrientationUtils != null) {
            mOrientationUtils.setOnlyRotateLand(mIsOnlyRotateLand);
        }
    }


}
