package com.moukiladev.Javadb;

public class Goods {
    private long Id;
    private String Designation;
    private int Price;

    public Goods(){};

    public Goods(long Id, String Designation, int Price){
        this.Id = Id;
        this.Designation = Designation;
        this.Price = Price;
    }

    public Goods(String Designation, int Price){
        this.Designation = Designation;
        this.Price = Price;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    @Override
    public String toString() {
        return Id+ " - " +
                Designation + " - " +
                Price + " FCFA ";
    }
}
