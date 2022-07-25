/*
Tyler Vogel
11161080
tjv255
04/07/20
CMPT381 A4
 */
package com.example.linesketch;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class InteractionModel {
    private Path path;
    private ArrayList<PointF> points;
    private ArrayList<SketchListener> subscribers;

    private ArrayList<Groupable> selectedLines;
    protected boolean recentSelection;
    private Circle startHandle, endHandle, selectedHandle;

    public InteractionModel() {
        selectedLines = new ArrayList<>();
        subscribers = new ArrayList<>();
        points = new ArrayList<>();
        path = new Path();
    }
    public boolean checkHandleHit(float x, float y) {
        if(startHandle == null) return false;
        if (startHandle.contains(x, y)) {
            selectedHandle = startHandle;
            return true;
        }
        else if(endHandle.contains(x, y)) {
            selectedHandle = endHandle;
            return true;
        }
        return false;
    }

    public boolean checkSelectedHit(float x, float y) {
        if(!selectedLines.isEmpty()){
            for(Groupable l: selectedLines) {
                if(l.contains(x, y)){
                    return true;
                }
            }
        }
        return false;
    }

    public void unSelectLines() {
        if(!selectedLines.isEmpty()) {
            if(selectedLines.size() == 1) {
                removeHandles();
            }
            for(Groupable item: selectedLines) {
                if(!item.hasChildren()) {
                    Line l = (Line)item;
                    l.resetOrigin();
                }
            }
            selectedLines.clear();
            notifySubscribers();
        }

    }


    public void unSelectHandle() {
        selectedHandle = null;
    }

    public void removeHandles() {
        startHandle = null;
        endHandle = null;
        selectedHandle = null;
    }

    public void moveHandle(float dX, float dY) {
        if(this.needsHandles()) {
            Line selectedLine = (Line)selectedLines.get(0);
            recentSelection = true; // handle moving resets the scale factor
            if (selectedHandle == startHandle) {
                selectedLine.moveStart(dX, dY);
                selectedHandle.moveTo(selectedLine.getStartPoint().x, selectedLine.getStartPoint().y);
            } else if (selectedHandle == endHandle) {
                selectedLine.moveEnd(dX, dY);
                selectedHandle.moveTo(selectedLine.getEndPoint().x, selectedLine.getEndPoint().y);
            }
            notifySubscribers();
        }

    }

    private void resetHandles() {
        if(this.needsHandles()) {
            Line selectedLine = (Line)selectedLines.get(0);
            startHandle.moveTo(selectedLine.getStartPoint().x, selectedLine.getStartPoint().y);
            endHandle.moveTo(selectedLine.getEndPoint().x, selectedLine.getEndPoint().y);
        }
    }

    public void addGroup(LineGroup g) {
        selectedLines.clear();
        selectedLines.add(g);
        notifySubscribers();
    }

    public void addLine(Groupable item) {
        selectedLines.add(item);
        recentSelection = true;
        if(this.needsHandles()) {
            Line l = (Line)item;
            startHandle = new Circle(l.getStartPoint().x, l.getStartPoint().y, 30);
            endHandle = new Circle(l.getEndPoint().x, l.getEndPoint().y, 30);
        }
        else if (selectedLines.size() == 2 && getStartHandle() != null) {
            removeHandles();
        }
        notifySubscribers();
    }

    public void moveLines(float dX, float dY) {
        for(Groupable selectedLine: selectedLines) {
            selectedLine.move(dX, dY);
            if(this.needsHandles()) {
                startHandle.move(dX, dY);
                endHandle.move(dX, dY);
            }
        }
        notifySubscribers();
    }

    public void rotateLines(float theta) {
        for(Groupable l: selectedLines) {
            l.rotate(theta);
            resetHandles();
        }
        notifySubscribers();
    }

    public void scaleLines(float theta) {
        for(Groupable l: selectedLines) {
            l.scale(theta);
            resetHandles();
        }
        notifySubscribers();
    }

    protected boolean needsHandles() {
        if(selectedLines.size() == 1 && !selectedLines.get(0).hasChildren())
            return true;
        return false;
    }

    // Methods for drawing new lines:
    //  For starting a new path
    public void startPath(float x, float y) {
        points.add(new PointF(x, y));
        path.moveTo(x, y);
        notifySubscribers();
    }
    //  for continuously adding new points to a path
    public void addPath(float x, float y) {
        points.add(new PointF(x, y));
        path.lineTo(x,y);
        notifySubscribers();
    }
    // Determines if the drawn path resembles a line
    public boolean isLine() {
        int ERROR_RANGE = 250;

        double firstX = points.get(0).x;
        double firstY = points.get(0).y;
        double lastX = points.get(points.size()-1).x;
        double lastY = points.get(points.size()-1).y;
        double m = (lastY - firstY) / (lastX - firstX)+.1;
        double b = firstY - firstX*m;
        if((abs(m) < .2) || abs(m) > 7) {
            return xXOrY();
        }
        for(PointF p: points){
            double expectedX = ((p.y - b) / m);
            if((p.x >= expectedX + ERROR_RANGE) || (p.x < expectedX - ERROR_RANGE)){
                return false;
            }
            double expectedY = (m * p.x + b);
            if((p.y >= expectedY + ERROR_RANGE) || (p.y < expectedY - ERROR_RANGE)){
                return false;
            }
        }
        return true;
    }
    /* Returns true if the maximum y values of this.points are within a small range */
    private boolean xXOrY() {
        int ERROR_RANGE = 130;

        float minX = points.get(0).x, maxX = points.get(0).x;
        float minY = points.get(0).y, maxY = points.get(0).y;
        for(PointF p: points) {
            if (minX > p.x) minX = p.x;
            if (maxX < p.x) maxX = p.x;
            if (minY > p.y) minY = p.y;
            if (maxY < p.y) maxY = p.y;
        }
        if(abs(maxX - minX) <= ERROR_RANGE) return true;
        if(abs(maxY - minY) <= ERROR_RANGE) return true;
        return false;
    }
    /* Resets path */
    public void dropPath() {
        points.clear();
        path.reset();
        notifySubscribers();
    }

    // Subscriber methods:
    public void addSubscriber(SketchListener sketchListener) {
        subscribers.add(sketchListener);
    }

    private void notifySubscribers() {
        for(SketchListener s: subscribers) {
            s.modelChanged();
        }
    }

    // Getters/Setters:

    public ArrayList<PointF> getPoints() {
        return points;
    }

    public Path getPath() {
        return path;
    }

    public ArrayList<Groupable> getSelectedLines() {
        return selectedLines;
    }

        public Circle getStartHandle() {
        return startHandle;
    }

    public Circle getEndHandle() {
        return endHandle;
    }

    public Circle getSelectedHandle() {
        return selectedHandle;
    }

    public void setSelectedLines(ArrayList<Groupable> lines) {
        selectedLines = lines;
        resetHandles();
    }
}
