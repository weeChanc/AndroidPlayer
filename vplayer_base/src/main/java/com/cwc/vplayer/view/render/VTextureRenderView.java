package com.cwc.vplayer.view.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cwc.vplayer.player.base.model.VideoType;
import com.cwc.vplayer.utils.MeasureHelper;
import com.cwc.vplayer.view.render.listener.ISurfaceListener;


public abstract class VTextureRenderView extends FrameLayout implements ISurfaceListener, MeasureHelper.MeasureFormVideoParamsListener {

    //native绘制
    protected Surface mSurface;

    //渲染控件
    protected RenderView mRenderView;

    //渲染控件父类
    protected ViewGroup mRenderViewContainer;

    //满屏填充暂停位图
    protected Bitmap mFullPauseBitmap;

    //画面选择角度
    protected int mRotate;

    public VTextureRenderView(@NonNull Context context) {
        super(context);
    }

    public VTextureRenderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VTextureRenderView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /******************** start render  listener****************************/


    @Override
    public void onSurfaceAvailable(Surface surface) {
        pauseLogic(surface, (mRenderView != null && mRenderView.getShowView() instanceof TextureView));
    }

    @Override
    public void onSurfaceSizeChanged(Surface surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceDestroyed(Surface surface) {
        //清空释放
        setDisplay(null);
        //同一消息队列中去release
        releaseSurface(surface);
        return true;
    }

    @Override
    public void onSurfaceUpdated(Surface surface) {
        //如果播放的是暂停全屏了
        releasePauseCover();
    }


    /******************** end render listener****************************/

    /**
     * 暂停逻辑
     */
    protected void pauseLogic(Surface surface, boolean pauseLogic) {
        mSurface = surface;
        if (pauseLogic)
            //显示暂停切换显示的图片
            showPauseCover();
        setDisplay(mSurface);
    }

    /**
     * 添加播放的view
     * 继承后重载addTextureView，继承RenderView后实现自己的IRenderView类，既可以使用自己自定义的显示层
     */
    protected void addTextureView() {
        mRenderView = new RenderView();
        mRenderView.addView(getContext(), mRenderViewContainer, mRotate, this, this);
    }

    /**
     * 获取布局参数
     *
     * @return
     */
    protected int getTextureParams() {
        boolean typeChanged = (VideoType.getShowType() != VideoType.SCREEN_TYPE_DEFAULT);
        return (typeChanged) ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT;
    }

    /**
     * 调整TextureView去适应比例变化
     */
    protected void changeTextureViewShowType() {
        if (mRenderView != null) {
            int params = getTextureParams();
            ViewGroup.LayoutParams layoutParams = mRenderView.getLayoutParams();
            layoutParams.width = params;
            layoutParams.height = params;
            mRenderView.setLayoutParams(layoutParams);
        }
    }

    /**
     * 暂停时初始化位图
     */
    protected void initCover() {
        if (mRenderView != null) {
            mFullPauseBitmap = mRenderView.initCover();
        }
    }

    /**
     * 小窗口渲染
     **/
    protected void setSmallVideoTextureView(OnTouchListener onTouchListener) {
        mRenderViewContainer.setOnTouchListener(onTouchListener);
        mRenderViewContainer.setOnClickListener(null);
        setSmallVideoTextureView();
    }

    /**
     * 获取渲染的代理层
     */
    public RenderView getRenderProxy() {
        return mRenderView;
    }

    //暂停时使用绘制画面显示暂停、避免黑屏
    protected abstract void showPauseCover();

    //清除暂停画面
    protected abstract void releasePauseCover();

    //小屏幕绘制层
    protected abstract void setSmallVideoTextureView();

    //设置播放
    protected abstract void setDisplay(Surface surface);

    //释放
    protected abstract void releaseSurface(Surface surface);

}
