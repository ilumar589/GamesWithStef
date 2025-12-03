package ro.experimentation.stef.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import ro.experimentation.stef.config.GameConfig;

/**
 * Base class for all game characters/entities.
 * Encapsulates common properties like sprite, health, position, and alive status.
 */
public abstract class Character {
    protected Sprite sprite;
    protected float health;
    protected float maxHealth;
    protected boolean alive;
    
    /**
     * Creates a new character with the specified texture and max health.
     *
     * @param texture The texture to use for this character
     * @param maxHealth The maximum health for this character
     */
    public Character(Texture texture, float maxHealth) {
        this.sprite = new Sprite(texture);
        this.sprite.setScale(GameConfig.SPRITE_SCALE);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.alive = true;
    }
    
    /**
     * Updates the character's state. To be implemented by subclasses.
     *
     * @param delta Time elapsed since last frame in seconds
     */
    public abstract void update(float delta);
    
    /**
     * Renders the character.
     *
     * @param batch The SpriteBatch to render with
     */
    public void render(SpriteBatch batch) {
        if (alive) {
            sprite.draw(batch);
        }
    }
    
    /**
     * Applies damage to the character.
     *
     * @param damage Amount of damage to apply
     */
    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }
    
    /**
     * Gets the bounding rectangle for collision detection.
     *
     * @return The bounding rectangle
     */
    public Rectangle getBoundingRectangle() {
        return sprite.getBoundingRectangle();
    }
    
    /**
     * Sets the character's position.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void setPosition(float x, float y) {
        sprite.setPosition(x, y);
    }
    
    /**
     * Translates the character's position.
     *
     * @param x The x amount to translate
     * @param y The y amount to translate
     */
    public void translate(float x, float y) {
        sprite.translate(x, y);
    }
    
    // Getters and setters
    public Sprite getSprite() {
        return sprite;
    }
    
    public float getHealth() {
        return health;
    }
    
    public float getMaxHealth() {
        return maxHealth;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public float getX() {
        return sprite.getX();
    }
    
    public float getY() {
        return sprite.getY();
    }
}
