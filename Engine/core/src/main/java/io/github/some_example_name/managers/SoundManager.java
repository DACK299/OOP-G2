package io.github.some_example_name.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private Map<String, Sound> soundEffects;
    private Map<String, Music> music;
    private Music currentMusic;
    private float musicVolume = 0.5f;
    private float soundVolume = 0.7f;
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;
    
    private SoundManager() {
        soundEffects = new HashMap<>();
        music = new HashMap<>();
        loadAudio();
    }
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    private void loadAudio() {
        // Load sound effects
        soundEffects.put("wall_collision", Gdx.audio.newSound(Gdx.files.internal("sounds/wall_hit.wav")));
        
        // Load music
        music.put("play_screen_music", Gdx.audio.newMusic(Gdx.files.internal("music/play_screen.mp3")));
        music.put("play_screen2_music", Gdx.audio.newMusic(Gdx.files.internal("music/play_screen2.mp3")));
    }
    
    public void playSound(String soundName) {
        if (soundEnabled && soundEffects.containsKey(soundName)) {
            soundEffects.get(soundName).play(soundVolume);
        }
    }
    
    public void playMusic(String musicName) {
        if (musicEnabled && music.containsKey(musicName)) {
            // Stop current music if playing
            if (currentMusic != null && currentMusic.isPlaying()) {
                currentMusic.stop();
            }
            
            // Play new music
            currentMusic = music.get(musicName);
            currentMusic.setVolume(musicVolume);
            currentMusic.setLooping(true);
            currentMusic.play();
        }
    }
    
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }
    
    public void pauseMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }
    
    public void resumeMusic() {
        if (currentMusic != null && musicEnabled) {
            currentMusic.play();
        }
    }
    
    // Volume control methods
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.min(1.0f, Math.max(0.0f, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
    }
    
    public void setSoundVolume(float volume) {
        this.soundVolume = Math.min(1.0f, Math.max(0.0f, volume));
    }
    
    // Enable/disable methods
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled && currentMusic != null) {
            currentMusic.pause();
        } else if (enabled && currentMusic != null) {
            currentMusic.play();
        }
    }
    
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }
    
    public void dispose() {
        // Dispose all sound effects
        for (Sound sound : soundEffects.values()) {
            sound.dispose();
        }
        soundEffects.clear();
        
        // Dispose all music
        for (Music musicTrack : music.values()) {
            musicTrack.dispose();
        }
        music.clear();
        
        instance = null;
    }
}