package edu.iut.m414.distimate.data;

public class Country {
    private final String id;
    private final int nameId;
    private final int citiesCount;
    private final int area;
    private final int flag;
    private final int image;
    private final int baseDistance;

    public Country(String id, int nameId, int citiesCount, int area, int flag, int image, int baseDistance) {
        this.id = id;
        this.nameId = nameId;
        this.area = area;
        this.citiesCount = citiesCount;
        this.flag = flag;
        this.image = image;
        this.baseDistance = baseDistance;
    }

    public String getId() {
        return id;
    }

    public int getNameId() {
        return nameId;
    }

    public int getCitiesCount() {
        return citiesCount;
    }

    public int getArea() {
        return area;
    }

    public int getFlag() {
        return flag;
    }

    public int getImage() {
        return image;
    }

    public int getBaseDistance() {
        return baseDistance;
    }
}
