package com.parrot.freeflight.tracking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.parrot.freeflight.video.VideoStageRenderer;

public class VideoStageGrabber extends VideoStageRenderer {

	private OnFrameCallback callback = null;
	private boolean isGrabbing = false;
	private int fps; 
	private long lastScreen = 0;
	
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
			long now = System.currentTimeMillis();
			
			if(now-lastScreen >= (1/fps)*1000){
				int w = screenWidth/2;
			    int h = screenHeight/2;
			    int x = w/2;
			    int y = h/2;
			    Bitmap map = createBitmapFromGLSurface2(x,y,w,h,gl);
			    callback.onFrame(map);
			}
		}
		 
		super.onDrawFrame(gl);
	}
	
	
	private Bitmap createBitmapFromGLSurface2(int x, int y, int width, int height, GL10 gl){
		
		Log.i("video","CreateBitmap x: "+x+" y: "+y+" w: "+width+" h: "+height);
        int screenshotSize = width * height;
        ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
        bb.order(ByteOrder.nativeOrder());
        gl.glReadPixels(x,y, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
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

	
	
	/*
	  if(count++ % 10 == 0){
			
			try {
				String s= Environment.getExternalStorageDirectory().getPath()+"/DCIM/AR.Drone/lol"+count+".jpg";
				File file = new File(s);
			    FileOutputStream fOut = new FileOutputStream(file);
			  
			    int w = screenWidth/2;
			    int h = screenHeight/2;
			    int x = w/2;
			    int y = h/2;
			    Bitmap map = createBitmapFromGLSurface2(x,y,w,h,gl);

			    if(map != null){
				    map.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
				    fOut.flush();
				    fOut.close();
		            //Log.i("video","written "+s);
			    }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	 * */
	
	

}
