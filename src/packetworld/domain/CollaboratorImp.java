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
import java.util.ArrayList;
import java.util.Collections;
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
            Type listType = new TypeToken<List<Collaborator>>() {
            }.getType();

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

    public static MessageResponse uploadPhoto(int idCollaborator, byte[] photoBytes) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "colaborador/subir-foto/" + idCollaborator;

        ResponseHTTP responseAPI = Connection.requestWithByteBody(url, "PUT", photoBytes, "application/octet-stream");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al subir foto. Código: " + responseAPI.getCode());
        }
        return response;
    }

    public static Collaborator getCollaboratorPhoto(int idCollaborator) {
        Collaborator collaborator = null;
        String url = Constants.URL_WS + "colaborador/obtener-foto/" + idCollaborator;

        ResponseHTTP responseAPI = Connection.requestGET(url);

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                collaborator = gson.fromJson(responseAPI.getContent(), Collaborator.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return collaborator;
    }

    public static MessageResponse delete(Integer idCollaborator) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "colaborador/eliminar/" + idCollaborator;

        ResponseHTTP responseAPI = Connection.requestWithouthBody(url, "DELETE");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al eliminar. Código: " + responseAPI.getCode());
        }
        return response;
    }
    public static List<Collaborator> getAvailableDrivers() {
        try {
            String url = Constants.URL_WS + "colaborador/obtener-todos";
            System.out.println("CollaboratorImp.getAvailableDrivers -> URL: " + url);

            ResponseHTTP resp = Connection.requestGET(url);

            if (resp == null) {
                System.err.println("CollaboratorImp.getAvailableDrivers -> resp == null");
                return Collections.emptyList();
            }

            System.out.println("CollaboratorImp.getAvailableDrivers -> HTTP " + resp.getCode() + " body: " + resp.getContent());

            if (resp.getCode() != java.net.HttpURLConnection.HTTP_OK) {
                // Si no es 200, devolvemos vacío y se imprime el body para depuración
                return Collections.emptyList();
            }

            String body = resp.getContent();
            if (body == null || body.trim().isEmpty()) {
                return Collections.emptyList();
            }

            Gson g = new Gson();
            Type listType = new TypeToken<List<Collaborator>>() {}.getType();
            List<Collaborator> all = g.fromJson(body, listType);
            if (all == null || all.isEmpty()) return Collections.emptyList();

            // Filtrar por rol que indique "conductor" (ignora mayúsculas/minúsculas) y opcionalmente activo = true
            List<Collaborator> drivers = new ArrayList<>();
for (Collaborator c : all) {
    if (c == null) continue;
    String role = c.getRole();
    if (role == null) continue;
    String rl = role.trim().toLowerCase();
    if (rl.contains("conduc") || rl.contains("driver") || rl.equals("conductor") || rl.equals("chofer")) {
        drivers.add(c);
    }
}

            System.out.println("CollaboratorImp.getAvailableDrivers -> found drivers: " + drivers.size());
            return drivers;

        } catch (Throwable ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
}
