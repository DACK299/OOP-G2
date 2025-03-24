package io.github.some_example_name.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class ScoreManager {
    private static final String FILE_PATH = "scores.txt";
    private static ScoreManager instance;
    private int currentScore = 0;
    private int highScore = 0;
    
    private ScoreManager() {
        loadHighScore();
    }
    
    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }
    
    public void incrementScore(int amount) {
        currentScore += amount;
        if (currentScore > highScore) {
            highScore = currentScore;
        }
    }
    
    public void resetScore() {
        currentScore = 0;
    }
    
    public int getCurrentScore() {
        return currentScore;
    }
    
    public int getHighScore() {
        return highScore;
    }
    
    public void saveScore() {
        FileHandle file = Gdx.files.local(FILE_PATH);
        // Ensure the file exists
        if (!file.exists()) {
            try {
                file.writeString("", false);
            } catch (Exception e) {
                System.err.println("Error creating score file: " + e.getMessage());
                return;
            }
        }
        
        // Append the current score to the file
        try {
            file.writeString(currentScore + "\n", true); // Append new score
            System.out.println("Saved score: " + currentScore);
        } catch (Exception e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }
    
    public List<Integer> loadScores() {
        FileHandle file = Gdx.files.local(FILE_PATH);
        List<Integer> scores = new ArrayList<>();
        
        if (file.exists()) {
            try {
                String content = file.readString();
                String[] lines = content.split("\n");
                
                for (String line : lines) {
                    try {
                        if (!line.trim().isEmpty()) {
                            int score = Integer.parseInt(line.trim());
                            scores.add(score);
                        }
                    } catch (NumberFormatException ignored) {
                        // Skip invalid entries
                        System.err.println("Skipping invalid score entry: " + line);
                    }
                }
                
                // Sort scores in descending order
                Collections.sort(scores, Collections.reverseOrder());
                System.out.println("Loaded " + scores.size() + " scores");
            } catch (Exception e) {
                System.err.println("Error loading scores: " + e.getMessage());
            }
        } else {
            System.out.println("No score file found at: " + FILE_PATH);
        }
        
        return scores;
    }
    
    private void loadHighScore() {
        try {
            List<Integer> scores = loadScores();
            if (!scores.isEmpty()) {
                highScore = scores.get(0); // First score is the highest after sorting
                System.out.println("Loaded high score: " + highScore);
            }
        } catch (Exception e) {
            System.err.println("Error loading high score: " + e.getMessage());
        }
    }
}
