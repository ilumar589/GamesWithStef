package ro.experimentation.stef.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ro.experimentation.stef.config.GameConfig;
import ro.experimentation.stef.entities.Character;

/**
 * Factory class for creating different types of projectiles.
 * Manages object pooling for efficient projectile creation.
 */
public class ProjectileFactory {
    private final Texture redLaserTexture;
    private final Texture blueLaserTexture;
    private final Texture yellowLaserTexture;
    private final Texture cyanBeamTexture;
    private final Texture greenLaserTexture;
    private final Texture magentaLaserTexture;
    private final Texture orangeLaserTexture;
    
    // Object pool for Vector2 to reduce allocations
    private final Pool<Vector2> vector2Pool;
    
    // Object pool for Sprite to reduce allocations
    private final Pool<Sprite> spritePool;
    
    // Object pool for Projectile to reduce allocations
    private final Pool<Projectile> projectilePool;
    
    /**
     * Creates a new ProjectileFactory with the specified textures.
     */
    public ProjectileFactory(Texture redLaser, Texture blueLaser, Texture yellowLaser,
                           Texture cyanBeam, Texture greenLaser, Texture magentaLaser,
                           Texture orangeLaser) {
        this.redLaserTexture = redLaser;
        this.blueLaserTexture = blueLaser;
        this.yellowLaserTexture = yellowLaser;
        this.cyanBeamTexture = cyanBeam;
        this.greenLaserTexture = greenLaser;
        this.magentaLaserTexture = magentaLaser;
        this.orangeLaserTexture = orangeLaser;
        
        this.vector2Pool = new Pool<Vector2>(100, 500) {
            @Override
            protected Vector2 newObject() {
                return new Vector2();
            }
        };
        
        this.spritePool = new Pool<Sprite>(100, 500) {
            @Override
            protected Sprite newObject() {
                return new Sprite();
            }
        };
        
        this.projectilePool = new Pool<Projectile>(100, 500) {
            @Override
            protected Projectile newObject() {
                return new Projectile() {};
            }
        };
    }
    
    /**
     * Calculates the eye position for shooting based on character sprite.
     *
     * @param character The character shooting
     * @return Vector2 with eye position (must be freed to pool after use)
     */
    public Vector2 calculateEyePosition(Character character) {
        Vector2 pos = vector2Pool.obtain();
        float eyeX = character.getX() - 50 + (character.getSprite().getWidth() * 0.8f);
        float eyeY = character.getY() - 100 + (character.getSprite().getHeight() * 0.8f);
        pos.set(eyeX, eyeY);
        return pos;
    }
    
    /**
     * Creates a basic player laser.
     *
     * @param shooter The character shooting
     * @return The created projectile
     */
    public Projectile createPlayerLaser(Character shooter) {
        Vector2 eyePos = calculateEyePosition(shooter);
        
        Sprite sprite = spritePool.obtain();
        sprite.setTexture(redLaserTexture);
        sprite.setRegion(redLaserTexture);
        sprite.setPosition(eyePos.x, eyePos.y);
        
        Vector2 velocity = vector2Pool.obtain();
        velocity.set(GameConfig.LASER_SPEED, 0);
        
        Projectile projectile = projectilePool.obtain();
        projectile.init(sprite, velocity);
        
        vector2Pool.free(eyePos);
        
        return projectile;
    }
    
    /**
     * Creates an enemy laser aimed at the target.
     *
     * @param shooter The enemy shooting
     * @param target The target to aim at
     * @return The created projectile
     */
    public Projectile createEnemyLaser(Character shooter, Character target) {
        Vector2 eyePos = calculateEyePosition(shooter);
        Vector2 targetCenter = vector2Pool.obtain();
        targetCenter.set(
            target.getX() + (target.getSprite().getWidth() * target.getSprite().getScaleX() / 2),
            target.getY() + (target.getSprite().getHeight() * target.getSprite().getScaleY() / 2)
        );
        
        Vector2 direction = vector2Pool.obtain();
        direction.set(targetCenter.x - eyePos.x, targetCenter.y - eyePos.y);
        direction.nor().scl(GameConfig.LASER_SPEED);
        
        Sprite sprite = spritePool.obtain();
        sprite.setTexture(blueLaserTexture);
        sprite.setRegion(blueLaserTexture);
        sprite.setPosition(eyePos.x, eyePos.y);
        
        Projectile projectile = projectilePool.obtain();
        projectile.init(sprite, direction);
        
        vector2Pool.free(eyePos);
        vector2Pool.free(targetCenter);
        vector2Pool.free(direction);
        
        return projectile;
    }
    
    /**
     * Creates an AOE spread attack.
     *
     * @param shooter The character shooting
     * @param target The target to aim at
     * @param projectiles Array to add projectiles to
     */
    public void createAOESpread(Character shooter, Character target, Array<Projectile> projectiles) {
        Vector2 eyePos = calculateEyePosition(shooter);
        Vector2 targetCenter = vector2Pool.obtain();
        targetCenter.set(
            target.getX() + (target.getSprite().getWidth() * target.getSprite().getScaleX() / 2),
            target.getY() + (target.getSprite().getHeight() * target.getSprite().getScaleY() / 2)
        );
        
        Vector2 baseDirection = vector2Pool.obtain();
        baseDirection.set(targetCenter.x - eyePos.x, targetCenter.y - eyePos.y);
        float baseAngle = baseDirection.angleDeg();
        
        int numProjectiles = 7;
        float spreadAngle = 40f;
        float angleStep = spreadAngle / (numProjectiles - 1);
        float startAngle = baseAngle - (spreadAngle / 2);
        
        for (int i = 0; i < numProjectiles; i++) {
            float angle = startAngle + (i * angleStep);
            Vector2 velocity = vector2Pool.obtain();
            velocity.x = MathUtils.cosDeg(angle);
            velocity.y = MathUtils.sinDeg(angle);
            velocity.scl(GameConfig.LASER_SPEED);
            
            Sprite sprite = spritePool.obtain();
            sprite.setTexture(yellowLaserTexture);
            sprite.setRegion(yellowLaserTexture);
            sprite.setPosition(eyePos.x, eyePos.y);
            
            Projectile projectile = projectilePool.obtain();
            projectile.init(sprite, velocity);
            projectiles.add(projectile);
        }
        
        vector2Pool.free(eyePos);
        vector2Pool.free(targetCenter);
        vector2Pool.free(baseDirection);
    }
    
