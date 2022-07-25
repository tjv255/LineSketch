/*
Tyler Vogel
11161080
tjv255
04/07/20
CMPT381 A4
 */

package com.example.linesketch;

public class Circle {
    private float radius;
    private float centerX;
    private float centerY;

    public Circle(float x, float y, float aRadius) {
        radius = aRadius;
        centerX = x;
        centerY = y;
    }

    public boolean contains(float x, float y) {
        return (x <= centerX+radius+20 && x >= centerX-radius -20 && y<= centerY+radius +20 && y>= centerY-radius -20);
    }
    public void move(float dx, float dy) {
        centerX = centerX+dx;
        centerY = centerY+dy;
    }
    public void moveTo(float x, float y) {
        centerX = x;
        centerY = y;
    }

    public float getRadius() { return radius; }

    public float getCenterY() { return centerY; }

    public float getCenterX() { return centerX; }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setCenter(float x, float y){
        centerX = x;
        centerY = y;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }
}

