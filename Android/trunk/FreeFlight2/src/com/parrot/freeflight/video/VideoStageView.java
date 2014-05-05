package com.parrot.freeflight.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("WrongCall")
public class VideoStageView extends SurfaceView 
implements 
	SurfaceHolder.Callback
{
	public static boolean SHOW_FPS = false;
    //Measure frames per second.
    long now;
    int framesCount=0;
    int framesCountAvg=0;
    long framesTimer=0;
	
    long timeNow;
    long timePrev = 0;
    long timePrevFrame = 0;
    long timeDelta;
	
	private VideoStageRenderer renderer;
	private int width = 0;
	private int height = 0;
	private DrawThread invalidateThread;
	
	Paint fpsPaint = new Paint();
	
	public VideoStageView(Context context) {
		super(context);	
		
		Log.i("video","VideoStageView");
		getHolder().addCallback(this);
		
		 fpsPaint.setTextSize(30);
		 fpsPaint.setColor(Color.RED);
	}

	@Override
	protected void onDraw(Canvas canvas) 
	{
		Log.i("video","VideoStageView ondraw");
		if (renderer != null) {
			renderer.onDrawFrame(canvas);
		}
		else 
			super.onDraw(canvas);
		
		if (SHOW_FPS) {
	        now=System.currentTimeMillis();
	        canvas.drawText(framesCountAvg + " fps", 80, 70, fpsPaint);
	        framesCount++;
	        if(now-framesTimer>1000) {
	                framesTimer=now;
	                framesCountAvg=framesCount;
	                framesCount=0;
	        }
		}
	}
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
		super.onSizeChanged(w, h, oldw, oldh);
		Log.i("video","VideoStageView onsizechange");
		width = w;
		height = h;
		
		if (renderer != null) {
			renderer.onSurfaceChanged((Canvas)null, w, h);
		}
	}
	

	public void setRenderer(VideoStageRenderer renderer)
	{
		this.renderer = renderer;
		Log.i("video","VideoStageView setrenderer");
		if (width != 0 && height != 0) {
			renderer.onSurfaceChanged((Canvas)null, width, height);
		}
	}

	
	public void onStart()
	{
		onStop();
		Log.i("video","VideoStageView onstart");
	}
	
	
	public void onStop()
	{
		Log.i("video","VideoStageView onstop");
		if (invalidateThread != null) {
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) 
	{
		renderer.onSurfaceChanged((Canvas)null, getWidth(), getHeight());	
		Log.i("video","VideoStageView surfacechange");
	}

	public void surfaceCreated(SurfaceHolder holder) 
	{
       invalidateThread = new DrawThread(getHolder(), this);
       invalidateThread.setRunning(true);
       invalidateThread.start();
       Log.i("video","VideoStageView surfcreated");
	}

	public void surfaceDestroyed(SurfaceHolder holder) 
	{
	       boolean retry = true;
	       invalidateThread.setRunning(false);
	        while (retry) {
	            try {
	            	invalidateThread.join();
	                retry = false;
	            } catch (InterruptedException e) {

	            }
	        }
	        Log.i("video","VideoStageView surfdestr");
	}
	
    class DrawThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private VideoStageView view;
        private boolean run = false;

        public DrawThread(SurfaceHolder surfaceHolder, VideoStageView gameView) {
            this.surfaceHolder = surfaceHolder;
            this.view = gameView;
            Log.i("video","VideoStageView drawthread");
        }

        public void setRunning(boolean run) {
            this.run = run;
        }

        public SurfaceHolder getSurfaceHolder() {
            return surfaceHolder;
        }

        @Override
        public void run() {
            Canvas c;
            while (run) {
            	Log.i("video","VideoStageView drawthread run");
                c = null;

                //limit frame rate to max 60fps
                timeNow = System.currentTimeMillis();
                timeDelta = timeNow - timePrevFrame;
                if ( timeDelta < 16) {
                    try {
                        Thread.sleep(16 - timeDelta);
                    }
                    catch(InterruptedException e) {

                    }
                }
                
                timePrevFrame = System.currentTimeMillis();

                try {
            	  	renderer.updateVideoFrame();  
                	c = surfaceHolder.lockCanvas(null);

                	synchronized (surfaceHolder) {	
                		view.onDraw(c);
                	}                 
            	} finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

}
