package com.parrot.freeflight.tracking;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.parrot.freeflight.video.VideoStageRenderer;

public class VideoStageGrabber extends VideoStageRenderer {

	private OnFrameCallback callback = null;
	private boolean isGrabbing = false;
	private int fps; 
	private double ratio = 0.5;
	private long lastScreen = 0;
	private Thread thread;
	
	
	public static abstract class OnFrameCallback{
		public abstract void onFrame(Bitmap bmap);
	}
	
	public VideoStageGrabber(Context context, Bitmap initialTexture) {
		super(context, initialTexture);
	}
	
	public void startGrabbing(int fps, OnFrameCallback call){
	
		isGrabbing = true;
		callback = call;
		this.fps = fps;
	}
	
	public void stopGrabbing() {
		isGrabbing = false;

	}

	public void onDrawFrame(GL10 gl){
		
		if(isGrabbing && callback != null){
			

			final long now = System.currentTimeMillis();
			
			if(now-lastScreen >= (1000/fps)){
				
				if(thread == null || thread.getState() == Thread.State.TERMINATED){
					final int w = (int) (screenWidth*ratio);
					final int h = (int) (screenHeight*ratio);
					final int x = (screenWidth-w)/2;
					final int y = (screenHeight-h)/2;
				    int screenshotSize = w * h;
				    final ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
			        bb.order(ByteOrder.nativeOrder());
			        gl.glReadPixels(x,y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
			        
				    
					thread = new Thread()
					{
					    public void run() 
					    {
					    	Bitmap map = createBitmapFromGLSurface2(x,y,w,h,bb);
						    callback.onFrame(map);
						    
					    }
					};
					thread.start();
				}
				//Log.i("video", "Video grabbed "+(now-lastScreen)+" "+((int)(1000/fps))+" executing: "+thread.isAlive());
				
			    lastScreen = System.currentTimeMillis();
			}
		}
		 
		super.onDrawFrame(gl);
	}
	
	
	private Bitmap createBitmapFromGLSurface2(int x, int y, int width, int height, ByteBuffer bb){
		
		Log.i("video","CreateBitmap x: "+x+" y: "+y+" w: "+width+" h: "+height);
        int screenshotSize = width * height;
        int pixelsBuffer[] = new int[screenshotSize];
        bb.asIntBuffer().get(pixelsBuffer);
        bb = null;
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        bitmap.setPixels(pixelsBuffer, screenshotSize - width, -width, 0,
                0, width, height);
        pixelsBuffer = null;

        short sBuffer[] = new short[screenshotSize];
        ShortBuffer sb = ShortBuffer.wrap(sBuffer);
        bitmap.copyPixelsToBuffer(sb);

        // Making created bitmap (from OpenGL points) compatible with
        // Android bitmap
        for (int i = 0; i < screenshotSize; ++i) {
            short v = sBuffer[i];
            sBuffer[i] = (short) (((v & 0x1f) << 11) | (v & 0x7e0) | ((v & 0xf800) >> 11));
        }
        sb.rewind();
        bitmap.copyPixelsFromBuffer(sb);
        return bitmap.copy(Bitmap.Config.ARGB_8888,false);
	}

}
