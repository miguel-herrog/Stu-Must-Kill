package com.miguel.stumustkill;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class StuGame extends ApplicationAdapter {

    // NUESTRAS HERRAMIENTAS DE DIBUJO
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    InventoryGrid grid;

    // EL ESTADO DEL RATÓN
    InventoryItem draggedItem = null;
    int originGridX = -1;
    int originGridY = -1;
    int grabOffsetX = 0;
    int grabOffsetY = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        grid = new InventoryGrid(5, 4);
        grid.addItem(ItemFactory.createMedkit(), 0, 0);
        grid.addItem(ItemFactory.createBodyArmor(), 2, 1);
    }

    @Override
    public void render() {
        // 1. Calculamos SIEMPRE en qué casilla está el ratón (Global para todo el frame)
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        int invertedY = Gdx.graphics.getHeight() - mouseY;
        int gridX = mouseX / 50;
        int gridY = invertedY / 50;

        // --- FASE A: RECOGER UN OBJETO (Hacer Clic Izquierdo) ---
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // Añadimos comprobación para que no dé error si haces clic fuera de la pantalla
            if (draggedItem == null && gridX >= 0 && gridX < grid.getColumns() && gridY >= 0 && gridY < grid.getRows()) {

                InventoryItem itemToPick = grid.getItemAt(gridX, gridY);

                if (itemToPick != null) {
                    draggedItem = itemToPick;

                    // Escaneamos el ancla real del objeto
                    originGridX = gridX;
                    originGridY = gridY;

                    for (int i = 0; i < grid.getColumns(); i++) {
                        for (int j = 0; j < grid.getRows(); j++) {
                            if (grid.getItemAt(i, j) == itemToPick) {
                                if (i < originGridX) originGridX = i;
                                if (j < originGridY) originGridY = j;
                            }
                        }
                    }

                    // Calculamos el desfase (offset) de agarre
                    grabOffsetX = gridX - originGridX;
                    grabOffsetY = gridY - originGridY;

                    grid.removeItemAt(gridX, gridY);
                }
            }
        }

        // --- FASE B: ROTAR EL OBJETO (Mientras lo tenemos agarrado) ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && draggedItem != null) {
            draggedItem.rotate();
            System.out.println("¡Arma rotada en el aire!");
        }

        // --- FASE C: SOLTAR EL OBJETO (Levantar el dedo del clic izquierdo) ---
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT) && draggedItem != null) {

            // Calculamos la verdadera coordenada donde debe ir el ancla
            int targetX = gridX - grabOffsetX;
            int targetY = gridY - grabOffsetY;

            // Intentamos soltarla usando esas coordenadas corregidas
            boolean success = grid.addItem(draggedItem, targetX, targetY);

            if (success) {
                System.out.println("¡Arma movida con éxito!");
            } else {
                // Si choca, la devolvemos a su origen intacta
                grid.addItem(draggedItem, originGridX, originGridY);
                System.out.println("Movimiento inválido. El arma vuelve a su sitio.");
            }
            draggedItem = null;
        }

        // --- FASE D: BORRAR UN OBJETO (Clic Derecho) ---
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            if (gridX >= 0 && gridX < grid.getColumns() && gridY >= 0 && gridY < grid.getRows()) {
                grid.removeItemAt(gridX, gridY);
                System.out.println("¡Limpieza! Borrando casilla en Columna: " + gridX + " | Fila: " + gridY);
            }
        }

        // --- FASE E: DIBUJAR LA PANTALLA ---
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        int cellSize = 50;

        // Pasada de relleno (Objetos anclados)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < grid.getColumns(); x++) {
            for (int y = 0; y < grid.getRows(); y++) {
                InventoryItem item = grid.getItemAt(x, y);
                if (item != null) {
                    shapeRenderer.setColor(Color.ORANGE);
                    shapeRenderer.rect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
        shapeRenderer.end();

        // Pasada de líneas (Cuadrícula)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for (int x = 0; x < grid.getColumns(); x++) {
            for (int y = 0; y < grid.getRows(); y++) {
                shapeRenderer.rect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
        shapeRenderer.end();

        // --- NUEVO PASO: EL EFECTO FANTASMA (DIBUJAR AL AGARRAR) ---
        if (draggedItem != null) {

            // Activamos transparencia para el fantasma
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            // Color fantasma: Naranja con 50% de transparencia
            shapeRenderer.setColor(1, 0.65f, 0, 0.5f);

            boolean[][] shape = draggedItem.getShape();

            // Calculamos en qué casilla exacta anclaría el objeto si lo soltáramos ahora mismo
            int targetX = gridX - grabOffsetX;
            int targetY = gridY - grabOffsetY;

            for (int x = 0; x < draggedItem.getWidth(); x++) {
                for (int y = 0; y < draggedItem.getHeight(); y++) {
                    if (shape[x][y] == true) {

                        // ¡LA MAGIA DEL SNAP!
                        // El fantasma salta de casilla en casilla, perfectamente alineado.
                        float ghostX = (targetX + x) * cellSize;
                        float ghostY = (targetY + y) * cellSize;

                        shapeRenderer.rect(ghostX, ghostY, cellSize, cellSize);
                    }
                }
            }
            shapeRenderer.end();
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
    }
}
