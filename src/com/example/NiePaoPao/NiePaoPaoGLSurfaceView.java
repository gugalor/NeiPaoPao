package com.example.NiePaoPao;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import com.example.NiePaoPao.CrazyZombyConstent.E_SCENARIO;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Message;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by Administrator on 14-2-2.
 */
public class NiePaoPaoGLSurfaceView extends GLSurfaceView{
    DrawAnimal drawAnimal;
    DrawGrid drawGrid;
    private SceneRenderer mRenderer;
    Context mContext;
    static boolean m_bThreadRun = false;
    boolean istouch[][]=new boolean[(int)CrazyZombyConstent.GRID_NUM][(int)CrazyZombyConstent.GRID_NUM];
    int mWidth = 0;
    int mHeight = 0;
    int mGridX = 0;
    int mGridY = 0;
    int mStep = 0;
    int mYStart = 0;

    public NiePaoPaoGLSurfaceView(Activity activity) {
        super(activity);
        mContext = this.getContext();
        mRenderer = new SceneRenderer();	//场景渲染器
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置连续渲染模式
        for(int i=0;i<(int)CrazyZombyConstent.GRID_NUM;i++)
            for(int j=0;j<(int)CrazyZombyConstent.GRID_NUM;j++)
                istouch[i][j]=false;
            }

    //处理屏幕触摸事件
    @Override public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction()==MotionEvent.ACTION_DOWN){
        touchGameView(e);
        if(mGridX!=-1&&mGridY!=-1)
            if(istouch[mGridX][mGridY]==false){
            istouch[mGridX][mGridY]=true;
            CrazyZombyConstent.mp.start();
            }
        }
       return true;
    }

    private class SceneRenderer implements GLSurfaceView.Renderer
    {

        public void onDrawFrame(GL10 gl) {
            gl.glShadeModel(GL10.GL_SMOOTH);		//着色模式为平滑着色
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);//清除颜色缓冲区及深度缓冲区
            gl.glMatrixMode(GL10.GL_MODELVIEW);		//设置矩阵为模式矩阵
            gl.glLoadIdentity();					//设置当前矩阵为单位矩阵
            gl.glTranslatef(0f, 0f, -10f);
            for(int i = 0; i < (int)CrazyZombyConstent.GRID_NUM; i++)
            {
                for(int j = 0; j < (int)CrazyZombyConstent.GRID_NUM; j++)
                {
                    if(istouch[i][j])
                        drawAnimal.draw(gl,(i+j)%3+2,i,j);
                                drawAnimal.draw(gl,1,i,j);
                }
            }
            drawGrid.draw(gl);

        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {

            CrazyZombyConstent.REAL_WIDTH = width;
            CrazyZombyConstent.REAL_HEIGHT = height;
            CrazyZombyConstent.translateRatio = (float) width / height;
            CrazyZombyConstent.screentRatio = (float) width / height;
            CrazyZombyConstent.ADP_SIZE = CrazyZombyConstent.UNIT_SIZE * CrazyZombyConstent.VIEW_HEIGHT/height * width/CrazyZombyConstent.VIEW_WIDTH;
          //  ControlCenter.mScreenTouch = new ScreenTouch(mContext, width, height);
            mWidth = width;
            mHeight = height;
            mStep = (int) (width / CrazyZombyConstent.GRID_NUM);
            mYStart = (mHeight - mWidth) / 2;
            gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();

            gl.glOrthof(-CrazyZombyConstent.screentRatio*CrazyZombyConstent.GRID_NUM/2,
                    CrazyZombyConstent.screentRatio*CrazyZombyConstent.GRID_NUM/2,
                    -1*CrazyZombyConstent.GRID_NUM/2,
                    1*CrazyZombyConstent.GRID_NUM/2, 10, 100);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glDisable(GL10.GL_DITHER);			//�رտ�����
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_FASTEST);//�����ض�Hint��Ŀ��ģʽ������Ϊ����Ϊʹ�ÿ���ģʽ
            gl.glClearColor(0,0,0,0);            	//清楚颜色为黑色
            gl.glShadeModel(GL10.GL_SMOOTH);        //平滑
            gl.glEnable(GL10.GL_DEPTH_TEST);		//使能深度测试

            /*********透明效果设置***********/
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);

            drawGrid=new DrawGrid(initTexture(gl,R.drawable.grid));
            drawAnimal=new DrawAnimal(initTexture(gl,R.drawable.paopao));
        }

    }
    //初始化纹理的方法
    private int initTexture(GL10 gl, int drawableId)
    {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        int currTextureId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);//ָ����С���˷���
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);//ָ���Ŵ���˷���
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);//ָ��S�������ͼģʽ
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);//ָ��T�������ͼģʽ
        InputStream is = mContext.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try{
            bitmapTmp = BitmapFactory.decodeStream(is);
        }
        finally{
            try{
                is.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return currTextureId;
    }

    //触摸动作
    public void touchGameView(MotionEvent e) {

        float y = e.getY();
        float x = e.getX();
                mGridX = -1;
                mGridY = -1;
                if (x >= 0 && x < mStep) mGridX = 0;
                else if(x >= mStep && x < 2*mStep) mGridX = 1;
                else if(x >= 2*mStep && x < 3*mStep) mGridX = 2;
                else if(x >= 3*mStep && x < 4*mStep) mGridX = 3;
                else if(x >= 4*mStep && x < 5*mStep) mGridX = 4;
                else if(x >= 5*mStep && x < 6*mStep) mGridX = 5;
                else if(x >= 6*mStep && x < 7*mStep) mGridX = 6;

                if(y >= mYStart && y < mYStart + mStep) mGridY = 6;
                else if(y >= mYStart + mStep && y < mYStart + 2*mStep) mGridY = 5;
                else if(y >= mYStart + 2*mStep && y < mYStart + 3*mStep) mGridY = 4;
                else if(y >= mYStart + 3*mStep && y < mYStart + 4*mStep) mGridY = 3;
                else if(y >= mYStart + 4*mStep && y < mYStart + 5*mStep) mGridY = 2;
                else if(y >= mYStart + 5*mStep && y < mYStart + 6*mStep) mGridY = 1;
                else if(y >= mYStart + 6*mStep && y < mYStart + 7*mStep) mGridY = 0;
    }
}



