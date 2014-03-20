package com.parrot.freeflight.remotecontrollers;

import android.view.KeyEvent;

public class ControlButtonsFactory
{
    public static ControlButtons getRightHandedControls()
    {
        final ControlButtons result = new ControlButtons();
        result.setUpControllButton(ControlButtons.BUTTON_UP, KeyEvent.KEYCODE_DPAD_LEFT);
        result.setUpControllButton(ControlButtons.BUTTON_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT);
        result.setUpControllButton(ControlButtons.BUTTON_TURN_LEFT, KeyEvent.KEYCODE_DPAD_DOWN);
        result.setUpControllButton(ControlButtons.BUTTON_TURN_RIGHT, KeyEvent.KEYCODE_DPAD_UP);
        result.setUpControllButton(ControlButtons.BUTTON_ACCELEROMETR, KeyEvent.KEYCODE_MEDIA_PLAY);
        result.setUpControllButton(ControlButtons.BUTTON_CAMERA, KeyEvent.KEYCODE_PROG_YELLOW);
        result.setUpControllButton(ControlButtons.BUTTON_EMERGENCY, KeyEvent.KEYCODE_PROG_RED);
        result.setUpControllButton(ControlButtons.BUTTON_SALTO, KeyEvent.KEYCODE_DPAD_CENTER);
        result.setUpControllButton(ControlButtons.BUTTON_SETTINGS, KeyEvent.KEYCODE_MENU);
        result.setUpControllButton(ControlButtons.BUTTON_TAKE_OFF, KeyEvent.KEYCODE_PROG_BLUE);
        result.setUpControllButton(ControlButtons.BUTTON_PHOTO, KeyEvent.KEYCODE_INFO);
        result.setUpControllButton(ControlButtons.BUTTON_TRACK, KeyEvent.KEYCODE_PROG_GREEN);
        return result;
    }
    
    public static ControlButtons getLeftHandedControls()
    {
        final ControlButtons result = new ControlButtons();
        result.setUpControllButton(ControlButtons.BUTTON_UP, KeyEvent.KEYCODE_DPAD_RIGHT);
        result.setUpControllButton(ControlButtons.BUTTON_DOWN, KeyEvent.KEYCODE_DPAD_LEFT);
        result.setUpControllButton(ControlButtons.BUTTON_TURN_LEFT, KeyEvent.KEYCODE_DPAD_UP);
        result.setUpControllButton(ControlButtons.BUTTON_TURN_RIGHT, KeyEvent.KEYCODE_DPAD_DOWN);
        result.setUpControllButton(ControlButtons.BUTTON_ACCELEROMETR, KeyEvent.KEYCODE_MEDIA_PLAY);
        result.setUpControllButton(ControlButtons.BUTTON_CAMERA, KeyEvent.KEYCODE_PROG_GREEN);
        result.setUpControllButton(ControlButtons.BUTTON_EMERGENCY, KeyEvent.KEYCODE_PROG_BLUE);
        result.setUpControllButton(ControlButtons.BUTTON_SALTO, KeyEvent.KEYCODE_DPAD_CENTER);
        result.setUpControllButton(ControlButtons.BUTTON_SETTINGS, KeyEvent.KEYCODE_MENU);
        result.setUpControllButton(ControlButtons.BUTTON_TAKE_OFF, KeyEvent.KEYCODE_PROG_RED);
        result.setUpControllButton(ControlButtons.BUTTON_PHOTO, KeyEvent.KEYCODE_INFO);
        result.setUpControllButton(ControlButtons.BUTTON_TRACK, KeyEvent.KEYCODE_PROG_YELLOW);
        return result;
    }
        
}
