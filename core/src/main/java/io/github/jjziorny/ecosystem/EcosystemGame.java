package io.github.jjziorny.ecosystem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EcosystemGame extends ApplicationAdapter {

    private static final float WORLD_WIDTH = 80f;
    private static final float WORLD_HEIGHT = 60f;
    private static final float RABBIT_RADIUS = 1.5f;
    private static final int RABBIT_SEGMENTS = 32;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;

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
    }
    @Override
    public void render(){
        ScreenUtils.clear(0.08f, 0.16f, 0.10f, 1f);
        viewport.apply();
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(WORLD_WIDTH / 2f,
            WORLD_HEIGHT / 2f,
            RABBIT_RADIUS,
            RABBIT_SEGMENTS
        );
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
}
