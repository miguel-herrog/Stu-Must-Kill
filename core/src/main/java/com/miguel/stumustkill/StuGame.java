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
        // Si el jugador acaba de hacer clic izquierdo...
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.input.getY();

            // 1. INVERTIMOS LA 'Y' DEL RATÓN
            // Le restamos a la altura total de la ventana (Gdx.graphics.getHeight()) la Y del ratón.
            int invertedY = Gdx.graphics.getHeight() - mouseY;

            // 2. CONVERTIMOS PÍXELES A CASILLAS DE LA MATRIZ
            // Dividimos el píxel entre el tamaño de la casilla (50) para saber en qué columna y fila estamos.
            int gridX = mouseX / 50;
            int gridY = invertedY / 50;

            System.out.println("Clic traducido -> Intentando meter una pistola en Columna: " + gridX + " | Fila: " + gridY);

            // 3. ¡LA PRUEBA DE FUEGO!
            // Intentamos meter una pistola nueva exactamente en la casilla donde has hecho clic.
            InventoryItem newPistol = ItemFactory.createPistol();
            boolean success = grid.addItem(newPistol, gridX, gridY);

            if (success) {
                System.out.println("¡Pistola colocada con éxito!");
            } else {
                System.out.println("Error: La pistola no cabe ahí o choca con algo.");
            }
        }
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        int cellSize = 50;

        // --- 1. PASADA DE RELLENO (Pintar los objetos) ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int x = 0; x < grid.getColumns(); x++) {
            for (int y = 0; y < grid.getRows(); y++) {

                // Preguntamos al inventario: ¿hay algún objeto aquí?
                InventoryItem item = grid.getItemAt(x, y);

                if (item != null) {
                    // Si hay objeto, pintamos esa casilla de color naranja
                    shapeRenderer.setColor(Color.ORANGE);
                    shapeRenderer.rect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
        shapeRenderer.end();

        // --- 2. PASADA DE LÍNEAS (Dibujar la cuadrícula blanca) ---
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
