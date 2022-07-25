/*
Tyler Vogel
11161080
tjv255
04/07/20
CMPT381 A4
 */
package com.example.linesketch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Line implements Groupable {
    private PointF startPoint, endPoint, midPoint;
    protected double len, lenX, lenY;
    protected double unscaledLen, unscaledLenX, unscaledLenY;
    protected double unscaledStartX, unscaledStartY, unscaledEndX, unscaledEndY;
    private double scale;
    private int z;



    public Line(PointF startPoint, PointF endPoint) {
        this.startPoint = new PointF(startPoint.x, startPoint.y);
        this.endPoint = new PointF(endPoint.x, endPoint.y);
        this.midPoint = new PointF(startPoint.x + (endPoint.x - startPoint.x)/2, startPoint.y + (endPoint.y - startPoint.y)/2);

        len = dist(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        lenX = distX(startPoint.x, endPoint.x);
        lenY = distY(startPoint.y, endPoint.y);
        unscaledLen = len();
        unscaledLenX = lenX();
        unscaledLenY = lenY();
        unscaledStartX = startPoint.x;
        unscaledStartY = startPoint.y;
        unscaledEndX = endPoint.x;
        unscaledEndY = endPoint.y;

        scale = 1.0;
        z = 0;
    }

    private double dist(double x1, double y1, double x2, double y2) {
        return  sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }
    private double distX(double x1, double x2) {
        return (x2 - x1);
    }
    private double distY(double y1, double y2) {
        return (y2 - y1);
    }

    public double len() {
        return dist(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
    }
    public double lenX() {
        return lenX = distX(startPoint.x, endPoint.x);
    }
    public double lenY() {
        return lenY = distY(startPoint.y, endPoint.y);
    }
/*
    private void setRatios() {
        len = dist(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        ratioA = (startPoint.y - endPoint.y) / len;
        ratioB = (endPoint.x - startPoint.x) / len;
        ratioC = -1 * ((startPoint.y - endPoint.y) * startPoint.x + (endPoint.x - startPoint.x) * startPoint.y) / len;
    }

    public double distanceFromLine (double x, double y) {
        return (ratioA * x + ratioB * y + ratioC);
    }
*/
//
    protected void resetLength() {
        len = dist(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        lenX = distX(startPoint.x, endPoint.x);
        lenY = distY(startPoint.y, endPoint.y);
        midPoint.set(startPoint.x + (endPoint.x - startPoint.x)/2, startPoint.y + (endPoint.y - startPoint.y)/2);
    }


    public void resetOrigin() {
        unscaledStartX = startPoint.x;
        unscaledStartY = startPoint.y;
        unscaledEndX = endPoint.x;
        unscaledEndY = endPoint.y;
        unscaledLen = dist(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        unscaledLenX = distX(startPoint.x, endPoint.x);
        unscaledLenY = distY(startPoint.y, endPoint.y);
    }



    /*6
 Given x and y coordinates, and a hit width value, answers whether the x, y coordinates exist
 within this line. */




/*
//    public boolean checkHitBox(float x, float y, int hitWidth) {
//        if(x > Math.min(endPoint.x, startPoint.x) && x < Math.max(startPoint.x, endPoint.x)) {
//            if (y > Math.min(endPoint.y, startPoint.y) && y < Math.max(startPoint.y, endPoint.y)) {
//                if (distanceFromLine(x, y) <= (float)hitWidth/2) {
//                    return true;
//                }
//            }
//        }
//        System.out.println(distanceFromLine(x, y));
//        return false;
//    }

 */

    public void moveStart(float dx, float dy) {
        startPoint.x += dx;
        startPoint.y += dy;
        resetLength();
        resetOrigin();
        scale = 1;
    }
    public void moveEnd(float dx, float dy) {
        endPoint.x += dx;
        endPoint.y += dy;
        resetLength();
        resetOrigin();
        scale = 1;
    }
    public void moveStartTo(float x, float y) {
        startPoint.x = x;
        startPoint.y = y;
        resetLength();
        resetOrigin();
        scale = 1;
    }
    public void moveEndTo(float x, float y) {
        endPoint.x = x;
        endPoint.y = y;
        resetLength();
        resetOrigin();
        scale = 1;
    }

    public PointF getStartPoint() {
        return startPoint;
    }

    public PointF getEndPoint() {
        return endPoint;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public ArrayList<Groupable> getChildren() {
        return null;
    }

    @Override
    public boolean contains(double x, double y) {
        float dx = (endPoint.x - startPoint.x);
        float dy = (endPoint.y - startPoint.y);
        if(dx == 0) dx = 1;
        float m = dy / dx;
        float b = startPoint.y - startPoint.x*m;

        if(x > Math.min(endPoint.x, startPoint.x) && x < Math.max(startPoint.x, endPoint.x)) {
            if(y > Math.min(endPoint.y, startPoint.y) && y < Math.max(startPoint.y, endPoint.y)) {
                double yy = m*x + b;
                if ((y <= yy + 200) && (y >= yy - 200)) {
                    System.out.println(y + "  " + yy);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isContained(double x1, double y1, double x2, double y2) {
        if(startPoint.x >= x1 && endPoint.x <= x2 && startPoint.y >= y1 && endPoint.y <= y2)
            return true;
        return false;
    }

    @Override
    public void draw(Canvas c, boolean selected) {
        Paint linePaint = new Paint();
        Paint wideningPaint = new Paint();
        linePaint.setStrokeWidth(15);
        wideningPaint.setStrokeWidth(100);
        if(selected) {
            linePaint.setColor(Color.YELLOW);
            wideningPaint.setColor(Color.YELLOW);
            wideningPaint.setAlpha(80);
        }
        else {
            linePaint.setColor(Color.GREEN);
            wideningPaint.setColor(Color.GREEN);
            wideningPaint.setAlpha(80);
        }
        c.drawLine(startPoint.x, startPoint.y,
                endPoint.x, endPoint.y, wideningPaint);
        c.drawLine(startPoint.x, startPoint.y,
                endPoint.x, endPoint.y, linePaint);
    }

    @Override
    public void move(double dx, double dy) {
        startPoint.x += dx;
        startPoint.y += dy;
        midPoint.x += dx;
        midPoint.y += dy;
        endPoint.x += dx;
        endPoint.y += dy;
    }

    @Override
    public double getLeft() {
        if(startPoint.x <= endPoint.x)
            return startPoint.x;
        return endPoint.x;
    }

    @Override
    public double getRight() {
        if(startPoint.x >= endPoint.x)
            return startPoint.x;
        return endPoint.x;
    }

    @Override
    public double getTop() {
        if(startPoint.y <= endPoint.y)
            return startPoint.y;
        return endPoint.y;
    }

    @Override
    public double getBottom() {
        if(startPoint.y >= endPoint.y)
            return startPoint.y;
        return endPoint.y;
    }

    @Override
    public void setZ(int newZ) {
        z = newZ;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public void rotate(double rot) {
        double rotation = 0 + rot;
        resetOrigin();
        startPoint.x = midPoint.x + (float)(Math.cos(rotation) * (lenX)/2 - Math.sin(rotation) * (lenY)/2);
        startPoint.y = midPoint.y + (float)(Math.sin(rotation) * (lenX)/2 + Math.cos(rotation) * (lenY)/2);
        endPoint.x = midPoint.x - (float)(Math.cos(rotation) * (lenX)/2 - Math.sin(rotation) * (lenY)/2);
        endPoint.y = midPoint.y - (float)(Math.sin(rotation) * (lenX)/2 + Math.cos(rotation) * (lenY)/2);
        resetLength();
        scale = 1;

    }

    @Override
    public void scale(double s) {
        if(scale < 1.0)
            s *= 3 ;
        scale += s;
        resetLength();
        startPoint.x = (float)(midPoint.x + unscaledLenX/2 * scale);
        startPoint.y = (float)(midPoint.y + unscaledLenY/2 * scale);
        endPoint.x = (float)(midPoint.x - unscaledLenX/2 * scale);
        endPoint.y = (float)(midPoint.y - unscaledLenY/2 * scale);
    }

    @Override
    public String toString() {
        return "Line{" +
                "startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", midPoint=" + midPoint +
                '}';
    }
}
