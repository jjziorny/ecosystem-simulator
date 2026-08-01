package io.github.jjziorny.ecosystem.entity;
import io.github.jjziorny.ecosystem.world.Position;
import java.util.concurrent.ThreadLocalRandom;

public class Rabbit {

    private static final float SPEED = 10f;

    private Position position;
    private float directionX;
    private float directionY;

    public Rabbit(Position position) {
        this.position = position;
        chooseRandomDirection();
    }

    public void update(
        float deltaTime,
        float minX,
        float maxX,
        float minY,
        float maxY
    ) {
        float nextX = position.x() + directionX * SPEED * deltaTime;
        float nextY = position.y() + directionY * SPEED * deltaTime;

        if (nextX < minX || nextX > maxX) {
            directionX *= -1f;
            nextX = clamp(nextX, minX, maxX);
        }

        if (nextY < minY || nextY > maxY) {
            directionY *= -1f;
            nextY = clamp(nextY, minY, maxY);
        }

        position = new Position(nextX, nextY);
    }

    private void chooseRandomDirection() {
        double angle = ThreadLocalRandom.current()
            .nextDouble(0, Math.PI * 2);

        directionX = (float) Math.cos(angle);
        directionY = (float) Math.sin(angle);
    }

    private float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(value, maximum));
    }

    public Position getPosition() {
        return position;
    }
}
