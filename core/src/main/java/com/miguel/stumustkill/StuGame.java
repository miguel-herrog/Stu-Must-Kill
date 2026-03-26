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
    SpriteBatch batch;       // Para dibujar imágenes (Texturas)
    ShapeRenderer shapeRenderer; // Para dibujar formas geométricas simples (Cuadrados)
    InventoryGrid grid;

    InventoryItem draggedItem = null; // El objeto que tenemos agarrado flotando en el aire (null = manos vacías)
    int originGridX = -1;             // La columna de donde lo cogimos
    int originGridY = -1;
    int grabOffsetX = 0; // Distancia en X desde el clic hasta el ancla
    int grabOffsetY = 0;

    @Override
    public void create() {
        // Inicializamos las herramientas
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
            // Añadimos comprobación para que no dé error si haces clic fuera de la pantalla por la izquierda/abajo
            if (draggedItem == null && gridX >= 0 && gridX < grid.getColumns() && gridY >= 0 && gridY < grid.getRows()) {

                InventoryItem itemToPick = grid.getItemAt(gridX, gridY);

                if (itemToPick != null) {
                    draggedItem = itemToPick;

                    // ¡LA MAGIA DEL ANCLA!
                    // Asumimos que el clic es el origen, pero escaneamos por si hay partes más a la izquierda o más abajo.
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

                    // Restamos la posición de tu ratón a la posición real del ancla.
                    grabOffsetX = gridX - originGridX;
                    grabOffsetY = gridY - originGridY;

                    grid.removeItemAt(gridX, gridY);
                }
            }
        }

        // --- FASE B: SOLTAR EL OBJETO (Levantar el dedo del clic izquierdo) ---
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

        // --- FASE C: BORRAR UN OBJETO (Clic Derecho) ---
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            // ¡Magia! Como gridX y gridY ya están calculados arriba, solo tenemos que usarlos.
            grid.removeItemAt(gridX, gridY);
            System.out.println("¡Limpieza! Borrando casilla en Columna: " + gridX + " | Fila: " + gridY);
        }

        // --- FASE D: DIBUJAR LA PANTALLA ---
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        int cellSize = 50;

        // Pasada de relleno (Objetos)
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
    }

    @Override
    public void dispose() {
        // Limpiamos la memoria al cerrar el juego
        batch.dispose();
        shapeRenderer.dispose();
    }
}
