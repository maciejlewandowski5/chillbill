package com.example.chillbill.model;

public enum Category {
    PURPLE(0),ORANGE(1),YELLOW(2),GREEN(3),BLUE(4);

    private int label;
    private Category(int label){
        this.label =label;
    }
    public int toInt(){
        return label;
    }
}

