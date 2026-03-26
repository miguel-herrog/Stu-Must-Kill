package com.miguel.stumustkill;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class InventoryItem {
    private String name;
    private boolean[][] shape;

    // --- LA NUEVA HERRAMIENTA VISUAL ---
    // Usamos TextureRegion en lugar de Texture porque es mejor para optimizar más adelante
    private TextureRegion textureRegion;

    // Constructor actualizado
    public InventoryItem(String name, boolean[][] shape, TextureRegion textureRegion) {
        this.name = name;
        this.shape = shape;
        this.textureRegion = textureRegion;
    }

    public String getName() { return name; }
    public boolean[][] getShape() { return shape; }

    // Getter para que el juego pueda pedir la imagen
    public TextureRegion getTextureRegion() { return textureRegion; }

    public int getWidth() { return shape.length; }
    public int getHeight() { return shape[0].length; }

    public void rotate() {
        int oldWidth = getWidth();
        int oldHeight = getHeight();
        boolean[][] newShape = new boolean[oldHeight][oldWidth];
        for (int x = 0; x < oldWidth; x++) {
            for (int y = 0; y < oldHeight; y++) {
                newShape[y][oldWidth - 1 - x] = shape[x][y];
            }
        }
        this.shape = newShape;
    }
}
