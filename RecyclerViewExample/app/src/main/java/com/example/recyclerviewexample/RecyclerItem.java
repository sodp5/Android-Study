package com.example.recyclerviewexample;

public class RecyclerItem {
    private String name;
    private boolean man = false;

    public RecyclerItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public boolean isMan() {
        return man;
    }
    public void switchMan(){
        man = !man;
    }
}
