package io.github.jjziorny.ecosystem.entity;

import io.github.jjziorny.ecosystem.world.Position;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Fox {

    private static final float SPEED = 12f;

    private Position position;
    private float directionX;
    private float directionY;

    public Fox(Position position) {
        this.position = position;

        double angle = ThreadLocalRandom.current().nextDouble(
            0,
            Math.PI * 2
        );

        directionX = (float) Math.cos(angle);
        directionY = (float) Math.sin(angle);
    }

    public void update(
        float deltaTime,
        List<Rabbit> rabbits,
        float minX,
        float maxX,
        float minY,
        float maxY
    ) {
        Rabbit closestRabbit = findClosestRabbit(rabbits);

        if (closestRabbit != null) {
            pointToward(closestRabbit.getPosition());
        }

        float nextX =
            position.x() + directionX * SPEED * deltaTime;

        float nextY =
            position.y() + directionY * SPEED * deltaTime;

        if (nextX < minX) {
            nextX = minX;
            directionX = Math.abs(directionX);
        } else if (nextX > maxX) {
            nextX = maxX;
            directionX = -Math.abs(directionX);
        }

        if (nextY < minY) {
            nextY = minY;
            directionY = Math.abs(directionY);
        } else if (nextY > maxY) {
            nextY = maxY;
            directionY = -Math.abs(directionY);
        }

        position = new Position(nextX, nextY);
    }

    private Rabbit findClosestRabbit(List<Rabbit> rabbits) {
        Rabbit closestRabbit = null;
        float smallestDistance = Float.MAX_VALUE;

        for (Rabbit rabbit : rabbits) {
            float distance =
                position.distanceTo(rabbit.getPosition());

            if (distance < smallestDistance) {
                smallestDistance = distance;
                closestRabbit = rabbit;
            }
        }

        return closestRabbit;
    }

    private void pointToward(Position target) {
        float deltaX = target.x() - position.x();
        float deltaY = target.y() - position.y();
        float distance = position.distanceTo(target);

        if (distance <= 0.0001f) {
            return;
        }

        directionX = deltaX / distance;
        directionY = deltaY / distance;
    }

    public Position getPosition() {
        return position;
    }
}
