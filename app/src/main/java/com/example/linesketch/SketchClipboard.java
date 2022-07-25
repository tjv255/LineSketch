package com.example.linesketch;

import java.util.ArrayList;

public class SketchClipboard {
    ArrayList<Groupable> children;

    public SketchClipboard() {
        children = new ArrayList<>();
    }

    public void deepCopy(ArrayList<Groupable> toCopy) {
        children.clear();
        for(Groupable item: toCopy) {
            if(!item.hasChildren()) {
                Line l = (Line)item;
                children.add(new Line(l.getStartPoint(), l.getEndPoint()));
            }
            else {
                LineGroup g = (LineGroup)item;
                children.add(new LineGroup(g.getChildren()));
            }
        }
    }




}
