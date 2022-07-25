package com.example.linesketch;

import android.graphics.Canvas;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface Groupable {
    boolean hasChildren();
    ArrayList<Groupable> getChildren();
    boolean contains(double x, double y);
    boolean isContained(double x1, double y1, double x2, double y2);
    void draw(Canvas c, boolean selected);
    void move(double dx, double dy);
    double getLeft();   // bounding box of the group
    double getRight();
    double getTop();
    double getBottom();
    void setZ(int newZ);    // Z-order for the group
    int getZ();
    void rotate(double rot);
    void scale(double s);

    // methods for rotate and scale
}
