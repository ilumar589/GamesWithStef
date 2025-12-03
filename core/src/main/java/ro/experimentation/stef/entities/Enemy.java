package ro.experimentation.stef.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import ro.experimentation.stef.config.GameConfig;

/**
 * Represents an enemy character with AI behavior.
 */
public class Enemy extends Character {
    private Vector2 velocity;
    private float moveTimer;
    private float shootTimer;
    
    /**
     * Creates a new enemy with the specified texture.
     *
     * @param texture The texture to use for the enemy
     */
    public Enemy(Texture texture) {
        super(texture, GameConfig.MAX_HEALTH);
        this.velocity = new Vector2();
    }
    
    @Override
    public void update(float delta) {
        // Movement and shooting logic is handled by EnemyAI system
        // This method is here for consistency with the Character interface
    }
    
    /**
     * Gets the enemy's velocity vector.
     *
     * @return The velocity vector
     */
    public Vector2 getVelocity() {
        return velocity;
    }
    
    /**
     * Sets the enemy's velocity.
     *
     * @param velocity The new velocity vector
     */
    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }
    
    /**
     * Gets the move timer for AI behavior.
     *
     * @return The move timer value
     */
    public float getMoveTimer() {
        return moveTimer;
    }
    
    /**
     * Sets the move timer for AI behavior.
     *
     * @param moveTimer The new move timer value
     */
    public void setMoveTimer(float moveTimer) {
        this.moveTimer = moveTimer;
    }
    
    /**
     * Updates the move timer.
     *
     * @param delta Time to subtract from the timer
     */
    public void updateMoveTimer(float delta) {
        this.moveTimer -= delta;
    }
    
    /**
     * Gets the shoot timer for AI behavior.
     *
     * @return The shoot timer value
     */
    public float getShootTimer() {
        return shootTimer;
    }
    
    /**
     * Sets the shoot timer for AI behavior.
     *
     * @param shootTimer The new shoot timer value
     */
    public void setShootTimer(float shootTimer) {
        this.shootTimer = shootTimer;
    }
    
    /**
     * Updates the shoot timer.
     *
     * @param delta Time to subtract from the timer
     */
    public void updateShootTimer(float delta) {
        this.shootTimer -= delta;
    }
}
