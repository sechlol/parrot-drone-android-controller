package com.parrot.freeflight.tracking;

import com.parrot.freeflight.service.DroneControlService;
import com.parrot.freeflight.ui.HudViewController;

public class Tracking {
	
	private DroneControlService droneControlService;
	private HudViewController view;
	
	private final float power = (float)0.5;
	
	public Tracking(DroneControlService droneControlService, HudViewController view) {
		this.droneControlService = droneControlService;
		this.view = view;
	}
	
	public void start() {
		
		view.setSwitchCameraButtonEnabled(false); // disable switching camera button
		view.setTracking(true); // Change the message of screen to stop
		//switchCamara();
		if(!droneControlService.isDownCamara()) {
			switchCamara();
		}
		//stop();
	}
	
	public void stop() {
		view.setSwitchCameraButtonEnabled(true); // enable switching camera button
		view.setTracking(false); // Change the message of screen to start
		//switchCamara(); // Return to front camara
		//takeOff(); // land
	}
	
	/*
	 *  Takes off if drone is not flying
	 *  Lands if it's flying
	 */
	public void takeOff() {
		droneControlService.triggerTakeOff();
	}
	
	public void moveRight() {
		droneControlService.moveRight(power);
	}
	
	public void moveLeft() {
		droneControlService.moveLeft(power);
	}
	
	public void moveForward() {
		droneControlService.moveForward(power);
	}
	
	public void moveBackward() {
		droneControlService.moveBackward(power);
	}
	
	public void moveUp() {
		droneControlService.moveUp(power);
	}
	
	public void moveDown() {
		droneControlService.moveDown(power);
	}
	
	public void switchCamara() {
		droneControlService.switchCamera();
	}

}
