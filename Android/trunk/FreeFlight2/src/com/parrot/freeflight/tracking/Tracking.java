package com.parrot.freeflight.tracking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.parrot.freeflight.service.DroneControlService;
import com.parrot.freeflight.tracking.VideoStageGrabber.OnFrameCallback;
import com.parrot.freeflight.ui.HudViewController;

public class Tracking {
	
	private DroneMovements drone;
	private ImageProcessor processor;
	private HudViewController view;
	private VideoStageGrabber grabber;
	private boolean isActive = false;
	
	public Tracking(DroneControlService droneControlService, HudViewController hud) {
		view = hud;
		drone = new DroneMovements(droneControlService);
		
		double[] color1 = new double[]{120,120,50};//Color.argb(1, 120,120,50);
		double[] color2 = new double[]{180,255,255};//Color.argb(1, 180,256,256);
		
		processor = new ImageProcessor(color1,color2){
			public void OnTargetMoveLeft() {/*drone.turnLeft();*/}
			public void OnTargetMoveRight() {/*drone.turnRight();*/}
			public void OnTargetMoveUp() {drone.moveUp();}
			public void OnTargetMoveDown() {drone.moveDown();}
			public void OnTargetMoveFar() {drone.moveForward();}
			public void OnTargetMoveClose() {drone.moveBackward();}
		};
	}
	
	public void start() {
		if(!isActive){
			
			view.setSwitchCameraButtonEnabled(false); // disable switching camera button
			view.setTracking(true); // Change the message of screen to stop
			grabber = (VideoStageGrabber) view.getRenderer();
	
			grabber.startGrabbing(30, new OnFrameCallback(){public void onFrame(Bitmap bmap) {
				processor.Process(bmap);
			}});
			
			/*grabber.startGrabbing(1, new OnFrameCallback(){public void onFrame(Bitmap bmap) {
				savePicture(bmap);
			}});*/
			
			isActive = true;
		}
	}
	 
	public void stop() {
		if(isActive){
			
			isActive = false;
			grabber.stopGrabbing();
			view.setSwitchCameraButtonEnabled(true); // enable switching camera button
			view.setTracking(false); // Change the message of screen to start
		}
	}
	
	private int count = 0;
	private void savePicture(Bitmap map){
		try {
			count++;
			String s= Environment.getExternalStorageDirectory().getPath()+"/DCIM/AR.Drone/lol"+count+".jpg";
			File file = new File(s);
		    FileOutputStream fOut = new FileOutputStream(file);
		  
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
}
