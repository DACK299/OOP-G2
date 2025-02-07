package io.github.some_example_name.lwjgl3;

public interface CollisionListener {
    void onCollisionEnter(Object objA, Object objB);
    void onCollisionStay(Object objA, Object objB);
    void onCollisionExit(Object objA, Object objB);
}
