package packetworld.pojo;

import com.google.gson.annotations.SerializedName;

public class Unit {

    @SerializedName("id") 
    private Integer idUnit;

    @SerializedName("marca")
    private String brand;

    @SerializedName("modelo")
    private String model;

    @SerializedName("anio")
    private Integer year;

    @SerializedName("vin")
    private String vin;

    @SerializedName("tipoUnidad") 
    private String type;

    @SerializedName("nii")
    private String nii;

    @SerializedName("activo")
    private boolean active;
    
    @SerializedName("motivoBaja")
    private String deleteReason;

    public Unit() {
    }

    public Integer getIdUnit() { return idUnit; }
    public void setIdUnit(Integer idUnit) { this.idUnit = idUnit; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getNii() { return nii; }
    public void setNii(String nii) { this.nii = nii; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public String getDeleteReason() { return deleteReason; }
    public void setDeleteReason(String deleteReason) { this.deleteReason = deleteReason; }
    
    @Override
    public String toString() {
        return brand + " " + model + " (" + vin + ")";
    }
}