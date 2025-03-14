package triviyou.michal.com.entities;

import java.util.Date;

public class UserGameHistory {
    private String userId;
    private int gameId;
    private boolean isFinished;
    private int currentLevel;
    private int failuresNumber;


    public UserGameHistory(int gameId, String userId, boolean isFinished, int currentLevel, int failuresNumber) {
        this.gameId = gameId;
        this.userId = userId;
        this.isFinished = isFinished;
        this.currentLevel = currentLevel;
        this.failuresNumber = failuresNumber;
    }

    public UserGameHistory() {
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

    public int getFailuresNumber() {
        return failuresNumber;
    }

    public void setFailuresNumber(int failuresNumber) {
        this.failuresNumber = failuresNumber;
    }
}
