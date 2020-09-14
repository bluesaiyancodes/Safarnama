package com.strongties.safarnama.user_classes;

public class LandmarkStat {
    Landmark landmark;
    int landmarkCounter;

    public LandmarkStat() {
    }

    public LandmarkStat(Landmark landmark, int landmarkCounter) {
        this.landmark = landmark;
        this.landmarkCounter = landmarkCounter;
    }

    public Landmark getLandmark() {
        return landmark;
    }

    public void setLandmark(Landmark landmark) {
        this.landmark = landmark;
    }

    public int getLandmarkCounter() {
        return landmarkCounter;
    }

    public void setLandmarkCounter(int landmarkCounter) {
        this.landmarkCounter = landmarkCounter;
    }

    @Override
    public String toString() {
        return "LandmarkStat{" +
                "landmark=" + landmark +
                ", landmarkCounter=" + landmarkCounter +
                '}';
    }
}
