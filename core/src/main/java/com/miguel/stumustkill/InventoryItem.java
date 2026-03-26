package com.miguel.stumustkill;

public class InventoryItem {

    private String name;

    // Esta es la mini-matriz geométrica del objeto.
    // true = bloque sólido, false = espacio vacío.
    private boolean[][] shape;

    public InventoryItem(String name, boolean[][] shape) {
        this.name = name;
        this.shape = shape;
    }

    public String getName() { return name; }
    public boolean[][] getShape() { return shape; }

    public int getWidth() { return shape.length; }
    public int getHeight() { return shape[0].length; }
}
