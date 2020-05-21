package com.cwc.vplayer.view.render.surface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.cwc.vplayer.utils.Debuger;
import com.cwc.vplayer.utils.MeasureHelper;
import com.cwc.vplayer.view.render.RenderView;
import com.cwc.vplayer.view.render.listener.VideoShotListener;
import com.cwc.vplayer.view.render.listener.VideoShotSaveListener;
import com.cwc.vplayer.view.render.listener.ISurfaceListener;

import java.io.File;


public class VSurfaceView extends SurfaceView implements SurfaceHolder.Callback2, IRenderView, MeasureHelper.MeasureFormVideoParamsListener {

    private ISurfaceListener mSurfaceListener;

    private MeasureHelper.MeasureFormVideoParamsListener mVideoParamsListener;

    private MeasureHelper measureHelper;

    public VSurfaceView(Context context) {
        super(context);
        init();
    }

    public VSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        measureHelper = new MeasureHelper(this, this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureHelper.prepareMeasure(widthMeasureSpec, heightMeasureSpec, (int) getRotation());
        setMeasuredDimension(measureHelper.getMeasuredWidth(), measureHelper.getMeasuredHeight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceAvailable(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceSizeChanged(holder.getSurface(), width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //清空释放
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceDestroyed(holder.getSurface());
        }
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
    }

    @Override
    public ISurfaceListener getISurfaceListener() {
        return mSurfaceListener;
    }

    @Override
    public void setISurfaceListener(ISurfaceListener surfaceListener) {
        getHolder().addCallback(this);
        this.mSurfaceListener = surfaceListener;
    }

    @Override
    public int getSizeH() {
        return getHeight();
    }

    @Override
    public int getSizeW() {
        return getWidth();
    }

    @Override
    public Bitmap initCover() {
        Debuger.printfLog(getClass().getSimpleName() + " not support initCover now");
        return null;
    }

    @Override
    public Bitmap initCoverHigh() {
        Debuger.printfLog(getClass().getSimpleName() + " not support initCoverHigh now");
        return null;
    }

    /**
     * 获取截图
     *
     * @param shotHigh 是否需要高清的
     */
    public void taskShotPic(VideoShotListener videoShotListener, boolean shotHigh) {
        Debuger.printfLog(getClass().getSimpleName() + " not support taskShotPic now");
    }

    /**
     * 保存截图
     *
     * @param high 是否需要高清的
     */
    public void saveFrame(final File file, final boolean high, final VideoShotSaveListener videoShotSaveListener) {
        Debuger.printfLog(getClass().getSimpleName() + " not support saveFrame now");
    }

    @Override
    public View getRenderView() {
        return this;
    }

    @Override
    public void setRenderTransform(Matrix transform) {
        Debuger.printfLog(getClass().getSimpleName() + " not support setRenderTransform now");
    }

    @Override
    public void setVideoParamsListener(MeasureHelper.MeasureFormVideoParamsListener listener) {
        mVideoParamsListener = listener;
    }

    @Override
    public int getCurrentVideoWidth() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getCurrentVideoWidth();
        }
        return 0;
    }

    @Override
    public int getCurrentVideoHeight() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getCurrentVideoHeight();
        }
        return 0;
    }

    /**
     * 添加播放的view
     */
    public static VSurfaceView addSurfaceView(Context context, ViewGroup textureViewContainer, int rotate,
                                              final ISurfaceListener surfaceListener,
                                              final MeasureHelper.MeasureFormVideoParamsListener videoParamsListener) {
        if (textureViewContainer.getChildCount() > 0) {
            textureViewContainer.removeAllViews();
        }
        VSurfaceView showSurfaceView = new VSurfaceView(context);
        showSurfaceView.setISurfaceListener(surfaceListener);
        showSurfaceView.setVideoParamsListener(videoParamsListener);
        showSurfaceView.setRotation(rotate);
        RenderView.addToParent(textureViewContainer, showSurfaceView);
        return showSurfaceView;
    }

}
