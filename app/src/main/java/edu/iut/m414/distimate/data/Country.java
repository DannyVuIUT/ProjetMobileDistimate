package edu.iut.m414.distimate.data;

public class Country {
    private String id;
    private int nameId;
    private int citiesCount;
    private int area;
    private int flag;
    private int image;

    public Country(String id, int nameId, int citiesCount, int area, int flag, int image) {
        this.id = id;
        this.nameId = nameId;
        this.area = area;
        this.citiesCount = citiesCount;
        this.flag = flag;
        this.image = image;
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
}
