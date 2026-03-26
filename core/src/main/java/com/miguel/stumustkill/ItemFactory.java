package com.miguel.stumustkill;

public class ItemFactory {

    public static InventoryItem createMedkit() {
        boolean[][] shape = new boolean[1][1];
        shape[0][0] = true;
        return new InventoryItem("Medkit", shape);
    }

    public static InventoryItem createPistol() {
        boolean[][] shape = new boolean[2][1];
        shape[0][0] = true;
        return new InventoryItem("Pistol", shape);
    }

    public static InventoryItem createBodyArmor() {
        boolean[][] shape = new boolean[2][2];
        shape[0][0] = true;
        shape[1][0] = true;
        shape[0][1] = true;
        shape[1][1] = true;
        return new InventoryItem("Body Armor", shape);
    }
}
