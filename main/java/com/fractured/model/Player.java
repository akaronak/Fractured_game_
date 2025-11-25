package com.fractured.model;

public class Player {
    private String name;
    private int sanity = 100;
    private final Inventory inventory = new Inventory();

    public Player(String name) { this.name = name; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getSanity() { return sanity; }
    public void modifySanity(int delta) { sanity += delta; }
    public Inventory getInventory() { return inventory; }
}