    /**
     * Creates a beam attack.
     *
     * @param shooter The character shooting
     * @param target The target to aim at
     * @return The created projectile
     */
    public Projectile createBeamAttack(Character shooter, Character target) {
        Vector2 eyePos = calculateEyePosition(shooter);
        Vector2 targetCenter = vector2Pool.obtain();
        targetCenter.set(
            target.getX() + (target.getSprite().getWidth() * target.getSprite().getScaleX() / 2),
            target.getY() + (target.getSprite().getHeight() * target.getSprite().getScaleY() / 2)
        );
        
        Vector2 direction = vector2Pool.obtain();
        direction.set(targetCenter.x - eyePos.x, targetCenter.y - eyePos.y);
        direction.nor().scl(GameConfig.LASER_SPEED * 0.7f);
        
        Sprite sprite = spritePool.obtain();
        sprite.setTexture(cyanBeamTexture);
        sprite.setRegion(cyanBeamTexture);
        sprite.setPosition(eyePos.x, eyePos.y);
        
        Projectile projectile = projectilePool.obtain();
        projectile.init(sprite, direction);
        
        vector2Pool.free(eyePos);
        vector2Pool.free(targetCenter);
        vector2Pool.free(direction);
        
        return projectile;
    }
    
    /**
     * Creates rapid fire lasers for player ability A.
     *
     * @param shooter The player shooting
     * @param projectiles Array to add projectiles to
     */
    public void createRapidFire(Character shooter, Array<Projectile> projectiles) {
        Vector2 eyePos = calculateEyePosition(shooter);
        
        for (int i = -1; i <= 1; i++) {
            Sprite sprite = spritePool.obtain();
            sprite.setTexture(greenLaserTexture);
            sprite.setRegion(greenLaserTexture);
            sprite.setPosition(eyePos.x, eyePos.y + (i * 15));
            
            Vector2 velocity = vector2Pool.obtain();
            velocity.set(GameConfig.LASER_SPEED, 0);
            
            Projectile projectile = projectilePool.obtain();
            projectile.init(sprite, velocity);
            projectiles.add(projectile);
        }
        
        vector2Pool.free(eyePos);
    }
    
    /**
     * Creates circular burst for player ability S.
     *
     * @param shooter The player shooting
     * @param projectiles Array to add projectiles to
     */
    public void createCircularBurst(Character shooter, Array<Projectile> projectiles) {
        Vector2 eyePos = calculateEyePosition(shooter);
        
        int numProjectiles = 12;
        float angleStep = 360f / numProjectiles;
        
        for (int i = 0; i < numProjectiles; i++) {
            float angle = i * angleStep;
            Vector2 velocity = vector2Pool.obtain();
            velocity.x = MathUtils.cosDeg(angle);
            velocity.y = MathUtils.sinDeg(angle);
            velocity.scl(GameConfig.LASER_SPEED);
            
            Sprite sprite = spritePool.obtain();
            sprite.setTexture(magentaLaserTexture);
            sprite.setRegion(magentaLaserTexture);
            sprite.setPosition(eyePos.x, eyePos.y);
            
            Projectile projectile = projectilePool.obtain();
            projectile.init(sprite, velocity);
            projectiles.add(projectile);
        }
        
        vector2Pool.free(eyePos);
    }
    
    /**
     * Creates mega beam for player ability D.
     *
     * @param shooter The player shooting
     * @return The created projectile
     */
    public Projectile createMegaBeam(Character shooter) {
        Vector2 eyePos = calculateEyePosition(shooter);
        
        Sprite sprite = spritePool.obtain();
        sprite.setTexture(orangeLaserTexture);
        sprite.setRegion(orangeLaserTexture);
        sprite.setPosition(eyePos.x, eyePos.y);
        
        Vector2 velocity = vector2Pool.obtain();
        velocity.set(GameConfig.LASER_SPEED, 0);
        
        Projectile projectile = projectilePool.obtain();
        projectile.init(sprite, velocity);
        
        vector2Pool.free(eyePos);
        
        return projectile;
    }
    
    /**
     * Frees a projectile and its resources back to the pools.
     *
     * @param projectile The projectile to free
     */
    public void freeProjectile(Projectile projectile) {
        if (projectile != null) {
            // Return sprite to pool
            if (projectile.getSprite() != null) {
                spritePool.free(projectile.getSprite());
            }
            
            // Return velocity to pool
            if (projectile.getVelocity() != null) {
                vector2Pool.free(projectile.getVelocity());
            }
            
            // Reset and return projectile to pool
            projectile.reset();
            projectilePool.free(projectile);
        }
    }
    
    /**
     * Clears all pools.
     */
    public void dispose() {
        vector2Pool.clear();
        spritePool.clear();
        projectilePool.clear();
    }
}
