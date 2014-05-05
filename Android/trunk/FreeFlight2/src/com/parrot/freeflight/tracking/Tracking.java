package com.parrot.freeflight.tracking;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import android.graphics.Bitmap;
import android.graphics.Color;
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
		processor = new ImageProcessor(Color.RED){
			public void OnTargetMoveLeft() {drone.moveLeft();}
			public void OnTargetMoveRight() {drone.moveRight();}
			public void OnTargetMoveUp() {drone.moveUp();}
			public void OnTargetMoveDown() {drone.moveDown();}
			public void OnTargetMoveFar() {drone.moveForward();}
			public void OnTargetMoveClose() {drone.moveBackward();}
		};
		
	}
	
	public void start() {
		
		view.setSwitchCameraButtonEnabled(false); // disable switching camera button
		view.setTracking(true); // Change the message of screen to stop
		grabber = (VideoStageGrabber) view.getRenderer();

		grabber.startGrabbing(10, new OnFrameCallback(){public void onFrame(Bitmap bmap) {
			processor.Process(bmap);
		}});
		
		isActive = true;
	}
	 
	public void stop() {
		isActive = false;
		grabber.stopGrabbing();
		view.setSwitchCameraButtonEnabled(true); // enable switching camera button
		view.setTracking(false); // Change the message of screen to start
	}
}
