/*
Tyler Vogel
11161080
tjv255
04/07/20
CMPT381 A4
 */
package com.example.linesketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class SketchView extends View implements SketchListener {
    SketchController controller;
    SketchModel model;
    InteractionModel iModel;
    Paint pathPaint;

    public SketchView(Context context) {
        super(context);
        pathPaint = new Paint();
        pathPaint.setColor(Color.LTGRAY);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(10);

        this.setBackgroundColor(Color.BLACK);

    }

    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawPath(iModel.getPath(), pathPaint);

        for(Groupable item: model.getChildren()) {
            boolean selected = iModel.getSelectedLines().contains(item);
            item.draw(canvas, selected);
            if (iModel.needsHandles()) {
                Paint circlePaint = new Paint();
                circlePaint.setColor(Color.YELLOW);
                circlePaint.setStyle(Paint.Style.FILL);

                canvas.drawCircle(iModel.getStartHandle().getCenterX(), iModel.getStartHandle().getCenterY(),
                        iModel.getStartHandle().getRadius(), circlePaint);
                canvas.drawCircle(iModel.getEndHandle().getCenterX(), iModel.getEndHandle().getCenterY(),
                        iModel.getEndHandle().getRadius(), circlePaint);
            }
        }
    }

    public void setController(SketchController controller) {
        this.controller = controller;
        this.setOnTouchListener(controller);
    }

    public void setModel(SketchModel model) {
        this.model = model;
    }

    public void setIModel(InteractionModel iModel) {
        this.iModel = iModel;
    }

    public void modelChanged(){
        invalidate();
    }

}
