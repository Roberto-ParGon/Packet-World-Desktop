/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packetworld.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import packetworld.connection.Connection;
import packetworld.pojo.ResponseHTTP;
import packetworld.pojo.Collaborator;
import packetworld.utility.Constants;
import java.lang.reflect.Type;
import java.util.List;
import packetworld.dto.MessageResponse;

/**
 *
 * @author Lenovo
 */
public class CollaboratorImp {

    public static HashMap<String, Object> getAll() {
        HashMap<String, Object> responseMap = new LinkedHashMap<>();
        String url = Constants.URL_WS + "colaborador/obtener-todos";
        
        ResponseHTTP responseAPI = Connection.requestGET(url);
        
        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Collaborator>>() {}.getType();
            
            try {
                List<Collaborator> collaborators = gson.fromJson(responseAPI.getContent(), listType);
                responseMap.put("error", false);
                responseMap.put("collaborators", collaborators);
            } catch (Exception e) {
                responseMap.put("error", true);
                responseMap.put("message", "Error al procesar los datos del servidor.");
            }
        } else {
            responseMap.put("error", true);
            responseMap.put("message", "No se pudo obtener la información. Código: " + responseAPI.getCode());
        }
        return responseMap;
    }

    public static MessageResponse register(Collaborator collaborator) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "colaborador/registrar";
        
        Gson gson = new Gson();
        String jsonParams = gson.toJson(collaborator);
        
        ResponseHTTP responseAPI = Connection.requestWithBody(url, "POST", jsonParams, "application/json");
        
        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al registrar colaborador. Código: " + responseAPI.getCode());
        }
        return response;
    }

 
    public static MessageResponse edit(Collaborator collaborator) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "colaborador/editar";
        
        Gson gson = new Gson();
        String jsonParams = gson.toJson(collaborator);
        
        ResponseHTTP responseAPI = Connection.requestWithBody(url, "PUT", jsonParams, "application/json");
        
        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al actualizar colaborador. Código: " + responseAPI.getCode());
        }
        return response;
    }

    public static MessageResponse delete(String personalNumber) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "colaborador/eliminar/" + personalNumber;
        
        ResponseHTTP responseAPI = Connection.requestWithouthBody(url, "DELETE");
        
        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al eliminar colaborador. Código: " + responseAPI.getCode());
        }
        return response;
    }
}