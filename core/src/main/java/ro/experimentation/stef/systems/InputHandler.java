package ro.experimentation.stef.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import ro.experimentation.stef.config.GameConfig;
import ro.experimentation.stef.entities.Player;

/**
 * Handles all player input processing.
 * Separates input handling from game logic.
 */
public class InputHandler {
    private float laserCooldown;
    
    /**
     * Creates a new InputHandler.
     */
    public InputHandler() {
        this.laserCooldown = 0f;
    }
    
    /**
     * Updates the input handler state.
     *
     * @param delta Time elapsed since last frame
     */
    public void update(float delta) {
        if (laserCooldown > 0) {
            laserCooldown -= delta;
        }
    }
    
    /**
     * Handles player movement input.
     *
     * @param player The player to move
     * @param delta Time elapsed since last frame
     */
    public void handleMovement(Player player, float delta) {
        float speed = GameConfig.PLAYER_SPEED;
        
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.move(speed * delta, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.move(-speed * delta, 0);
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.move(0, speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.move(0, -speed * delta);
        }
    }
    
    /**
     * Checks if the shoot key is pressed and cooldown allows shooting.
     *
     * @return true if player should shoot
     */
    public boolean shouldShoot() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && laserCooldown <= 0) {
            laserCooldown = GameConfig.LASER_COOLDOWN_TIME;
            return true;
        }
        return false;
    }
    
    /**
     * Checks if ability A should be triggered.
     *
     * @param player The player
     * @return true if ability should trigger
     */
    public boolean shouldTriggerAbilityA(Player player) {
        return Gdx.input.isKeyJustPressed(Input.Keys.A) && player.isAbilityAReady();
    }
    
    /**
     * Checks if ability S should be triggered.
     *
     * @param player The player
     * @return true if ability should trigger
     */
    public boolean shouldTriggerAbilityS(Player player) {
        return Gdx.input.isKeyJustPressed(Input.Keys.S) && player.isAbilitySReady();
    }
    
    /**
     * Checks if ability D should be triggered.
     *
     * @param player The player
     * @return true if ability should trigger
     */
    public boolean shouldTriggerAbilityD(Player player) {
        return Gdx.input.isKeyJustPressed(Input.Keys.D) && player.isAbilityDReady();
    }
    
    /**
     * Checks if the pause key was pressed.
     *
     * @return true if pause should toggle
     */
    public boolean shouldTogglePause() {
        return Gdx.input.isKeyJustPressed(Input.Keys.P);
    }
    
    /**
     * Checks if the exit key was pressed.
     *
     * @return true if game should exit
     */
    public boolean shouldExit() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
    }
}
