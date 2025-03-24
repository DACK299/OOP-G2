package io.github.some_example_name.screens;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.managers.ScoreManager;

public class GameOverScreen implements Screen {
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;
    private static final float PHOTO_SIZE = 180f;
    private static final float MARGIN = 30f;
    private static final float SECTION_SPACING = 25f;
    
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont headerFont;
    private BitmapFont titleFont;
    private BitmapFont detailsFont;
    private BitmapFont messageFont;
    private ShapeRenderer shapeRenderer;
    private final int vehiclesHit;
    private final int vehiclesAvoided;
    private float blinkTimer = 0;
    private boolean showPressSpace = true;
    private GlyphLayout layout;
    private final String dateOfDeath;
    private final String dateOfBirth;
    private final String characterName;
    private Texture characterPhoto;
    private Texture paperTexture;
    private int age;
    private final String caseNumber;
    private final float bloodAlcoholLevel;
    private final int fatalities;
    private final int orphanedChildren;
    private final int affectedFamilies;
    private final int severeInjuries;

    public GameOverScreen(Main game, int vehiclesHit, int totalVehicles) {
        this.game = game;
        this.vehiclesHit = vehiclesHit;
        this.vehiclesAvoided = totalVehicles - vehiclesHit;
        
        // Calculate consistent statistics
        this.fatalities = (int)(vehiclesHit * 1.5f);
        this.orphanedChildren = vehiclesHit * 3;
        this.affectedFamilies = vehiclesHit;
        this.severeInjuries = (int)(vehiclesHit * 2.3f);
        this.bloodAlcoholLevel = 0.08f + (vehiclesHit * 0.02f); // BAC increases with more collisions
        
        // Generate a consistent case number
        this.caseNumber = String.format("%04d", (vehiclesHit * 137 + totalVehicles * 47) % 9000 + 1000);
        
        // Set character name based on selection
        String selectedChar = PlayScreen.getInstance().getSelectedCharacter();
        this.characterName = selectedChar.equals("paul") ? "Paul Walker" : "Dominic Toretto";
        
        // Load textures
        this.characterPhoto = new Texture(Gdx.files.internal(selectedChar.equals("paul") ? "paul walker.png" : "dominic.png"));
        
        // Generate dates for realism
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        this.dateOfDeath = today.format(formatter);
        LocalDate birthDate = selectedChar.equals("paul") ? 
            LocalDate.of(1973, 9, 12) :  // Paul Walker's actual birth date
            LocalDate.of(1967, 7, 18);   // Vin Diesel's birth date (for Dom)
        this.dateOfBirth = birthDate.format(formatter);
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Initialize different fonts for different purposes
        headerFont = new BitmapFont();
        headerFont.getData().setScale(2.5f);
        headerFont.setColor(Color.DARK_GRAY);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2f);
        titleFont.setColor(Color.BLACK);
        
        detailsFont = new BitmapFont();
        detailsFont.getData().setScale(1.5f);
        detailsFont.setColor(Color.BLACK);
        
        messageFont = new BitmapFont();
        messageFont.getData().setScale(1.3f);
        messageFont.setColor(Color.DARK_GRAY);
        
