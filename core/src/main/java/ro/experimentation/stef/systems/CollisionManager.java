package ro.experimentation.stef.systems;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import ro.experimentation.stef.config.GameConfig;
import ro.experimentation.stef.entities.Character;
import ro.experimentation.stef.entities.Enemy;
import ro.experimentation.stef.entities.Player;
import ro.experimentation.stef.weapons.Projectile;
import ro.experimentation.stef.weapons.ProjectileFactory;

/**
 * Manages all collision detection in the game.
 * Centralizes collision logic and uses cached rectangles for efficiency.
 */
public class CollisionManager {
    // Cached rectangles to reduce allocations
    private final Rectangle projectileRect;
    private final Rectangle characterRect;
    
    /**
     * Creates a new CollisionManager.
     */
    public CollisionManager() {
        this.projectileRect = new Rectangle();
        this.characterRect = new Rectangle();
    }
    
    /**
     * Checks collisions between player projectiles and enemies.
     * Applies damage and removes projectiles that hit.
     *
     * @param projectiles Array of player projectiles
     * @param enemies Array of enemy characters
     * @param projectileFactory The projectile factory for freeing projectiles
     */
    public void checkPlayerProjectileCollisions(Array<Projectile> projectiles, Array<Enemy> enemies, ProjectileFactory projectileFactory) {
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (!projectile.isActive()) {
                continue;
            }
            
            projectileRect.set(projectile.getBoundingRectangle());
            
            boolean hit = false;
            for (Enemy enemy : enemies) {
                if (!enemy.isAlive()) {
                    continue;
                }
                
                characterRect.set(enemy.getBoundingRectangle());
                if (projectileRect.overlaps(characterRect)) {
                    enemy.takeDamage(GameConfig.DAMAGE_PER_HIT);
                    projectile.deactivate();
                    hit = true;
                    break;
                }
            }
            
            if (hit || projectile.isOffScreen(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT)) {
                projectileFactory.freeProjectile(projectile);
                projectiles.removeIndex(i);
            }
        }
    }
    
    /**
     * Checks collisions between enemy projectiles and the player.
     * Applies damage and removes projectiles that hit.
     *
     * @param projectiles Array of enemy projectiles
     * @param player The player character
     * @param projectileFactory The projectile factory for freeing projectiles
     */
    public void checkEnemyProjectileCollisions(Array<Projectile> projectiles, Player player, ProjectileFactory projectileFactory) {
        if (!player.isAlive()) {
            return;
        }
        
        characterRect.set(player.getBoundingRectangle());
        
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (!projectile.isActive()) {
                continue;
            }
            
            projectileRect.set(projectile.getBoundingRectangle());
            
            if (projectileRect.overlaps(characterRect)) {
                player.takeDamage(GameConfig.DAMAGE_PER_HIT);
                projectile.deactivate();
                projectileFactory.freeProjectile(projectile);
                projectiles.removeIndex(i);
            } else if (projectile.isOffScreen(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT)) {
                projectileFactory.freeProjectile(projectile);
                projectiles.removeIndex(i);
            }
        }
    }
}
