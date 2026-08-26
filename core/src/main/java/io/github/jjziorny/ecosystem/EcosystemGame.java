package io.github.jjziorny.ecosystem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.jjziorny.ecosystem.entity.Rabbit;
import io.github.jjziorny.ecosystem.world.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import io.github.jjziorny.ecosystem.entity.Grass;
import io.github.jjziorny.ecosystem.entity.Fox;
import io.github.jjziorny.ecosystem.world.WaterSource;

public class EcosystemGame extends ApplicationAdapter {

    private static final float WORLD_WIDTH = 80f;
    private static final float WORLD_HEIGHT = 60f;
    private static final float RABBIT_RADIUS = 1.5f;
    private static final int RABBIT_SEGMENTS = 32;
    private static final int INITIAL_RABBIT_COUNT = 10;
    private static final int INITIAL_GRASS_COUNT = 100;
    private static final float GRASS_RADIUS = 0.35f;
    private static final int GRASS_SEGMENTS = 12;
    private static final int MAX_GRASS_COUNT = 100;
    private static final float GRASS_GROWTH_INTERVAL = 0.3f;
    private static final int MAX_RABBIT_COUNT = 50;
    private static final float TITLE_UPDATE_INTERVAL = 0.25f;
    private static final int INITIAL_FOX_COUNT = 3;
    private static final float FOX_RADIUS = 1.8f;
    private static final int FOX_SEGMENTS = 32;
    private static final int MAX_FOX_COUNT = 15;
    private static final int INITIAL_WATER_SOURCE_COUNT = 5;
    private static final float WATER_SOURCE_RADIUS = 3f;
    private static final int WATER_SOURCE_SEGMENTS = 32;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private List<Rabbit> rabbits;
    private List<Grass> grassPatches;
    private List<Fox> foxes;
    private List<WaterSource> waterSources;
    private float grassGrowthTimer;
    private float titleUpdateTimer;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.update(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            true
        );
        shapeRenderer = new ShapeRenderer();
        rabbits = new ArrayList<>();
        createInitialRabbits();
        grassPatches = new ArrayList<>();
        createInitialGrass();
        foxes = new ArrayList<>();
        createInitialFoxes();
        waterSources = new ArrayList<>();
        createInitialWaterSources();
    }



    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        List<Rabbit> newbornRabbits = new ArrayList<>();

        // Atualiza os coelhos
        for (Rabbit rabbit : rabbits) {
            Grass eatenGrass = (Grass) rabbit.update(
                deltaTime,
                grassPatches,
                foxes,
                RABBIT_RADIUS + GRASS_RADIUS,
                RABBIT_RADIUS,
                WORLD_WIDTH - RABBIT_RADIUS,
                RABBIT_RADIUS,
                WORLD_HEIGHT - RABBIT_RADIUS
            );

            if (eatenGrass != null) {
                grassPatches.remove(eatenGrass);
            }

            if (
                rabbits.size() + newbornRabbits.size() < MAX_RABBIT_COUNT
                    && rabbit.canReproduce()
            ) {
                Rabbit newborn = rabbit.reproduce();

                if (newborn != null) {
                    newbornRabbits.add(newborn);
                }
            }
        }

        rabbits.addAll(newbornRabbits);
        rabbits.removeIf(rabbit -> !rabbit.isAlive());

        List<Fox> newbornFoxes = new ArrayList<>();

        for (Fox fox : foxes) {
            Rabbit capturedRabbit = fox.update(
                deltaTime,
                rabbits,
                FOX_RADIUS + RABBIT_RADIUS,
                FOX_RADIUS,
                WORLD_WIDTH - FOX_RADIUS,
                FOX_RADIUS,
                WORLD_HEIGHT - FOX_RADIUS
            );

            if (capturedRabbit != null) {
                rabbits.remove(capturedRabbit);
            }

            if (
                foxes.size() + newbornFoxes.size() < MAX_FOX_COUNT
                    && fox.canReproduce()
            ) {
                Fox newborn = fox.reproduce();

                if (newborn != null) {
                    newbornFoxes.add(newborn);
                }
            }
        }

        foxes.addAll(newbornFoxes);
        foxes.removeIf(fox -> !fox.isAlive());

        growGrass(deltaTime);
        updateWindowTitle(deltaTime);

        // Limpa e prepara a tela
        ScreenUtils.clear(0.08f, 0.16f, 0.10f, 1f);

        viewport.apply();
        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//Desenha a agua
        for (WaterSource waterSource : waterSources) {
            Position waterPosition = waterSource.getPosition();

            shapeRenderer.circle(
                waterPosition.x(),
                waterPosition.y(),
                WATER_SOURCE_RADIUS,
                WATER_SOURCE_SEGMENTS
            );
        }
        // Desenha a grama
        shapeRenderer.setColor(Color.GREEN);

        for (Grass grass : grassPatches) {
            Position grassPosition = grass.getPosition();

            shapeRenderer.circle(
                grassPosition.x(),
                grassPosition.y(),
                GRASS_RADIUS,
                GRASS_SEGMENTS
            );
        }

        // Desenha os coelhos
        shapeRenderer.setColor(Color.WHITE);

        for (Rabbit rabbit : rabbits) {
            Position rabbitPosition = rabbit.getPosition();

            shapeRenderer.circle(
                rabbitPosition.x(),
                rabbitPosition.y(),
                RABBIT_RADIUS,
                RABBIT_SEGMENTS
            );
        }
        shapeRenderer.setColor(Color.ORANGE);