        layout = new GlyphLayout();
    }
    
    @Override
    public void render(float delta) {
        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new PlayScreen(game));
            dispose();
            return;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            dispose();
            return;
        }
        
        // Update blink timer
        blinkTimer += delta;
        if (blinkTimer >= 0.5f) {
            showPressSpace = !showPressSpace;
            blinkTimer = 0;
        }
        
        // Clear screen with paper color background
        Gdx.gl.glClearColor(0.95f, 0.95f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Draw paper texture background
        batch.draw(paperTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        
        // End batch to draw shapes
        batch.end();
        
        // Draw semi-transparent backgrounds for text sections
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 0.85f);
        
        // Header background (increased height)
        float headerHeight = 60;
        shapeRenderer.rect(0, WORLD_HEIGHT - MARGIN - headerHeight, WORLD_WIDTH, headerHeight);
        
        // Case number background (increased height)
        float caseHeight = 40;
        shapeRenderer.rect(MARGIN - 10, WORLD_HEIGHT - MARGIN - headerHeight - SECTION_SPACING * 2 - caseHeight, 
                          300, caseHeight);
        
        // Deceased information background (increased height)
        float infoHeight = 300;
        float infoWidth = WORLD_WIDTH - MARGIN * 2;
        shapeRenderer.rect(MARGIN - 10, WORLD_HEIGHT - MARGIN - headerHeight - SECTION_SPACING * 4 - caseHeight - infoHeight,
                          infoWidth + 20, infoHeight);
        
        // Statistics background (increased height)
        float statsHeight = 200;
        shapeRenderer.rect(MARGIN - 10, WORLD_HEIGHT - MARGIN - headerHeight - SECTION_SPACING * 6 - caseHeight - infoHeight - statsHeight,
                          infoWidth + 20, statsHeight);
        
        // PSA message background (increased height)
        float psaHeight = 150;
        shapeRenderer.rect(MARGIN - 10, MARGIN * 3 - 10, infoWidth + 20, psaHeight);
        
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        // Resume batch for text drawing
        batch.begin();
        
        float currentY = WORLD_HEIGHT - MARGIN;
        
        // Draw header
        String headerText = "AUTOPSY REPORT";
        layout.setText(headerFont, headerText);
        headerFont.draw(batch, headerText, (WORLD_WIDTH - layout.width) / 2, currentY);
        currentY -= layout.height + SECTION_SPACING * 2;
        
        // Draw case details
        layout.setText(detailsFont, caseNumber);
        detailsFont.draw(batch, caseNumber, MARGIN, currentY);
        currentY -= layout.height + SECTION_SPACING;
        
        // Draw deceased information section
        float photoX = MARGIN;
        float photoY = currentY - PHOTO_SIZE;
        batch.draw(characterPhoto, photoX, photoY, PHOTO_SIZE, PHOTO_SIZE);
        
        // Draw details next to photo
        float detailsX = photoX + PHOTO_SIZE + MARGIN;
        float detailsY = currentY;
        
        drawDetails(detailsX, detailsY);
        
        currentY = photoY - SECTION_SPACING * 3;
        
        // Draw impact statistics section
        currentY = drawStatistics(currentY);
        
        currentY -= SECTION_SPACING * 2;
        
        // Draw PSA message
        messageFont.setColor(Color.RED);
        String[] finalMessages = {
            "PREVENTABLE TRAGEDY",
            "This death and all associated casualties were the direct result",
            "of the deceased's decision to operate a vehicle while intoxicated.",
            "",
            "DON'T DRINK AND DRIVE."
        };
        
        for (String message : finalMessages) {
            layout.setText(messageFont, message);
            messageFont.draw(batch, message, (WORLD_WIDTH - layout.width) / 2, currentY);
            currentY -= messageFont.getLineHeight() + 5;
        }
        
        // Draw restart and menu options
        if (showPressSpace) {
            messageFont.setColor(Color.GRAY);
            String restartText = "Press SPACE to try again";
            layout.setText(messageFont, restartText);
            messageFont.draw(batch, restartText, (WORLD_WIDTH - layout.width) / 2, MARGIN * 3);
            
            String menuText = "Press ESC for main menu";
            layout.setText(messageFont, menuText);
            messageFont.draw(batch, menuText, (WORLD_WIDTH - layout.width) / 2, MARGIN * 2);
        }
        
        // Draw final score
        String scoreText = "Final Score: " + ScoreManager.getInstance().getCurrentScore();
        messageFont.setColor(Color.YELLOW);
        layout.setText(messageFont, scoreText);
        messageFont.draw(batch, scoreText, (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT / 2 + 40);
        messageFont.setColor(Color.WHITE);
        
        batch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        try {
            // Only update if dimensions are valid
            if (width > 0 && height > 0) {
                viewport.update(width, height, true);
                camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
                System.out.println("GameOverScreen resized to: " + width + "x" + height);
            } else {
                System.err.println("Invalid resize dimensions: " + width + "x" + height);
            }
        } catch (Exception e) {
            System.err.println("Error resizing GameOverScreen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (headerFont != null) headerFont.dispose();
        if (titleFont != null) titleFont.dispose();
        if (detailsFont != null) detailsFont.dispose();
        if (messageFont != null) messageFont.dispose();
        if (characterPhoto != null) characterPhoto.dispose();
        if (paperTexture != null) paperTexture.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        
        // Clear references
        batch = null;
        headerFont = null;
        titleFont = null;
        detailsFont = null;
        messageFont = null;
        characterPhoto = null;
        paperTexture = null;
        shapeRenderer = null;
    }
    
    // Other required Screen interface methods
    @Override
    public void show() {
        // Initialize resources when screen is shown
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        headerFont = new BitmapFont();
        titleFont = new BitmapFont();
        detailsFont = new BitmapFont();
        messageFont = new BitmapFont();
        layout = new GlyphLayout();
        
        // Create paper texture programmatically
        createPaperTexture();
        
        // Scale fonts
        headerFont.getData().setScale(2.5f);
        titleFont.getData().setScale(2.0f);
        detailsFont.getData().setScale(1.5f);
        messageFont.getData().setScale(1.5f);
    }

    private void createPaperTexture() {
        int width = 512;
        int height = 512;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        
        // Set base color (off-white)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Add some noise to create texture
                float noise = (float) Math.random() * 0.1f;
                int color = Color.rgba8888(0.95f + noise, 0.93f + noise, 0.88f + noise, 1);
                pixmap.drawPixel(x, y, color);
            }
        }
        
        paperTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void hide() {
        dispose(); // Clean up resources when screen is hidden
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    
    private float drawStatistics(float currentY) {
        titleFont.draw(batch, "COLLATERAL DAMAGE ASSESSMENT", MARGIN, currentY);
        currentY -= titleFont.getLineHeight() + SECTION_SPACING;
        
        String[] impactMessages = {
            "• Fatalities: " + fatalities + " innocent lives lost",
            "• Orphaned Children: " + orphanedChildren,
            "• Affected Families: " + affectedFamilies,
            "• Severe Injuries: " + severeInjuries
        };
        
        for (String message : impactMessages) {
            detailsFont.draw(batch, message, MARGIN * 2, currentY);
            currentY -= detailsFont.getLineHeight() + 10;
        }
        
        return currentY;
    }
    
    private void drawDetails(float detailsX, float detailsY) {
        titleFont.draw(batch, "DECEASED INFORMATION", detailsX, detailsY);
        detailsY -= titleFont.getLineHeight() + SECTION_SPACING;
        
        String[] details = {
            "Name: " + characterName,
            "Date of Birth: " + dateOfBirth,
            "Date of Death: " + dateOfDeath,
            "Age: " + age + " years",
            "Location of Death: City Streets",
            "Cause of Death: Multiple Vehicle Collision",
            "Blood Alcohol Level: " + String.format("%.2f%%", bloodAlcoholLevel)
        };
        
        for (String detail : details) {
            detailsFont.draw(batch, detail, detailsX + MARGIN, detailsY);
            detailsY -= detailsFont.getLineHeight() + 10;
        }
    }
}