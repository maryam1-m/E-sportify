package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserGame {

    private int gameId;
    private String title;
    private BigDecimal price;
    private LocalDate releaseDate;
    private String developerName;
    private String categoryName;

    public UserGame(int gameId, String title, BigDecimal price, LocalDate releaseDate, String developerName, String categoryName) {
        this.gameId = gameId;
        this.title = title;
        this.price = price;
        this.releaseDate = releaseDate;
        this.developerName = developerName;
        this.categoryName = categoryName;
    }

    public int getGameId() {
        return gameId;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}