// Desenha raposas
        for (Fox fox : foxes) {
            Position foxPosition = fox.getPosition();

            shapeRenderer.circle(
                foxPosition.x(),
                foxPosition.y(),
                FOX_RADIUS,
                FOX_SEGMENTS
            );
        }

        shapeRenderer.setColor(Color.BLUE);

        shapeRenderer.end();

    }
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    private void createInitialRabbits() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int index = 0; index < INITIAL_RABBIT_COUNT; index++) {
            float x = (float) random.nextDouble(
                RABBIT_RADIUS,
                WORLD_WIDTH - RABBIT_RADIUS
            );

            float y = (float) random.nextDouble(
                RABBIT_RADIUS,
                WORLD_HEIGHT - RABBIT_RADIUS
            );

            rabbits.add(new Rabbit(new Position(x, y)));
        }
    }
    private void createInitialGrass() {
        for (int index = 0; index < INITIAL_GRASS_COUNT; index++) {
            grassPatches.add(createGrass());
        }
    }
    private Grass createGrass() {
        float x = GRASS_RADIUS
            + ThreadLocalRandom.current().nextFloat()
            * (WORLD_WIDTH - 2f * GRASS_RADIUS);

        float y = GRASS_RADIUS
            + ThreadLocalRandom.current().nextFloat()
            * (WORLD_HEIGHT - 2f * GRASS_RADIUS);

        return new Grass(new Position(x, y));
    }
    private void growGrass(float deltaTime) {
        if (grassPatches.size() >= MAX_GRASS_COUNT) {
            grassGrowthTimer = 0f;
            return;
        }

        grassGrowthTimer += deltaTime;

        while (
            grassGrowthTimer >= GRASS_GROWTH_INTERVAL
                && grassPatches.size() < MAX_GRASS_COUNT
        ) {
            grassPatches.add(createGrass());
            grassGrowthTimer -= GRASS_GROWTH_INTERVAL;
        }
    }
    private void updateWindowTitle(float deltaTime) {
        titleUpdateTimer += deltaTime;

        if (titleUpdateTimer < TITLE_UPDATE_INTERVAL) {
            return;
        }

        Gdx.graphics.setTitle(
            "Ecosystem Simulator"
                + " | Coelhos: " + rabbits.size()
                + " | Grama: " + grassPatches.size()
                + " | Raposas: " + foxes.size()
        );

        titleUpdateTimer = 0f;
    }
    private void createInitialFoxes() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int index = 0; index < INITIAL_FOX_COUNT; index++) {
            float x = (float) random.nextDouble(
                FOX_RADIUS,
                WORLD_WIDTH - FOX_RADIUS
            );

            float y = (float) random.nextDouble(
                FOX_RADIUS,
                WORLD_HEIGHT - FOX_RADIUS
            );

            foxes.add(new Fox(new Position(x, y)));
        }
    }
    private void createInitialWaterSources() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (
            int index = 0;
            index < INITIAL_WATER_SOURCE_COUNT;
            index++
        ) {
            float x = (float) random.nextDouble(
                WATER_SOURCE_RADIUS,
                WORLD_WIDTH - WATER_SOURCE_RADIUS
            );

            float y = (float) random.nextDouble(
                WATER_SOURCE_RADIUS,
                WORLD_HEIGHT - WATER_SOURCE_RADIUS
            );

            waterSources.add(
                new WaterSource(new Position(x, y))
            );
        }
    }
}

