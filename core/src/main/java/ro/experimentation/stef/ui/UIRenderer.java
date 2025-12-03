package ro.experimentation.stef.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ro.experimentation.stef.config.GameConfig;
import ro.experimentation.stef.entities.Character;

/**
 * Handles rendering of UI elements like health bars and text.
 */
public class UIRenderer {
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout;
    
    /**
     * Creates a new UIRenderer.
     */
    public UIRenderer() {
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.font.getData().setScale(GameConfig.DEFAULT_FONT_SCALE);
        this.font.setColor(Color.WHITE);
        this.glyphLayout = new GlyphLayout();
    }
    
    /**
     * Draws a health bar above a character.
     *
     * @param character The character to draw health bar for
     */
    public void drawHealthBar(Character character) {
        if (!character.isAlive()) {
            return;
        }
        
        // Calculate health bar position above the character's head
        float barX = character.getX() + 
                    (character.getSprite().getWidth() * character.getSprite().getScaleX() / 2) - 
                    (GameConfig.HEALTH_BAR_WIDTH / 2);
        float barY = character.getY() + 
                    (character.getSprite().getHeight() * character.getSprite().getScaleY()) + 10;
        
        // Draw background (red) for max health
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(barX, barY, GameConfig.HEALTH_BAR_WIDTH, GameConfig.HEALTH_BAR_HEIGHT);
        
        // Draw foreground (green) for current health
        float maxHealth = character.getMaxHealth();
        if (maxHealth > 0) {
            float healthPercentage = character.getHealth() / maxHealth;
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(barX, barY, 
                             GameConfig.HEALTH_BAR_WIDTH * healthPercentage, 
                             GameConfig.HEALTH_BAR_HEIGHT);
        }
    }
    
    /**
     * Draws centered text at the specified y position.
     *
     * @param batch The SpriteBatch to use
     * @param text The text to draw
     * @param y The y position
     * @param scale The font scale
     */
    public void drawCenteredText(SpriteBatch batch, String text, float y, float scale) {
        float originalScale = font.getData().scaleX;
        font.getData().setScale(scale);
        glyphLayout.setText(font, text);
        float x = (GameConfig.SCREEN_WIDTH - glyphLayout.width) / 2;
        font.draw(batch, text, x, y);
        font.getData().setScale(originalScale);
    }
    
    /**
     * Draws text at the specified position.
     *
     * @param batch The SpriteBatch to use
     * @param text The text to draw
     * @param x The x position
     * @param y The y position
     */
    public void drawText(SpriteBatch batch, String text, float x, float y) {
        font.draw(batch, text, x, y);
    }
    
    /**
     * Begins shape rendering.
     */
    public void beginShapes() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    }
    
    /**
     * Ends shape rendering.
     */
    public void endShapes() {
        shapeRenderer.end();
    }
    
    /**
     * Sets the projection matrix for the shape renderer.
     *
     * @param projectionMatrix The projection matrix
     */
    public void setShapeProjectionMatrix(com.badlogic.gdx.math.Matrix4 projectionMatrix) {
        shapeRenderer.setProjectionMatrix(projectionMatrix);
    }
    
    /**
     * Gets the glyph layout for text measurements.
     *
     * @return The glyph layout
     */
    public GlyphLayout getGlyphLayout() {
        return glyphLayout;
    }
    
    /**
     * Gets the font.
     *
     * @return The font
     */
    public BitmapFont getFont() {
        return font;
    }
    
    /**
     * Disposes UI resources.
     */
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}
