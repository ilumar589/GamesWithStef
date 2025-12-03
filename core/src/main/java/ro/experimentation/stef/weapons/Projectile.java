package ro.experimentation.stef.weapons;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Abstract base class for all projectiles in the game.
 * Provides common functionality for movement, rendering, and collision detection.
 */
public abstract class Projectile {
    protected Sprite sprite;
    protected Vector2 velocity;
    protected boolean active;
    
    /**
     * Creates a new projectile (for pooling).
     */
    public Projectile() {
        this.velocity = null;
        this.active = false;
    }
    
    /**
     * Initializes the projectile with a sprite and velocity (for pooling).
     *
     * @param sprite The sprite to use for rendering
     * @param velocity The velocity vector from the pool
     */
    public void init(Sprite sprite, Vector2 velocity) {
        this.sprite = sprite;
        this.velocity = velocity;
        this.active = true;
    }
    
    /**
     * Updates the projectile's state.
     *
     * @param delta Time elapsed since last frame in seconds
     */
    public void update(float delta) {
        if (active && velocity != null) {
            sprite.translate(velocity.x * delta, velocity.y * delta);
        }
    }
    
    /**
     * Renders the projectile.
     *
     * @param batch The SpriteBatch to render with
     */
    public void render(SpriteBatch batch) {
        if (active && sprite != null) {
            sprite.draw(batch);
        }
    }
    
    /**
     * Gets the bounding rectangle for collision detection.
     *
     * @return The bounding rectangle
     */
    public Rectangle getBoundingRectangle() {
        if (sprite == null) {
            return new Rectangle();
        }
        return sprite.getBoundingRectangle();
    }
    
    /**
     * Checks if the projectile is outside the screen bounds.
     *
     * @param screenWidth Screen width
     * @param screenHeight Screen height
     * @return true if projectile is off-screen
     */
    public boolean isOffScreen(int screenWidth, int screenHeight) {
        if (sprite == null) {
            return true;
        }
        float x = sprite.getX();
        float y = sprite.getY();
        return x > screenWidth || x < -100 || y > screenHeight || y < -100;
    }
    
    /**
     * Deactivates the projectile (marks it for removal).
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Checks if the projectile is active.
     *
     * @return true if active
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Gets the sprite.
     *
     * @return The projectile sprite
     */
    public Sprite getSprite() {
        return sprite;
    }
    
    /**
     * Gets the velocity.
     *
     * @return The velocity vector
     */
    public Vector2 getVelocity() {
        return velocity;
    }
    
    /**
     * Resets the projectile for object pooling.
     */
    public void reset() {
        this.sprite = null;
        this.velocity = null;
        this.active = false;
    }
}
