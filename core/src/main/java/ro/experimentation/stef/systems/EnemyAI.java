package ro.experimentation.stef.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ro.experimentation.stef.config.GameConfig;
import ro.experimentation.stef.entities.Enemy;
import ro.experimentation.stef.entities.Player;
import ro.experimentation.stef.weapons.Projectile;
import ro.experimentation.stef.weapons.ProjectileFactory;

/**
 * Manages enemy AI behavior including movement and shooting.
 */
public class EnemyAI {
    private final ProjectileFactory projectileFactory;
    
    /**
     * Creates a new EnemyAI system.
     *
     * @param projectileFactory Factory for creating projectiles
     */
    public EnemyAI(ProjectileFactory projectileFactory) {
        this.projectileFactory = projectileFactory;
    }
    
    /**
     * Initializes an enemy with random movement and shooting timers.
     *
     * @param enemy The enemy to initialize
     */
    public void initializeEnemy(Enemy enemy) {
        setRandomDirection(enemy.getVelocity());
        enemy.setMoveTimer(MathUtils.random(GameConfig.MIN_TIMER_DURATION, GameConfig.MAX_TIMER_DURATION));
        enemy.setShootTimer(MathUtils.random(GameConfig.MIN_TIMER_DURATION, GameConfig.MAX_TIMER_DURATION));
    }
    
    /**
     * Updates enemy movement.
     *
     * @param enemy The enemy to update
     * @param delta Time elapsed since last frame
     */
    public void updateMovement(Enemy enemy, float delta) {
        if (!enemy.isAlive()) {
            return;
        }
        
        // Update move timer
        enemy.updateMoveTimer(delta);
        if (enemy.getMoveTimer() <= 0) {
            setRandomDirection(enemy.getVelocity());
            enemy.setMoveTimer(MathUtils.random(GameConfig.MIN_TIMER_DURATION, GameConfig.MAX_TIMER_DURATION));
        }
        
        // Move enemy
        Vector2 velocity = enemy.getVelocity();
        enemy.translate(velocity.x * delta, velocity.y * delta);
        
        // Keep enemy within screen bounds
        keepWithinBounds(enemy);
    }
    
    /**
     * Updates enemy shooting behavior.
     *
     * @param enemy The enemy to update
     * @param enemyIndex Index of the enemy (determines attack type)
     * @param player The player target
     * @param projectiles Array to add projectiles to
     * @param delta Time elapsed since last frame
     */
    public void updateShooting(Enemy enemy, int enemyIndex, Player player, Array<Projectile> projectiles, float delta) {
        if (!enemy.isAlive() || !player.isAlive()) {
            return;
        }
        
        enemy.updateShootTimer(delta);
        if (enemy.getShootTimer() <= 0) {
            // Different enemies have different attack patterns
            switch (enemyIndex) {
                case 0: // First enemy uses AOE spread
                    projectileFactory.createAOESpread(enemy, player, projectiles);
                    break;
                case 1: // Second enemy uses beam attack
                    projectiles.add(projectileFactory.createBeamAttack(enemy, player));
                    break;
                case 2: // Third enemy uses normal laser
                    projectiles.add(projectileFactory.createEnemyLaser(enemy, player));
                    break;
            }
            enemy.setShootTimer(MathUtils.random(GameConfig.MIN_TIMER_DURATION, GameConfig.MAX_TIMER_DURATION));
        }
    }
    
    /**
     * Sets a random direction for the velocity vector.
     *
     * @param velocity The velocity vector to modify
     */
    private void setRandomDirection(Vector2 velocity) {
        float angle = MathUtils.random(0f, 360f);
        velocity.x = MathUtils.cosDeg(angle) * GameConfig.ENEMY_SPEED;
        velocity.y = MathUtils.sinDeg(angle) * GameConfig.ENEMY_SPEED;
    }
    
    /**
     * Keeps the enemy within screen bounds by bouncing off edges.
     *
     * @param enemy The enemy to constrain
     */
    private void keepWithinBounds(Enemy enemy) {
        float x = enemy.getX();
        float y = enemy.getY();
        float charWidth = enemy.getSprite().getWidth() * enemy.getSprite().getScaleX();
        float charHeight = enemy.getSprite().getHeight() * enemy.getSprite().getScaleY();
        Vector2 velocity = enemy.getVelocity();
        
        // Bounce off edges
        if (x < 0) {
            enemy.setPosition(0, y);
            velocity.x = Math.abs(velocity.x);
        } else if (x + charWidth > GameConfig.SCREEN_WIDTH) {
            enemy.setPosition(GameConfig.SCREEN_WIDTH - charWidth, y);
            velocity.x = -Math.abs(velocity.x);
        }
        
        if (y < 0) {
            enemy.setPosition(x, 0);
            velocity.y = Math.abs(velocity.y);
        } else if (y + charHeight > GameConfig.SCREEN_HEIGHT) {
            enemy.setPosition(x, GameConfig.SCREEN_HEIGHT - charHeight);
            velocity.y = -Math.abs(velocity.y);
        }
    }
}
