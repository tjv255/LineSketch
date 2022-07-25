/*
Tyler Vogel
11161080
tjv255
04/07/20
CMPT381 A4
 */
package com.example.linesketch;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import static java.lang.Math.sqrt;

public class SketchController implements View.OnTouchListener {
    SketchModel model;
    InteractionModel iModel;
    private Runnable longPressCheck;
    private Handler handler = new Handler();

    private enum State {READY, PENDING_DRAWING, DRAWING, PENDING_SELECT, DRAGGING, DRAG_OR_DELETE, STRETCHING};
    private State currentState = State.READY;
    private SeekBar scaleSlider;

    private float prevNormX;
    private float prevNormY;

    public SketchController(SeekBar scaleSlider) {
        longPressCheck = new Runnable() {
            @Override
            public void run() {
                // call a method to deal with the callback "event"
                SketchController.this.checkForLongPress();
            }
        };
        this.scaleSlider = scaleSlider;
        prevNormX = 0;
        prevNormY = 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float normX = event.getX();
        float normY = event.getY();
        float normDX = normX - prevNormX;
        float normDY = normY - prevNormY;
        prevNormX = normX;
        prevNormY = normY;

        switch (currentState) {
            case READY:
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        if (iModel.checkHandleHit(normX, normY)) {
                            model.internalChange = true; // lets activity know that  scale SeekBar has been reset
                            scaleSlider.setProgress(50);
                            currentState = State.STRETCHING;
                            break;
                        }
                        else if (iModel.checkSelectedHit(normX, normY)) {
                            currentState = State.DRAGGING;
                            break;
                        }
                        else if(model.checkHit(normX, normY)) {
                            handler.postDelayed(longPressCheck, 1000);
                            currentState = State.PENDING_SELECT;
                            break;
                        }
                        else {

                            currentState = State.PENDING_DRAWING;
                        } break;
                } break;
            case PENDING_DRAWING:
                switch(event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        iModel.unSelectLines();
                        currentState = State.READY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        iModel.startPath(normX, normY);
                        currentState = State.DRAWING;
                        break;
                } break;
            case DRAWING:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        iModel.addPath(normX, normY);
                        currentState = State.DRAWING;
                        break;
                    case MotionEvent.ACTION_UP:
//                        iModel.unSelectLines();
                        if(iModel.isLine()) {
                            model.addLine(iModel.getPoints().get(0), iModel.getPoints().get(iModel.getPoints().size()-1));
                        }
                        iModel.dropPath();
                        currentState = State.READY;
                        break;
                } break;
            case PENDING_SELECT:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        handler.removeCallbacks(longPressCheck);
                        currentState = State.READY;
                        break;
                    case MotionEvent.ACTION_UP:
                        Groupable l = model.getIndexedLine(); // line that was found by model.checkForHit()
                        handler.removeCallbacks(longPressCheck);
//                        System.out.println(longPressCheck.);
                        if(!iModel.getSelectedLines().isEmpty()) {
                            iModel.unSelectLines();
                        }
                        iModel.addLine(l);
                        currentState = State.READY;
                        break;
                } break;
            case DRAGGING:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        iModel.moveLines(normDX, normDY);
                        currentState = State.DRAGGING;
                        break;
                    case MotionEvent.ACTION_UP:
                        currentState = State.READY;
                        break;
                } break;
            case STRETCHING:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        iModel.recentSelection = true;
                        iModel.moveHandle(normDX, normDY);
                        currentState = State.STRETCHING;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(iModel.needsHandles()) {
                            Line l = (Line)iModel.getSelectedLines().get(0);
                            model.snapHandle(l, iModel.getSelectedHandle(), iModel.getStartHandle(), iModel.getEndHandle());
                        }
                        iModel.unSelectHandle();
                        currentState = State.READY;
                        break;
                }break;
        }
        return true;
    }

    private void checkForLongPress() {
        // (pseudo) Event: callback from timer
        switch (currentState) {
            case PENDING_SELECT:
                iModel.addLine(model.getIndexedLine());
                currentState = State.READY;
        }
    }

    public void changeScale(float newScale) {
        if (!iModel.getSelectedLines().isEmpty()) {
            iModel.scaleLines(newScale);
        }
    }

    public void changeRotation(float newRotation) {
        model.internalChange = true;
        scaleSlider.setProgress(50);
        if (!iModel.getSelectedLines().isEmpty()) {
            iModel.rotateLines( (newRotation* 4 * 3.14f) / 200);
        }
    }

    public void cut() {
        if(iModel.getSelectedLines() != null) {
            model.cut(iModel.getSelectedLines());
            iModel.getSelectedLines().clear();
        }
    }

    public void copy() {
        if(iModel.getSelectedLines() != null) {
            model.copy(iModel.getSelectedLines());
        }
    }

    public void paste() {
        if(iModel.getSelectedLines() != null) {
            iModel.setSelectedLines(model.paste());
        }
    }

    public void setModel(SketchModel model) {
        this.model = model;
    }

    public void setIModel(InteractionModel iModel) {
        this.iModel = iModel;
    }
}
