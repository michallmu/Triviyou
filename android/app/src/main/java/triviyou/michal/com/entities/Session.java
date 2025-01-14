package triviyou.michal.com.entities;

import java.sql.Time;
import java.sql.Timestamp;

public class Session {
    private String userId;
    private int gameId;
    private boolean isFinished;
    private int currentLevel;
    private Timestamp updatedAt;

    public Session(int gameId, String userId, boolean isFinished, int currentLevel, Timestamp updatedAt) {
        this.gameId = gameId;
        this.userId = userId;
        this.isFinished = isFinished;
        this.currentLevel = currentLevel;
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
