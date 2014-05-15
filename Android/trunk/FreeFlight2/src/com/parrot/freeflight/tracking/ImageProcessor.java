package com.parrot.freeflight.tracking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
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
import android.os.Environment;
import android.util.Log;
import android.util.Pair;


public abstract class ImageProcessor {
	
	//Detector
    private Mat                  mRgba;
    private Scalar				 colorThreshold_1;
    private Scalar 				 colorThreshold_2;
    private double 				 lastX = -1;
    private double				 lastY = -1;
    private Mat 				 linesMat;
    private Size				 blurSize;
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
	private double[] color1;
	private double[] color2;   
	
	ImageProcessor(double[] mainColorInt, double[] secondaryColorInt){
		color1 		= mainColorInt;
		color2 		= mainColorInt;

        linesMat 	= new Mat(height, width, CvType.CV_8UC4);
        
        //Color thresholds for defining the detection range
       // colorThreshold_1 = new Scalar(color1[0],color1[1],color1[2]);
       // colorThreshold_2 = new Scalar(color2[0],color2[1],color2[2]);
     // colorThreshold_1 = new Scalar(120,120,50);
        // colorThreshold_2 = new Scalar(180,255,255);
      
        //Detects the violet-red
        // colorThreshold_1 = new Scalar(120,120,50);//rgb
        // colorThreshold_2 = new Scalar(180,256,256);//brg
        
      //Detects the red color
        colorThreshold_1 = new Scalar(78, 203,22); 
        colorThreshold_2 = new Scalar(255, 255, 255);
        blurSize 		= new Size(3,3); 	//Used for gaussian blur
       
        //Points defined for line drawing
        newPoint 		= new Point(); 
        lastPoint 		= new Point(); 
        
        //Scalars defined for line drawing (blur)
        double[] temp2 	= {0,0,255};
        double[] temp3 	= {0,0,0};
        lineScalar 		= new Scalar(temp2);
        lineResetScalar = new Scalar(temp3);
        lineCounter 	= 0; //Counter that checks amount of lines drawn in linesMat matrix
        
        //Variables for calculating the middle point
    	moment01	= 0;
        moment10 	= 0;
        area 		= 0;
        
        //Line positions
        posX 		= 0;
        posY 		= 0;
        rows 		= 0;
        cols 		= 0;
        directionX 	= 0;
        directionY 	= 0;
        height 		= 0;
        width 		= 0;
	
	}


	public void Process(Bitmap bmap){
		
		height = bmap.getHeight();
		width = bmap.getWidth();
		
		mRgba = new Mat(height, width, CvType.CV_8UC4);
        linesMat = new Mat(height, width, CvType.CV_8UC4);
				
        
		Utils.bitmapToMat(bmap, mRgba);
		
        Imgproc.blur(mRgba, mRgba, blurSize);
        Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGR2HSV);
        Core.inRange(mRgba, colorThreshold_1, colorThreshold_2, mRgba);
        Imgproc.blur(mRgba, mRgba, blurSize);
        imgMoments = Imgproc.moments(mRgba);
        calculateMoments(imgMoments);
        
        cols = mRgba.cols();
        rows = mRgba.rows();
        
        directionX = (posX - ((double) cols/2)) / ((double) cols/2);   // value from 0 to 1
        directionY = (posY - ((double) rows/2)) / ((double) rows/2);   // value from 0 to 1

        String result1 = String.format("%.4f", directionX);
        String result2 = String.format("%.4f", directionY);
        
        CalculateDirection(directionX,directionY);
	} 
	
	private void CalculateDirection(double posX, double posY){
		int x = (int) (posX*1000);
		int y = (int) (posY*1000);
		Log.i("track", "x:" + x + ", y:" + y + ", area: "+area);
		
		//don't move if the color is not recognized 
		if(posX == -1 && posY== -1)
			return;
		
		if(Math.abs(x) > 600 && Math.abs(y) > 600)
			return;
		
		if(Math.abs(x) > 300 || Math.abs(y) > 300){
			if(Math.abs(x) <= 600 && Math.abs(x) > Math.abs(y)){
				if(x > 0){
					// Go right
					OnTargetMoveRight();
				} else {
					// Go left
					OnTargetMoveLeft();
				}
			} else if(Math.abs(y) <= 600) {
				if(y > 0){
					//Go down
					OnTargetMoveDown();
				} else {
					//Go up
					OnTargetMoveUp();
				}
			}
		}
	}
	
    private void calculateMoments(Moments imgMoments){
    	// Check http://stackoverflow.com/questions/8895749/cvgetspatialmoment-in-opencv-2-0
    	moment01 = imgMoments.get_m01();
        moment10 = imgMoments.get_m10();
        area = imgMoments.get_m00(); //Changed this from get_mu11(); 
        
        // calculate the position of the ball
        if(area>1000){  
	        posX = (moment10/area);
	        posY = (moment01/area);     		
        }       
    }

	
	public abstract void  OnTargetMoveLeft();
	public abstract void  OnTargetMoveRight();
	public abstract void  OnTargetMoveUp();
	public abstract void  OnTargetMoveDown();
	public abstract void  OnTargetMoveFar();
	public abstract void  OnTargetMoveClose();
	
	/*public void Process(Bitmap bmap){
	try {
		count++;
		Log.i("track", "Process bitmap "+count);
		String s= Environment.getExternalStorageDirectory().getPath()+"/DCIM/AR.Drone/lol"+count+".jpg";
		File file = new File(s);
	    FileOutputStream fOut = new FileOutputStream(file);
	  
	   
	    if(bmap != null){
		    bmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
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
}*/
}
