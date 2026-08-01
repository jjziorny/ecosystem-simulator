package io.github.jjziorny.ecosystem.entity;

import io.github.jjziorny.ecosystem.world.Position;

public class Rabbit {

    private Position position;

    public Rabbit(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
