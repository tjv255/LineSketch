package com.example.linesketch;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

public class LineGroup implements Groupable {
    private ArrayList<Groupable> children;
    private RectF bounds;
    private int z;
    private double scale;

    public LineGroup(ArrayList<Groupable> items) {
        children = new ArrayList<>();
        for(Groupable item: items) {
            if(!item.hasChildren()) {
                Line l = (Line)item;
                children.add(new Line(l.getStartPoint(), l.getEndPoint()));
            }
            else {
                LineGroup g = (LineGroup)item;
                children.add(new LineGroup(g.children));
            }
        }


        setBounds();
        z = 0;
        scale = 1.0;
    }



    public void setBounds() {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        for(Groupable item: children) {
            if (!item.hasChildren()) {
                Line l = (Line) item;
                float left = (float)l.getLeft();
                if (left < minX)
                    minX = left;
                float right = (float) l.getRight();
                if (right > maxX)
                    maxX = right;
                float top = (float) l.getTop();
                if (top < minY)
                    minY = top;
                float bottom = (float) l.getBottom();
                if (bottom > maxY)
                    maxY = bottom;
            }
            else {
                LineGroup g = (LineGroup) item;
                if (g.hasChildren()) {
                    if ((float)g.getLeft() < minX) minX = (float)g.getLeft();
                    if ((float)g.getTop() < minY) minY = (float)g.getTop();
                    if ((float)g.getRight() > maxX) maxX = (float)g.getRight();
                    if ((float)g.getBottom() > maxY) maxY = (float)g.getBottom();
                }
            }
        }
        bounds = new RectF(minX, minY, maxX, maxY);
    }

    @Override
    public boolean hasChildren() {
        if(!children.isEmpty())
            return true;
        return false;
    }

    @Override
    public ArrayList<Groupable> getChildren() {
        return children;
    }

    @Override
    public boolean contains(double x, double y) {
        if (bounds.contains((float)x, (float)y))
            return true;
        return false;
    }

    @Override
    public boolean isContained(double x1, double y1, double x2, double y2) {
        return false;
    }

    @Override
    public void draw(Canvas c, boolean selected) {
        Paint boundsPaint = new Paint();
        boundsPaint.setColor(Color.CYAN);
        boundsPaint.setStyle(Paint.Style.STROKE);
        boundsPaint.setStrokeWidth(10);
        for(Groupable item: children) {
            if(item.hasChildren()) {
                item.draw(c, selected);
            }
            else {
                item.draw(c, selected);
            }
        }

        if(children.get(0).hasChildren() || children.size() != 1);
            c.drawRect(bounds, boundsPaint);
    }

    @Override
    public void move(double dx, double dy) {
        for(Groupable item: children) {
            if(!item.hasChildren()) {
                Line l = (Line)item;
                l.unscaledStartX += dx;
                l.unscaledStartY += dy;
                l.unscaledEndX += dx;
                l.unscaledEndY += dy;
            }
            item.move(dx, dy);
            setBounds();
        }
    }

    private void moveTo(double x, double y) {
        for(Groupable item: children) {
            if(!item.hasChildren()) {
                Line l = (Line)item;
                l.unscaledStartX = x + (l.unscaledStartX - bounds.centerX());
                l.unscaledStartY = y + (l.unscaledStartY - bounds.centerY());
                l.unscaledEndX = x + (l.unscaledEndX - bounds.centerX());
                l.unscaledEndY = y + (l.unscaledEndY - bounds.centerY());

                l.getStartPoint().x = (float)x + l.getStartPoint().x - bounds.centerX();
                l.getStartPoint().y = (float)y + l.getStartPoint().y - bounds.centerY();
                l.getEndPoint().x = (float)x + l.getEndPoint().x - bounds.centerX();
                l.getEndPoint().y = (float)y + l.getEndPoint().y - bounds.centerY();
            }
        }
    }

    @Override
    public double getLeft() {
        return bounds.left;
    }

    @Override
    public double getRight() {
        return bounds.right;
    }

    @Override
    public double getTop() {
        return bounds.top;
    }

    @Override
    public double getBottom() {
        return bounds.bottom;
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
        scale = 1;
        rotate(bounds.centerX(), bounds.centerY(), rot);
    }

    private void rotate(float cx, float cy, double rot) {
        for(Groupable item: children) {
            if (!item.hasChildren()) {
                Line l = (Line) item;
                l.resetOrigin();
                double x1 = l.getStartPoint().x - bounds.centerX();
                double y1 = l.getStartPoint().y - bounds.centerY();
                double x2 = x1 * Math.cos(rot) - y1 * Math.sin(rot);
                double y2 = x1 * Math.sin(rot) + y1 * Math.cos(rot);

                l.getStartPoint().x = (float) (x2 + bounds.centerX());
                l.getStartPoint().y = (float) (y2 + bounds.centerY());

                x1 = l.getEndPoint().x - bounds.centerX();
                y1 = l.getEndPoint().y - bounds.centerY();
                x2 = x1 * Math.cos(rot) - y1 * Math.sin(rot);
                y2 = x1 * Math.sin(rot) + y1 * Math.cos(rot);

                l.getEndPoint().x = (float) (x2 + bounds.centerX());
                l.getEndPoint().y = (float) (y2 + bounds.centerY());
                l.resetLength();
                setBounds();
            } else {

                LineGroup g = (LineGroup) item;
                double x1 = g.bounds.centerX() - cx;
                double y1 = g.bounds.centerY() - cy;
                double x2 = x1 * Math.cos(rot) - y1 * Math.sin(rot);
                double y2 = x1 * Math.sin(rot) + y1 * Math.cos(rot);

                g.moveTo((float) (x2) + cx, (float) (y2) + cy);
                setBounds();
                g.rotate(cx,cy,rot);
            }
        }
    }

    @Override
    public void scale(double s) {
        for(Groupable item: children) {
            if(!item.hasChildren()) {
                Line l = (Line)item;
                if(scale < 1.0)
                    s *= 3 ;
                scale += s;
                if(scale < 0.25)
                    scale = 0.25;

                // pseudo: v -> s * (v - p) + p
                l.getStartPoint().x = (float)scale * ((float)l.unscaledStartX - bounds.centerX()) +  bounds.centerX() ;
                l.getStartPoint().y = (float)scale * ((float)l.unscaledStartY - bounds.centerY()) +  bounds.centerY();
                l.getEndPoint().x = (float)scale * ((float)l.unscaledEndX - bounds.centerX()) +  bounds.centerX();
                l.getEndPoint().y =  (float)scale * ((float)l.unscaledEndY - bounds.centerY()) +  bounds.centerY();
                l.resetLength();
                setBounds();
            }
            else {
                item.scale(s);
            }
        }
    }


    public ArrayList<Groupable> deconstruct() {
        ArrayList<Groupable> l = new ArrayList<>();
        l.addAll(children);
        return l;
    }

    public RectF getBounds() {
        return bounds;
    }
}
