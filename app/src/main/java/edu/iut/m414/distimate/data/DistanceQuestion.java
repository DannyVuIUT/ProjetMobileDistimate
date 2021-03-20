package edu.iut.m414.distimate.data;

public class DistanceQuestion {
    private final String from;
    private final String to;
    private final int realDistance;

    public DistanceQuestion(String from, String to, int realDistance) {
        this.from = from;
        this.to = to;
        this.realDistance = realDistance;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getRealDistance() {
        return realDistance;
    }
}
