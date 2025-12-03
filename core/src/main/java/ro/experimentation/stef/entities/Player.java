package ro.experimentation.stef.entities;

import com.badlogic.gdx.graphics.Texture;
import ro.experimentation.stef.config.GameConfig;

/**
 * Represents the player character with special abilities and higher health.
 */
public class Player extends Character {
    private float abilityCooldownA;
    private float abilityCooldownS;
    private float abilityCooldownD;
    
    /**
     * Creates a new player with the specified texture.
     *
     * @param texture The texture to use for the player
     */
    public Player(Texture texture) {
        super(texture, GameConfig.PLAYER_MAX_HEALTH);
        this.abilityCooldownA = 0f;
        this.abilityCooldownS = 0f;
        this.abilityCooldownD = 0f;
    }
    
    @Override
    public void update(float delta) {
        // Update ability cooldowns
        if (abilityCooldownA > 0) {
            abilityCooldownA -= delta;
        }
        if (abilityCooldownS > 0) {
            abilityCooldownS -= delta;
        }
        if (abilityCooldownD > 0) {
            abilityCooldownD -= delta;
        }
    }
    
    /**
     * Moves the player by the specified amounts.
     *
     * @param dx Delta x movement
     * @param dy Delta y movement
     */
    public void move(float dx, float dy) {
        sprite.translate(dx, dy);
    }
    
    /**
     * Checks if ability A is ready.
     *
     * @return true if ability is off cooldown
     */
    public boolean isAbilityAReady() {
        return abilityCooldownA <= 0;
    }
    
    /**
     * Checks if ability S is ready.
     *
     * @return true if ability is off cooldown
     */
    public boolean isAbilitySReady() {
        return abilityCooldownS <= 0;
    }
    
    /**
     * Checks if ability D is ready.
     *
     * @return true if ability is off cooldown
     */
    public boolean isAbilityDReady() {
        return abilityCooldownD <= 0;
    }
    
    /**
     * Uses ability A and sets it on cooldown.
     */
    public void useAbilityA() {
        abilityCooldownA = GameConfig.ABILITY_COOLDOWN_TIME;
    }
    
    /**
     * Uses ability S and sets it on cooldown.
     */
    public void useAbilityS() {
        abilityCooldownS = GameConfig.ABILITY_COOLDOWN_TIME;
    }
    
    /**
     * Uses ability D and sets it on cooldown.
     */
    public void useAbilityD() {
        abilityCooldownD = GameConfig.ABILITY_COOLDOWN_TIME;
    }
}
