/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packetworld.dto;

import packetworld.pojo.Collaborator;

/**
 *
 * @author Lenovo
 */
public class LoginResponse {
    
    private boolean error;
    private String mensaje;
    private Collaborator colaborador;

    public LoginResponse() {
    }

    public LoginResponse(boolean error, String mensaje, Collaborator colaborador) {
        this.error = error;
        this.mensaje = mensaje;
        this.colaborador = colaborador;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Collaborator getCollaborator() {
        return colaborador;
    }

    public void setCollaborator(Collaborator colaborador) {
        this.colaborador = colaborador;
    }
}