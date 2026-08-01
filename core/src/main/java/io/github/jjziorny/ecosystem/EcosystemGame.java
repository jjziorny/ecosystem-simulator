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

public class EcosystemGame extends ApplicationAdapter {

    private static final float WORLD_WIDTH = 80f;
    private static final float WORLD_HEIGHT = 60f;
    private static final float RABBIT_RADIUS = 1.5f;
    private static final int RABBIT_SEGMENTS = 32;
    private static final int INITIAL_RABBIT_COUNT = 10;
    private static final int INITIAL_GRASS_COUNT = 100;
    private static final float GRASS_RADIUS = 0.35f;
    private static final int GRASS_SEGMENTS = 12;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private List<Rabbit> rabbits;
    private List<Grass> grassPatches;

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
    }


    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        for (Rabbit rabbit : rabbits) {
            Grass eatenGrass = (Grass) rabbit.update(
                deltaTime,
                grassPatches,
                RABBIT_RADIUS + GRASS_RADIUS,
                RABBIT_RADIUS,
                WORLD_WIDTH - RABBIT_RADIUS,
                RABBIT_RADIUS,
                WORLD_HEIGHT - RABBIT_RADIUS
            );

            if (eatenGrass != null) {
                grassPatches.remove(eatenGrass);
            }
        }

        rabbits.removeIf(rabbit -> !rabbit.isAlive());

        ScreenUtils.clear(0.08f, 0.16f, 0.10f, 1f);

        viewport.apply();
        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

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
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int index = 0; index < INITIAL_GRASS_COUNT; index++) {
            float x = (float) random.nextDouble(
                GRASS_RADIUS,
                WORLD_WIDTH - GRASS_RADIUS
            );

            float y = (float) random.nextDouble(
                GRASS_RADIUS,
                WORLD_HEIGHT - GRASS_RADIUS
            );

            grassPatches.add(new Grass(new Position(x, y)));
        }
    }
}

