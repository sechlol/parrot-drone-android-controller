package com.parrot.freeflight.tracking;

import android.util.Pair;

import com.parrot.freeflight.service.DroneControlService;

public class DroneMovements {
	
	public static Sequence square;
	
	private DroneControlService droneControlService;
	private boolean isFlying = false;
	private boolean executing = false;
	private final float power = (float)0.5;

	private class DelayedAction implements Runnable{
		int runFor;
		DeferredAction action;
		
		DelayedAction(DeferredAction act, int rfor){
			runFor = rfor;
			action = act;
		}

		@Override
		public void run() {
			if(runFor == 0 && executing)
				action.execute();
			else{
				int time = (int) (System.currentTimeMillis());
				int timeEnd = time+runFor;
				while(time < timeEnd && executing){
					action.execute();
					time = (int) (System.currentTimeMillis());
				}
			}
		}		
	};
	
	private abstract class DeferredAction{
		abstract void execute();
	}
	
	static {
		square = new Sequence();
		square.AddAction(SequenceType.TAKEOFF, 3000);
		square.AddAction(SequenceType.MOVE_DOWN, 500);
		square.AddAction(SequenceType.MOVE_FRONT, 1000);
		square.AddAction(SequenceType.TAKE_PHOTO, 500);
		square.AddAction(SequenceType.MOVE_RIGHT, 1000);
		square.AddAction(SequenceType.TAKE_PHOTO, 500);
		square.AddAction(SequenceType.MOVE_BACK, 1000);
		square.AddAction(SequenceType.TAKE_PHOTO, 500);
		square.AddAction(SequenceType.MOVE_LEFT, 1000);
		square.AddAction(SequenceType.TAKE_PHOTO, 500);
		square.AddAction(SequenceType.LAND, 0);

	}
	
	public DroneMovements(DroneControlService controlService) {
		droneControlService = controlService;
	}
	
	public void executeSequence(Sequence s){
		int seqLength = s.Length();
		int delay = 0;
		if(!executing){
			executing = true;
			for(int i=0; i<seqLength; i++){
				Pair<SequenceType, Integer> sequence = s.get(i);
				
				DeferredAction act;
				int runFor = sequence.second;
				
				switch(sequence.first){
				
				case TAKEOFF:
					runFor = 0;
					act = new DeferredAction(){void execute() {takeOff();}};
					break;
				case LAND:
					runFor = 0;
					act = new DeferredAction(){void execute() {land();}};
					break;
				case MOVE_FRONT:
					act = new DeferredAction(){void execute() {moveForward();}};
					break;
				case MOVE_BACK:
					act = new DeferredAction(){void execute() {moveBackward();}};
					break;
				case MOVE_RIGHT:
					act = new DeferredAction(){void execute() {moveRight();}};
					break;
				case MOVE_LEFT:
					act = new DeferredAction(){void execute() {moveLeft();}};
					break;
				case MOVE_UP:
					act = new DeferredAction(){void execute() {moveUp();}};
					break;
				case MOVE_DOWN:
					act = new DeferredAction(){void execute() {moveDown();}};
					break;
				case TURN_LEFT:
					act = new DeferredAction(){void execute() {turnLeft();}};
					break;
				case TURN_RIGHT:
					act = new DeferredAction(){void execute() {turnRight();}};
					break;
				case TAKE_PHOTO:
					runFor = 0;
					act = new DeferredAction(){void execute() {takePhoto();}};
					break;
				case SWITCH_CAMERA:
					runFor = 0;
					act = new DeferredAction(){void execute() {switchCamera();}};
					break;
				default:
					act = null;
				}
				
				new android.os.Handler().postDelayed(
					new DelayedAction(act, runFor),
					delay
				);
				
				delay+=sequence.second;
				
			}
			
			//stop the execution at the end of the sequence
			new android.os.Handler().postDelayed(
				new DelayedAction(new DeferredAction(){void execute() {executing = false;}}, 0),
				delay
			);
		}
	}
	
	public void takeOff() {
		if(!isFlying){
			droneControlService.triggerTakeOff();
			isFlying = true;
		}
	}
	
	public void land() {
		if(isFlying){
			droneControlService.triggerTakeOff();
			isFlying = false;
		}
	}
	
	public void moveRight() {
		droneControlService.moveRight(power);
	}
	
	public void moveLeft() {
		droneControlService.moveLeft(power);
	}
	
	public void turnRight() {
		droneControlService.turnRight(power);
	}
	
	public void turnLeft() {
		droneControlService.turnLeft(power);
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
	
	public void takePhoto() {
		droneControlService.takePhoto();
	}
	
	public void switchCamera() {
		droneControlService.switchCamera();
	}
	
	




}
