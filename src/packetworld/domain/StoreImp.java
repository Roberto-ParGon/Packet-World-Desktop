/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packetworld.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import packetworld.connection.Connection;
import packetworld.dto.MessageResponse;
import packetworld.pojo.ResponseHTTP;
import packetworld.pojo.Store;
import packetworld.utility.Constants;

public class StoreImp {

    public static HashMap<String, Object> getAllStoresMap() {
        HashMap<String, Object> responseMap = new LinkedHashMap<>();
        String url = Constants.URL_WS + "sucursal/obtener-todos";

        ResponseHTTP responseAPI = Connection.requestGET(url);

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type listType = new TypeToken<List<Store>>() {
                }.getType();

                List<Store> list = gson.fromJson(responseAPI.getContent(), listType);

                responseMap.put("error", false);
                responseMap.put("stores", list);
            } catch (Exception e) {
                e.printStackTrace();
                responseMap.put("error", true);
                responseMap.put("message", "Error al procesar la lista de sucursales: " + e.getMessage());
            }
        } else {
            responseMap.put("error", true);
            responseMap.put("message", "Error de conexión: " + responseAPI.getCode());
        }

        return responseMap;
    }

    public static List<Store> getAll() {
        HashMap<String, Object> result = getAllStoresMap();

        if (result != null && !(boolean) result.get("error")) {
            return (List<Store>) result.get("stores");
        }

        return null;
    }

    public static MessageResponse register(Store store) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "sucursal/registrar";

        Gson gson = new Gson();
        String jsonParams = gson.toJson(store);

        ResponseHTTP responseAPI = Connection.requestWithBody(url, "POST", jsonParams, "application/json");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al registrar sucursal. Código: " + responseAPI.getCode());
        }
        return response;
    }

    public static MessageResponse edit(Store store) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "sucursal/editar";

        Gson gson = new Gson();
        String jsonParams = gson.toJson(store);

        ResponseHTTP responseAPI = Connection.requestWithBody(url, "PUT", jsonParams, "application/json");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al editar sucursal. Código: " + responseAPI.getCode());
        }
        return response;
    }

    public static MessageResponse changeStatus(int idStore, String newStatus) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "sucursal/cambiar-estatus";

        Store params = new Store();
        params.setIdStore(idStore);
        params.setStatus(newStatus);

        Gson gson = new Gson();
        String jsonParams = gson.toJson(params);

        ResponseHTTP responseAPI = Connection.requestWithBody(url, "POST", jsonParams, "application/json");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al cambiar estatus. Código: " + responseAPI.getCode());
        }
        return response;
    }
}
