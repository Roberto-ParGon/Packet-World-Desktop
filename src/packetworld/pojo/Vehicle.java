/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packetworld.pojo;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Lenovo
 */
public class Vehicle {

    @SerializedName("id")
    private Integer id;
    
    @SerializedName("marca")
    private String brand;
    
    @SerializedName("modelo")
    private String model;
    
    @SerializedName("anio")
    private Integer year;
    
    @SerializedName("nii")
    private String nii;
    
    @SerializedName("tipoUnidad")
    private String type;
    
    @SerializedName("vin")
    private String vin;
    
    @SerializedName("activo")
    private Boolean isActive;

    public Vehicle() {
    }

    public Vehicle(Integer id, String brand, String model, Integer year, String nii, String type, String vin, Boolean isActive) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.nii = nii;
        this.type = type;
        this.vin = vin;
        this.isActive = isActive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getNii() {
        return nii;
    }

    public void setNii(String nii) {
        this.nii = nii;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
 
    @Override
    public String toString() {
        return nii + " - " + brand + " " + model + " " + year;
    }
}
