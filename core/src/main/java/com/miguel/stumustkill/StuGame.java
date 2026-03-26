package com.miguel.stumustkill;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture; // Importamos la Textura
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion; // Importamos la Region
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

    // --- NUEVAS VARIABLES: CARGAR IMÁGENES ---
    Texture medkitTex;
    Texture armorTex;
    Texture pistolTex;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // 1. Cargamos las imágenes reales
        medkitTex = new Texture(Gdx.files.internal("medkit.png"));
        armorTex = new Texture(Gdx.files.internal("armor.png"));
        pistolTex = new Texture(Gdx.files.internal("pistol.png"));

        grid = new InventoryGrid(5, 4);

        // 2. Creamos los objetos manualmente, dándoles la imagen
        // Botiquín 1x1
        boolean[][] medkitShape = {{true}};
        InventoryItem medkit = new InventoryItem("Medkit", medkitShape, new TextureRegion(medkitTex));
        grid.addItem(medkit, 0, 0);

        // Armadura 2x2
        boolean[][] armorShape = {{true, true}, {true, true}};
        InventoryItem armor = new InventoryItem("Armor", armorShape, new TextureRegion(armorTex));
        grid.addItem(armor, 2, 1);
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

        // 1. Pasada de líneas (Cuadrícula de fondo)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for (int x = 0; x < grid.getColumns(); x++) {
            for (int y = 0; y < grid.getRows(); y++) {
                shapeRenderer.rect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
        shapeRenderer.end();

        // 2. Pasada de IMÁGENES (Texturas)
        batch.begin(); // ¡Despertamos al SpriteBatch!

        // Creamos una pequeña memoria para no dibujar el mismo arma gigante varias veces
        java.util.ArrayList<InventoryItem> drawnItems = new java.util.ArrayList<>();

        for (int x = 0; x < grid.getColumns(); x++) {
            for (int y = 0; y < grid.getRows(); y++) {

                InventoryItem item = grid.getItemAt(x, y);

                // Si hay un objeto Y no lo hemos dibujado todavía...
                if (item != null && !drawnItems.contains(item)) {

                    // Calculamos el tamaño real en píxeles (ej: Armadura = 2 de ancho * 50 = 100px)
                    float drawWidth = item.getWidth() * cellSize;
                    float drawHeight = item.getHeight() * cellSize;

                    // Dibujamos la imagen exacta del objeto
                    batch.draw(item.getTextureRegion(), x * cellSize, y * cellSize, drawWidth, drawHeight);

                    // Lo apuntamos en la lista de "ya dibujados"
                    drawnItems.add(item);
                }
            }
        }
        batch.end();

        // --- NUEVO PASO: EL EFECTO FANTASMA (IMAGEN TRANSPARENTE CON SNAP) ---
        // Si tenemos un objeto en la mano...
        if (draggedItem != null) {

            // 1. Calculamos en qué casilla exacta anclaría visualmente (Snap to Grid)
            int targetX = gridX - grabOffsetX;
            int targetY = gridY - grabOffsetY;

            // 2. Despierta al SpriteBatch (Usamos el batch, ya no el shapeRenderer)
            batch.begin();

            // 3. APLICAMOS TRANSPARENCIA AL BATCH (Aquí está la magia)
            // Color blanco puro con 50% de Alpha hace que la textura parezca "fantasma".
            batch.setColor(1, 1, 1, 0.5f);

            // 4. DIBUJAMOS LA IMAGEN ENTERA (No bloque a bloque)
            float drawWidth = draggedItem.getWidth() * cellSize;
            float drawHeight = draggedItem.getHeight() * cellSize;

            // Dibujamos la textura región del objeto anclada visualmente
            batch.draw(draggedItem.getTextureRegion(), targetX * cellSize, targetY * cellSize, drawWidth, drawHeight);

            // 5. RESETEAMOS EL COLOR DEL BATCH (¡Crucial para no pintar todo transparente el próximo frame!)
            batch.setColor(Color.WHITE);

            batch.end();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
    }
}
