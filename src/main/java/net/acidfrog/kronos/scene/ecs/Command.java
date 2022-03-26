package net.acidfrog.kronos.scene.ecs;

@FunctionalInterface
public interface Command {

    void execute();
    
}
