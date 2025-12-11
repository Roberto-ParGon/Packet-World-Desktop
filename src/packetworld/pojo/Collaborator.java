/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packetworld.pojo;

/**
 *
 * @author Lenovo
 */
public class Collaborator {

    private String personalNumber;
    private String name;
    private String lastname;
    private String surname;
    private String curp;
    private String email;
    private String role;
    private String store;
    private String license;

    public Collaborator(String personalNumber, String name, String lastname, String surname, String curp, String email, String role, String store, String license) {
        this.personalNumber = personalNumber;
        this.name = name;
        this.lastname = lastname;
        this.surname = surname;
        this.curp = curp;
        this.email = email;
        this.role = role;
        this.store = store;
        this.license = license;

    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getSurname() {
        return surname;
    }

    public String getCurp() {
        return curp;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getStore() {
        return store;
    }

    public String getLicense() {
        return license;
    }
}
