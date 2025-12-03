package ro.experimentation.stef.config;

/**
 * Central configuration class containing all game constants and settings.
 * Provides a single source of truth for game parameters.
 */
public class GameConfig {
    
    // Screen dimensions
    public static final int SCREEN_WIDTH = 1920;
    public static final int SCREEN_HEIGHT = 1080;
    
    // Health system
    public static final float MAX_HEALTH = 100f;
    public static final float PLAYER_MAX_HEALTH = 300f;
    public static final float DAMAGE_PER_HIT = 10f;
    public static final float HEALTH_BAR_WIDTH = 120f;
    public static final float HEALTH_BAR_HEIGHT = 12f;
    
    // Character selection rendering
    public static final float CHARACTER_SCALE = 0.4f;
    public static final int HIGHLIGHT_THICKNESS = 5;
    public static final float HIGHLIGHT_PADDING = 10f;
    public static final float CHARACTER_Y_OFFSET = 100f;
    public static final float CHARACTER_NAME_OFFSET = 30f;
    
    // Laser/Projectile settings
    public static final float LASER_COOLDOWN_TIME = 0.3f;
    public static final float LASER_SPEED = 700f;
    public static final float ABILITY_COOLDOWN_TIME = 2.0f;
    
    // Enemy AI
    public static final float ENEMY_SPEED = 125f;
    public static final float MIN_TIMER_DURATION = 1f;
    public static final float MAX_TIMER_DURATION = 3f;
    public static final float[][] ENEMY_SPAWN_POSITIONS = {
        {1000f, 400f},  // Enemy 1 position
        {1000f, 0f},    // Enemy 2 position
        {700f, 150f}    // Enemy 3 position
    };
    
    // Player movement
    public static final float PLAYER_SPEED = 200f;
    
    // Character sprite scale
    public static final float SPRITE_SCALE = 0.5f;
    
    // Asset paths
    public static final String BACKGROUND_TEXTURE = "dragonballbackground.jpg";
    public static final String MUSIC_FILE = "01.Chozetsu_Dynamic!_(TV_Size).mp3";
    
    // Character selection
    public static final String[] CHARACTER_TEXTURES = {
        "Brolly_renewed.png",
        "UltraInstinctGoku.png",
        "UltraInstinctGoku1.png",
        "VegitoUltraInstinct1.png"
    };
    
    public static final String[] CHARACTER_NAMES = {
        "Broly",
        "UI Goku",
        "UI Goku 2",
        "Vegito UI"
    };
    
    // Font scales
    public static final float DEFAULT_FONT_SCALE = 3f;
    public static final float TITLE_FONT_SCALE = 3f;
    public static final float NAME_FONT_SCALE = 2f;
    public static final float INSTRUCTION_FONT_SCALE = 2f;
    
    private GameConfig() {
        // Private constructor to prevent instantiation
    }
}
