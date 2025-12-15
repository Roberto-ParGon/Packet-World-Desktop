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
import packetworld.pojo.ResponseHTTP;
import packetworld.pojo.Vehicle;
import packetworld.utility.Constants;

/**
 *
 * @author Lenovo
 */
public class VehicleImp {

    public static HashMap<String, Object> getAll() {
        HashMap<String, Object> responseMap = new LinkedHashMap<>();
        String url = Constants.URL_WS + "vehiculo/obtener-todos";

        ResponseHTTP responseAPI = Connection.requestGET(url);

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type listType = new TypeToken<List<Vehicle>>() {
                }.getType();
                List<Vehicle> list = gson.fromJson(responseAPI.getContent(), listType);

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
                Type listType = new TypeToken<List<Vehicle>>() {
                }.getType();
                List<Vehicle> list = gson.fromJson(responseAPI.getContent(), listType);

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

    public static Vehicle getById(int idVehicle) {
        Vehicle vehicle = null;
        String url = Constants.URL_WS + "vehiculo/obtener/" + idVehicle;
        ResponseHTTP response = Connection.requestGET(url);

        if (response.getCode() == HttpURLConnection.HTTP_OK) {
            try {
                vehicle = new com.google.gson.Gson().fromJson(response.getContent(), Vehicle.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vehicle;
    }
}
