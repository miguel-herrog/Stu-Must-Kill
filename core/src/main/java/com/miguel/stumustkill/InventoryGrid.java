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

    public boolean addItem(InventoryItem item, int startX, int startY) {
        if (!canPlaceItem(item, startX, startY)) {
            return false;
        }

        boolean[][] shape = item.getShape();
        for (int x = 0; x < item.getWidth(); x++) {
            for (int y = 0; y < item.getHeight(); y++) {
               if (shape[x][y]) {
                   cells[startX + x][startY + y] = item;
               }
            }
        }
        return true;
    }

    public InventoryItem getItemAt(int x, int y) {
        return cells[x][y];
    }

    public void removeItemAt(int x, int y) {
        // 1. Capturamos el objeto que hay en la casilla tocada
        InventoryItem itemToRemove = cells[x][y];

        // 2. Si la casilla ya estaba vacía (null), no hay nada que borrar. Salimos.
        if (itemToRemove == null) {
            return;
        }

        // 3. Si hay un arma, recorremos TODO el maletín buscando sus partes
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {

                // Si la casilla actual guarda exactamente la misma arma que queremos borrar...
                if (cells[i][j] == itemToRemove) {
                    cells[i][j] = null; // ...la vaciamos dejándola en null
                }

            }
        }
    }
}
