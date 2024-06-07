package net.botwithus.Archaeology;

import net.botwithus.rs3.game.scene.entities.object.SceneObject;

import java.util.Objects;

public class CoordinateSceneObject {
    private final SceneObject sceneObject;

    public CoordinateSceneObject(SceneObject sceneObject) {
        this.sceneObject = sceneObject;
    }

    public SceneObject getSceneObject() {
        return sceneObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordinateSceneObject that = (CoordinateSceneObject) o;
        return Objects.equals(getSceneObject().getCoordinate(), that.getSceneObject().getCoordinate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSceneObject().getCoordinate());
    }
}