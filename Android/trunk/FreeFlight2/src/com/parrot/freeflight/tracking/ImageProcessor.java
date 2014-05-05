package com.parrot.freeflight.tracking;

import java.util.ArrayList;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;

public abstract class ImageProcessor {
	private int mainColor;
	private int secondColor;
	private ArrayList<Pair<Double,Double>> positions;
	
	//Detector
    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private Scalar				 colorThreshold_1;
    private Scalar 				 colorThreshold_2;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;
    private double 				 lastX = -1;
    private double				 lastY = -1;
    private Mat 				 linesMat;
    private Mat					 linesResetMap; 
    private Size				 blurSize;
	private Mat 				 imgHSV;
	private Mat 				 imgThresh;
    private Point 				 newPoint; 
    private Point 				 lastPoint;
    private Scalar				 lineScalar;
    private Scalar				 lineResetScalar; 
    private int 				 lineCounter;
    private int					 cols;
    private int					 rows;
    private int 				 height;
    private int 				 width;
    private double 				 moment01;
    private double 				 moment10;
    private double 				 area;
    private double[]			 tempPos1= {0, 0};
    private double[]			 tempPos2= {0, 0};
    private double 				 posX;
    private double 				 posY;
    private double				 directionX;
    private double				 directionY;
    private Moments 			 imgMoments;       
	
	
	ImageProcessor(int mainColorInt){
		mainColor = mainColorInt;
		positions = new ArrayList<Pair<Double,Double>>();
		//Tracking
//        mRgba = new Mat(height, width, CvType.CV_8UC4);
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
        height = 0;
        width = 0;
		
	}


	public void Process(Bitmap bmap){

		height = bmap.getHeight();
		width = bmap.getWidth();
		
		mRgba = new Mat(height, width, CvType.CV_8UC4);
        linesMat = new Mat(height, width, CvType.CV_8UC4);
        linesResetMap = new Mat(height, width, CvType.CV_8UC4);
				
		Utils.bitmapToMat(bmap, mRgba);
		
        Imgproc.blur(mRgba, mRgba, blurSize);
        Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGR2HSV);
        Core.inRange(mRgba, colorThreshold_1, colorThreshold_2, mRgba);
        Imgproc.blur(mRgba, mRgba, blurSize);
        imgMoments = Imgproc.moments(mRgba);
        calculateMoments(imgMoments);
//        mRgba = inputFrame.rgba();
//        Core.add(mRgba, linesMat, mRgba);

        
        cols = mRgba.cols();
        rows = mRgba.rows();

//        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
//        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        int x = (int) posX;
        int y = (int) posY;

        //Log.i(TAG, "Object coordinates: (" + x + ", " + y + ")");
        
        directionX = (posX - ((double) cols/2)) / ((double) cols/2);   // value from 0 to 1
        directionY = (posY - ((double) rows/2)) / ((double) rows/2);   // value from 0 to 1

        String result1 = String.format("%.4f", directionX);
        String result2 = String.format("%.4f", directionY);
        
//        Log.i(TAG, "Dir x and y: (" + result1 + ", " + result2 + ")");
	}
	
	private void CalculateDirection(int posX, int posY){
		if(Math.abs(posX) > 200 || Math.abs(posY) > 200){
			if(Math.abs(posX) > Math.abs(posY)){
				if(posX > 0){
					// Go right
				} else {
					// Go left
				}
			} else {
				if(posY > 0){
					//Go up
				} else {
					//Go down
				}
			}
		}
	}
	
    private void calculateMoments(Moments imgMoments){
    	// Check http://stackoverflow.com/questions/8895749/cvgetspatialmoment-in-opencv-2-0
    	moment01 = imgMoments.get_m01();
        moment10 = imgMoments.get_m10();
        area = imgMoments.get_m00(); //Changed this from get_mu11(); 
        if(area>1000){
          // calculate the position of the ball
          posX = (moment10/area);
          posY = (moment01/area);
      		if(lastX>=0 && lastY>=0 && posX>=0 && posY>=0)
              {
                  // Draw a yellow line from the previous point to the current point
                  // set Points for drawing the line
                  tempPos1[0] = posX;
                  tempPos1[1] = posY;
                  newPoint.set(tempPos1);
                  tempPos2[0] = lastX;
                  tempPos2[1] = lastY;
                  lastPoint.set(tempPos2);                  
                  // draw the line                  
                  try{
                	  Core.line(linesMat, newPoint, lastPoint, lineScalar, 4);
                  } finally { 
                	  
                  }
              }
      		lastX = posX;
      		lastY = posY;
      		//Log.v(ALARM_SERVICE, String.valueOf(posX));
      		lineCounter++;
      		if(lineCounter > 15) {
      			linesMat.setTo(lineResetScalar);
      			lineCounter = 0;
      		}
      		
        }       
    }
	
	
	public abstract void  OnTargetMoveLeft();
	public abstract void  OnTargetMoveRight();
	public abstract void  OnTargetMoveUp();
	public abstract void  OnTargetMoveDown();
	public abstract void  OnTargetMoveFar();
	public abstract void  OnTargetMoveClose();
	
}
