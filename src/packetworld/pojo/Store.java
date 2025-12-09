package packetworld.pojo;

public class Store {

    private String code;
    private String name;
    private String status;
    private String street;
    private String number;
    private String colony;
    private String zipCode;
    private String city;
    private String state;
    private String phone;
    private String manager;

    public Store() {
    }

    public Store(String code, String name, String status, String street, String number,
            String colony, String zipCode, String city, String state,
            String phone, String manager) {
        this.code = code;
        this.name = name;
        this.status = status;
        this.street = street;
        this.number = number;
        this.colony = colony;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.phone = phone;
        this.manager = manager;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
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
        return String.format("%s #%s, Col. %s, %s, %s, %s", street, number, colony, city, state, zipCode);
    }
}
