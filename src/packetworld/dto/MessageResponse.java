/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packetworld.dto;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Lenovo
 */
public class MessageResponse {

    private boolean error;
    @SerializedName("mensaje")
    private String message;

    public MessageResponse() {
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
