package net.botwithus.Combat;


import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.scene.entities.characters.PathingEntity;

public enum Direction {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;

    public static Direction of(int angle) {
        if (angle < 45) {
            return NORTH;
        } else if (angle < 90) {
            return NORTH_EAST;
        } else if (angle < 135) {
            return EAST;
        } else if (angle < 180) {
            return SOUTH_EAST;
        } else if (angle < 225) {
            return SOUTH;
        } else if (angle < 270) {
            return SOUTH_WEST;
        } else if (angle < 315) {
            return WEST;
        } else {
            return NORTH_WEST;
        }
    }

    public static Direction of(Coordinate src, Coordinate dst) {
        int angle = 90 - ((int) Math.toDegrees(Math.atan2(dst.getY() - src.getY(), dst.getX() - src.getX())));
        if (angle < 0) {
            angle += 360;
        }
        return of(angle % 360);
    }

    public static Direction of(PathingEntity<?> entity) {
        return of(asAngle(entity));
    }

    public static int asAngle(PathingEntity<?> entity) {
        double degrees = 90.0 + (Math.atan2(entity.getDirection1(),
                entity.getDirection2()) * (180.0 / Math.PI));
        return (int) (Math.round(degrees) + 360) % 360;
    }
}
