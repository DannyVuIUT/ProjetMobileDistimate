package edu.iut.m414.distimate.data;

public class DistanceQuestion {
    private final String from;
    private final String to;
    private final int actualDistance;

    public DistanceQuestion(String from, String to, int actualDistance) {
        this.from = from;
        this.to = to;
        this.actualDistance = actualDistance;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getActualDistance() {
        return actualDistance;
    }
}
