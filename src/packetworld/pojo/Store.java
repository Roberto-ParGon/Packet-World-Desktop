package packetworld.pojo;

import com.google.gson.annotations.SerializedName;

public class Store {

    @SerializedName("idSucursal")
    private Integer idStore;

    @SerializedName("codigoUnico")
    private String code;

    @SerializedName("nombre")
    private String name;

    @SerializedName("estatus")
    private String status;

    @SerializedName("calle")
    private String street;

    @SerializedName("numero")
    private String number;

    @SerializedName("colonia")
    private String colony;

    @SerializedName("codigoPostal")
    private String zipCode;

    @SerializedName("ciudad")
    private String city;

    @SerializedName("estado")
    private String state;

    public Store() {
    }

    public Store(Integer idStore, String code, String name, String status, String street, String number, String colony, String zipCode, String city, String state) {
        this.idStore = idStore;
        this.code = code;
        this.name = name;
        this.status = status;
        this.street = street;
        this.number = number;
        this.colony = colony;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
    }

    public Integer getIdStore() {
        return idStore;
    }

    public void setIdStore(Integer idStore) {
        this.idStore = idStore;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getColony() {
        return colony;
    }

    public void setColony(String colony) {
        this.colony = colony;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFullAddress() {
        return String.format("%s #%s, Col. %s",
                (street != null ? street : "S/C"),
                (number != null ? number : ""),
                (colony != null ? colony : ""));
    }

    public boolean isActiva() {
        return "Activa".equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return (code != null ? code : "S/C") + ": " + name;
    }
}
