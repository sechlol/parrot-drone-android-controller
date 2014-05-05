package com.parrot.freeflight.tracking;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Pair;

public abstract class ImageProcessor {
	private int mainColor;
	private int secondColor;
	private ArrayList<Pair<Double,Double>> positions;
	
	ImageProcessor(int mainColorInt){
		mainColor = mainColorInt;
		positions = new ArrayList<Pair<Double,Double>>();
	}


	public void Process(Bitmap bmap){
		
	}
	
	private void CalculateDirection(){
		
	}
	
	public abstract void  OnTargetMoveLeft();
	public abstract void  OnTargetMoveRight();
	public abstract void  OnTargetMoveUp();
	public abstract void  OnTargetMoveDown();
	public abstract void  OnTargetMoveFar();
	public abstract void  OnTargetMoveClose();
	
}
