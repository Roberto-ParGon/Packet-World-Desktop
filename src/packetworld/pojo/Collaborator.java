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
public class Collaborator {

    @SerializedName("idColaborador")
    private Integer idCollaborator;

    @SerializedName("noPersonal")
    private String personalNumber;

    @SerializedName("curp")
    private String curp;

    @SerializedName("nombre")
    private String name;

    @SerializedName("apellidoPaterno")
    private String lastname;

    @SerializedName("apellidoMaterno")
    private String surname;

    @SerializedName("email")
    private String email;

    @SerializedName("rol")
    private String role;

    @SerializedName("idSucursal")
    private String idStore;

    @SerializedName("licencia")
    private String license;

    @SerializedName("foto")
    private byte[] photo;

    @SerializedName("fotoBase64")
    private String photo64;

    @SerializedName("activo")
    private boolean active;

    public Collaborator() {
    }

    public Collaborator(Integer idCollaborator, String personalNumber, String curp, String name, String lastname, String surname, String email, String role, String idStore, String license, byte[] photo, String photo64, boolean active) {
        this.idCollaborator = idCollaborator;
        this.personalNumber = personalNumber;
        this.curp = curp;
        this.name = name;
        this.lastname = lastname;
        this.surname = surname;
        this.email = email;
        this.role = role;
        this.idStore = idStore;
        this.license = license;
        this.photo = photo;
        this.photo64 = photo64;
        this.active = active;
    }

    public Integer getIdCollaborator() {
        return idCollaborator;
    }

    public void setIdCollaborator(Integer idCollaborator) {
        this.idCollaborator = idCollaborator;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIdStore() {
        return idStore;
    }

    public void setIdStore(String idStore) {
        this.idStore = idStore;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhoto64() {
        return photo64;
    }

    public void setPhoto64(String photo64) {
        this.photo64 = photo64;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
