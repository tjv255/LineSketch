/*
Tyler Vogel
11161080
tjv255
04/07/20
CMPT381 A4
 */
package com.example.linesketch;


import android.graphics.PointF;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class SketchModel {
    private ArrayList<Groupable> children;
    private Groupable indexedLine;
    protected boolean internalChange;
    SketchClipboard clipboard;

    private ArrayList<SketchListener> subscribers;

    private int lineHitWidth = 200;

    public SketchModel(){
        children = new ArrayList<>();
        clipboard = new SketchClipboard();
        subscribers = new ArrayList<>();
    }

    public void addLine(PointF startPoint, PointF endPoint){
        Line newLine = new Line(startPoint, endPoint);
        children.add(newLine);
        notifySubscribers();
    }

    // adds a group to lines, then returns the group
    protected LineGroup createGroup(ArrayList<Groupable> selected){
        LineGroup newGroup = new LineGroup(selected);
        for(Groupable item: selected) {
            children.remove(item);
        }
        children.add(newGroup);
        return newGroup;
    }

    protected ArrayList<Groupable> ungroup(LineGroup selectedGroup) {
        ArrayList<Groupable> l = selectedGroup.deconstruct();
        children.remove(selectedGroup);
        children.addAll(l);
        notifySubscribers();
        return l;
    }

    public void removeItems(ArrayList<Groupable> items) {
        for(Groupable item: items) {
            removeItem(item);
        }
        notifySubscribers();
    }
    private void removeItem(Groupable item) {
        if(children.contains(item)) {
            children.remove(item);
        }
        else
            throw new NoSuchElementException("removeItem: The given item is not part of the model.");
    }


    public boolean checkHit(float x, float y) {
        for(Groupable l: children) {
            if(l.contains(x, y)) {
                indexedLine = l;
                return true;
            }
        } return false;
    }

    /*
        Prerequisite: selectedLine must check out to be a line, and not lineGroup
     */
    protected void snapHandle(Line selectedLine, Circle selectedArea, Circle startArea, Circle endArea) {
        for(Groupable item: children) {
            if(selectedLine != item && !item.hasChildren()) {
                Line l = (Line)item;
                if (selectedArea.contains(l.getStartPoint().x, l.getStartPoint().y)) {
                    selectedArea.moveTo(l.getStartPoint().x, l.getStartPoint().y);
                    if (selectedArea == startArea)
                        selectedLine.moveStartTo(l.getStartPoint().x, l.getStartPoint().y);
                    else if (selectedArea == endArea)
                        selectedLine.moveEndTo(l.getStartPoint().x, l.getStartPoint().y);
                } else if (selectedArea.contains(l.getEndPoint().x, l.getEndPoint().y)) {
                    selectedArea.moveTo(l.getEndPoint().x, l.getEndPoint().y);
                    if (selectedArea == startArea)
                        selectedLine.moveStartTo(l.getEndPoint().x, l.getEndPoint().y);
                    else if (selectedArea == endArea)
                        selectedLine.moveEndTo(l.getEndPoint().x, l.getEndPoint().y);
                }
            }
        }
        notifySubscribers();
    }

    public void cut(ArrayList<Groupable> items) {
        clipboard.deepCopy(items);
        removeItems(items);
        notifySubscribers();
    }

    public void copy(ArrayList<Groupable> items) {
        clipboard.deepCopy(items);
    }

    public ArrayList<Groupable> paste() {
        ArrayList<Groupable> pasted = new ArrayList<>();
        for(Groupable item: clipboard.children) {
            if(!item.hasChildren()) {
                Line l1 = (Line)item;
                Line l2 = new Line(l1.getStartPoint(), l1.getEndPoint());
                children.add(l2);
                pasted.add(l2);
            }
            else {
                LineGroup g1 = (LineGroup)item;
                LineGroup g2 = new LineGroup(g1.getChildren());
                children.add(g2);
                pasted.add(g2);
            }
        }
        notifySubscribers();
        return pasted;
    }

    public void addSubscriber(SketchListener sketchListener) {
        subscribers.add(sketchListener);
    }
    private void notifySubscribers() {
        for(SketchListener s: subscribers) {
            s.modelChanged();
        }
    }

    public ArrayList<Groupable> getChildren() {
        return children;
    }

    public int getLineHitWidth() {
        return lineHitWidth;
    }

    public Groupable getIndexedLine() {
        return indexedLine;
    }
}
