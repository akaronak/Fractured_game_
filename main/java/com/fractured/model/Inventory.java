package com.fractured.model;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final List<String> items = new ArrayList<>();

    public void add(String item) { items.add(item); }
    public boolean remove(String item) { return items.remove(item); }
    public List<String> list() { return new ArrayList<>(items); }
    public boolean contains(String item) { return items.contains(item); }
    public int size() { return items.size(); }
}
