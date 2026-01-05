package packetworld.pojo;

import com.google.gson.annotations.SerializedName;

public class Client {

    @SerializedName("id")
    private Integer id;

    @SerializedName("nombre")
    private String firstName;

    @SerializedName("apellido")
    private String lastName;

    @SerializedName("calle")
    private String street;

    @SerializedName("num_ext")
    private String number;

    @SerializedName("colonia")
    private String colony;

    @SerializedName("cp")
    private String zipCode;

    // Algunos endpoints pueden no devolver ciudad/estado; los mantenemos opcionales
    @SerializedName("ciudad")
    private String city;

    @SerializedName("estado")
    private String state;

    @SerializedName("telefono")
    private String phone;

    @SerializedName("correo")
    private String email;

    // Si tu API no devuelve estatus no pasa nada, lo dejamos por compatibilidad
    @SerializedName("estatus")
    private String status;

    public Client() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Utilitarios para la tabla
    public String getFullName() {
        String fn = (firstName == null ? "" : firstName);
        String ln = (lastName == null ? "" : lastName);
        String sep = (fn.isEmpty() || ln.isEmpty()) ? "" : " ";
        return (fn + sep + ln).trim();
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (street != null && !street.isEmpty()) {
            sb.append(street);
        }
        if (number != null && !number.isEmpty()) {
            sb.append(" #").append(number);
        }
        if (colony != null && !colony.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Col. ").append(colony);
        }
        return sb.toString();
    }
}