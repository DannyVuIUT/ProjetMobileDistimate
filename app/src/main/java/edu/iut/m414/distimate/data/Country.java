package edu.iut.m414.distimate.data;

public class Country {
    private String id;
    private String name;
    private int citiesCount;
    private int area;
    private int flag;
    private int image;

    public Country(String id, String name, int citiesCount, int area, int flag, int image){
        this.id = id;
        this.name = name;
        this.area = area;
        this.citiesCount = citiesCount;
        this.flag = flag;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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
