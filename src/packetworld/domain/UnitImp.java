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
import packetworld.pojo.Unit;
import packetworld.utility.Constants;

public class UnitImp {

    public static HashMap<String, Object> getAll() {
        HashMap<String, Object> responseMap = new LinkedHashMap<>();
        String url = Constants.URL_WS + "vehiculo/obtener-todos";
        ResponseHTTP responseAPI = Connection.requestGET(url);

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type listType = new TypeToken<List<Unit>>() {}.getType();
                List<Unit> list = gson.fromJson(responseAPI.getContent(), listType);
                responseMap.put("error", false);
                responseMap.put("data", list);
            } catch (Exception e) {
                responseMap.put("error", true);
                responseMap.put("message", "Error al procesar JSON de vehículos.");
            }
        } else {
            responseMap.put("error", true);
            responseMap.put("message", "Error de conexión: " + responseAPI.getCode());
        }
        return responseMap;
    }

    public static HashMap<String, Object> getAvailable() {
        HashMap<String, Object> responseMap = new LinkedHashMap<>();
        String url = Constants.URL_WS + "vehiculo/obtener-disponibles";
        ResponseHTTP responseAPI = Connection.requestGET(url);

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type listType = new TypeToken<List<Unit>>() {}.getType();
                List<Unit> list = gson.fromJson(responseAPI.getContent(), listType);
                responseMap.put("error", false);
                responseMap.put("data", list);
            } catch (Exception e) {
                responseMap.put("error", true);
                responseMap.put("message", "Error al procesar JSON.");
            }
        } else {
            responseMap.put("error", true);
            responseMap.put("message", "Error conexión: " + responseAPI.getCode());
        }
        return responseMap;
    }

    public static Unit getById(int idVehicle) {
        Unit vehicle = null;
        String url = Constants.URL_WS + "vehiculo/obtener/" + idVehicle;
        ResponseHTTP response = Connection.requestGET(url);

        if (response.getCode() == HttpURLConnection.HTTP_OK) {
            try {
                vehicle = new Gson().fromJson(response.getContent(), Unit.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vehicle;
    }

    public static MessageResponse register(Unit unit) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "vehiculo/registrar";
        Gson gson = new Gson();
        String jsonParams = gson.toJson(unit);
        ResponseHTTP responseAPI = Connection.requestWithBody(url, "POST", jsonParams, "application/json");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al registrar unidad: " + responseAPI.getCode());
        }
        return response;
    }

    public static MessageResponse edit(Unit unit) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "vehiculo/editar";
        Gson gson = new Gson();
        String jsonParams = gson.toJson(unit);
        ResponseHTTP responseAPI = Connection.requestWithBody(url, "PUT", jsonParams, "application/json");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al actualizar unidad: " + responseAPI.getCode());
        }
        return response;
    }

    public static MessageResponse delete(Integer idUnit, String reason) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "vehiculo/baja-vehiculo"; 
        
        Unit params = new Unit();
        params.setIdUnit(idUnit);   
        params.setDeleteReason(reason);

        Gson gson = new Gson();
        String jsonParams = gson.toJson(params);

        ResponseHTTP responseAPI = Connection.requestWithBody(url, "POST", jsonParams, "application/json");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error de conexión. Código: " + responseAPI.getCode());
        }
        return response;
    }
}