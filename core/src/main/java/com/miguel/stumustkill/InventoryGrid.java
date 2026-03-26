package com.miguel.stumustkill;

public class InventoryGrid {

    private int columns;
    private int rows;

    // Si una casilla está vacía, valdrá 'null'. Si tiene un arma, apuntará a ese objeto.
    private InventoryItem[][] cells;

    public InventoryGrid(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        this.cells = new InventoryItem[columns][rows];
    }

    // Getters básicos para poder leer el tamaño desde fuera
    public int getColumns() { return columns; }
    public int getRows() { return rows; }

    public boolean canPlaceItem(InventoryItem item, int startX, int startY) {
        int itemWidth = item.getWidth();
        int itemHeight = item.getHeight();

        if (startX < 0 || startY < 0 || (startX + itemWidth) > getColumns() || (startY + itemHeight) > getRows()) {
            return false;
        }

        boolean[][] shape = item.getShape();

        for (int x = 0; x < itemWidth; x++) {
            for (int y = 0; y < itemHeight; y++) {
                if (shape[x][y]) {
                    if (cells[startX + x][startY + y] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
