package triviyou.michal.com.entities;

import java.util.Objects;

public class Game {
    private int id;          // GUID
    private String name;
    private String name_en;
    private String description; // Game description
    private String imageUrl;
    private boolean isActive;

    public Game()
    {

    }
    public Game(int id,String name, String name_en, String description ,String imageUrl,boolean isActive) {
        this.id = id;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.name = name;
        this.name_en = name_en;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
