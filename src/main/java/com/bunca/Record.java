package com.bunca;

public class Record {
    private String username;
    private int score;
    private int ammo;
    private int stance;
    private double coeficient;

    public Record() {
    }

    public Record(String username, int score, int ammo, int stance, double coeficient) {
        this.username = username;
        this.score = score;
        this.ammo = ammo;
        this.stance = stance;
        this.coeficient = coeficient;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        coeficient = stance + score / ammo;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public int getStance() {
        return stance;
    }

    public void setStance(int stance) {
        this.stance = stance;
    }

    public double getCoeficient() {
        return coeficient;
    }

    @Override
    public String toString() {
        return "Record{" +
                "username='" + username + '\'' +
                ", score=" + score +
                ", ammo=" + ammo +
                ", stance=" + stance +
                ", coeficient=" + coeficient +
                '}';
    }
}
