package com.parrot.freeflight.tracking;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.samples.colorblobdetect.ColorBlobDetector;

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
		
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        linesMat = new Mat(height, width, CvType.CV_8UC4);
        linesResetMap = new Mat(height, width, CvType.CV_8UC4);
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
        //Color thresholds for defining the detection range
        colorThreshold_1 = new Scalar(120,120,50);
        colorThreshold_2 = new Scalar(180,2556,256);
        blurSize = new Size(3,3); //Used for gaussian blur
        imgHSV = new Mat(); //Create new HSV matrix
        imgThresh = new Mat(); //ImgThreshold matrix for detecting the objects
        //Points defined for line drawing
        newPoint = new Point(); 
        lastPoint = new Point(); 
        //Scalars defined for line drawing (blur)
        double[] temp2 = {0,0,255};
        lineScalar = new Scalar(temp2);
        double[] temp3 = {0,0,0};
        lineResetScalar = new Scalar(temp3);
        lineCounter = 0; //Counter that checks amount of lines drawn in linesMat matrix
        //Variables for calculating the middle point
    	moment01 = 0;
        moment10 = 0;
        area = 0;
        //Line positions
        posX = 0;
        posY = 0;
        rows = 0;
        cols = 0;
        directionX = 0;
        directionY = 0;
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
