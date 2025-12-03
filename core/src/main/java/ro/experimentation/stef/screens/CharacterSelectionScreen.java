package ro.experimentation.stef.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import ro.experimentation.stef.GamesWithStef;
import ro.experimentation.stef.config.GameConfig;

/**
 * Screen for character selection.
 * Allows players to choose their fighter before starting the game.
 */
public class CharacterSelectionScreen implements Screen {
    private final GamesWithStef game;
    private final Texture[] characterTextures;
    private final Texture backgroundTexture;
    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final FillViewport viewport;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout;
    
    private int selectedCharacterIndex;
    
    /**
     * Creates a new character selection screen.
     *
     * @param game The main game instance
     * @param characterTextures Array of character textures
     * @param backgroundTexture The background texture
     */
    public CharacterSelectionScreen(GamesWithStef game, Texture[] characterTextures, Texture backgroundTexture) {
        this.game = game;
        this.characterTextures = characterTextures;
        this.backgroundTexture = backgroundTexture;
        this.selectedCharacterIndex = 0;
        
        this.spriteBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.viewport = new FillViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        
        this.font = new BitmapFont();
        this.font.getData().setScale(GameConfig.DEFAULT_FONT_SCALE);
        this.font.setColor(Color.WHITE);
        this.glyphLayout = new GlyphLayout();
    }
    
    @Override
    public void show() {
        // Called when this screen becomes the current screen
    }
    
    @Override
    public void render(float delta) {
        handleInput();
        draw();
    }
    
    /**
     * Handles player input for character selection.
     */
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selectedCharacterIndex--;
            if (selectedCharacterIndex < 0) {
                selectedCharacterIndex = characterTextures.length - 1;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectedCharacterIndex++;
            if (selectedCharacterIndex >= characterTextures.length) {
                selectedCharacterIndex = 0;
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            startGame();
        }
    }
    
    /**
     * Draws the character selection screen.
     */
    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        
        spriteBatch.begin();
        
        // Draw background
        spriteBatch.draw(backgroundTexture, 0, 0, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        
        // Draw title text
        drawTextCentered("SELECT YOUR FIGHTER", 
                        GameConfig.SCREEN_HEIGHT - GameConfig.CHARACTER_Y_OFFSET, 
                        GameConfig.TITLE_FONT_SCALE);
        
        // Draw instruction text
        drawTextCentered("Use LEFT/RIGHT arrows - Press ENTER to confirm", 
                        GameConfig.CHARACTER_Y_OFFSET, 
                        GameConfig.INSTRUCTION_FONT_SCALE);
        
        // Draw characters
        float characterY = GameConfig.SCREEN_HEIGHT / 2 - GameConfig.CHARACTER_Y_OFFSET;
        
        for (int i = 0; i < characterTextures.length; i++) {
            Texture texture = characterTextures[i];
            float characterX = calculateCharacterX(i, GameConfig.CHARACTER_SCALE, texture);
            
            // Draw character sprite
            spriteBatch.draw(texture, characterX, characterY,
                           texture.getWidth() * GameConfig.CHARACTER_SCALE,
                           texture.getHeight() * GameConfig.CHARACTER_SCALE);
            
            // Draw character name
            font.getData().setScale(GameConfig.NAME_FONT_SCALE);
            glyphLayout.setText(font, GameConfig.CHARACTER_NAMES[i]);
            float nameX = characterX + (texture.getWidth() * GameConfig.CHARACTER_SCALE / 2) - (glyphLayout.width / 2);
            float nameY = characterY - GameConfig.CHARACTER_NAME_OFFSET;
            font.draw(spriteBatch, GameConfig.CHARACTER_NAMES[i], nameX, nameY);
            font.getData().setScale(GameConfig.DEFAULT_FONT_SCALE);
        }
        
        spriteBatch.end();
        
        // Draw selection highlight box
        drawSelectionHighlight(characterY);
    }
    
    /**
     * Draws the selection highlight around the selected character.
     *
     * @param characterY The y position of characters
     */
    private void drawSelectionHighlight(float characterY) {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        
        Texture selectedTexture = characterTextures[selectedCharacterIndex];
        float selectedX = calculateCharacterX(selectedCharacterIndex, GameConfig.CHARACTER_SCALE, selectedTexture);
        float boxWidth = selectedTexture.getWidth() * GameConfig.CHARACTER_SCALE;
        float boxHeight = selectedTexture.getHeight() * GameConfig.CHARACTER_SCALE;
        
        // Draw thick selection box
        for (int i = 0; i < GameConfig.HIGHLIGHT_THICKNESS; i++) {
            shapeRenderer.rect(selectedX - GameConfig.HIGHLIGHT_PADDING - i, 
                             characterY - GameConfig.HIGHLIGHT_PADDING - i,
                             boxWidth + GameConfig.HIGHLIGHT_PADDING * 2 + i * 2, 
                             boxHeight + GameConfig.HIGHLIGHT_PADDING * 2 + i * 2);
        }
        
        shapeRenderer.end();
    }
    
    /**
     * Calculates the x position for a character.
     *
     * @param index Character index
     * @param scale Character scale
     * @param texture Character texture
     * @return The x position
     */
    private float calculateCharacterX(int index, float scale, Texture texture) {
        float spacing = GameConfig.SCREEN_WIDTH / (characterTextures.length + 1);
        return spacing * (index + 1) - (texture.getWidth() * scale / 2);
    }
    
    /**
     * Draws centered text.
     *
     * @param text The text to draw
     * @param y The y position
     * @param scale The font scale
     */
    private void drawTextCentered(String text, float y, float scale) {
        float originalScale = font.getData().scaleX;
        font.getData().setScale(scale);
        glyphLayout.setText(font, text);
        float x = (GameConfig.SCREEN_WIDTH - glyphLayout.width) / 2;
        font.draw(spriteBatch, text, x, y);
        font.getData().setScale(originalScale);
    }
    
    /**
     * Starts the game with the selected character.
     */
    private void startGame() {
        game.startGameWithCharacter(selectedCharacterIndex);
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, true);
    }
    
    @Override
    public void pause() {
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void hide() {
    }
    
    @Override
    public void dispose() {
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}
