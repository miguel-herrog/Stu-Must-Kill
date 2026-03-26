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

    public void rotate() {
        int oldWidth = getWidth();
        int oldHeight = getHeight();

        // 1. Invertimos las dimensiones para la nueva forma
        boolean[][] newShape = new boolean[oldHeight][oldWidth];

        // 2. Escaneamos la forma antigua bloque a bloque
        for (int x = 0; x < oldWidth; x++) {
            for (int y = 0; y < oldHeight; y++) {

                newShape[y][oldWidth - 1 - x] = shape[x][y];

            }
        }
        this.shape = newShape;
    }
}
