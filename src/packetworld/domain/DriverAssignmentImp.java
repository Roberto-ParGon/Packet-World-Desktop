/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packetworld.domain;

import com.google.gson.Gson;
import java.net.HttpURLConnection;
import java.util.HashMap;
import packetworld.connection.Connection;
import packetworld.dto.MessageResponse;
import packetworld.pojo.DriverAssignment;
import packetworld.pojo.ResponseHTTP;
import packetworld.utility.Constants;

/**
 *
 * @author Lenovo
 */
public class DriverAssignmentImp {

    public static DriverAssignment getAssignment(int driverId) {
        DriverAssignment assignment = null;
        String url = Constants.URL_WS + "conductor-asignacion/obtener/" + driverId;

        ResponseHTTP response = Connection.requestGET(url);

        if (response.getCode() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                assignment = gson.fromJson(response.getContent(), DriverAssignment.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return assignment;
    }

    public static MessageResponse assignVehicle(int driverId, int vehicleId) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "conductor-asignacion/asignar-vehiculo/" + driverId;

        HashMap<String, Integer> map = new HashMap<>();
        map.put("vehiculoId", vehicleId);

        Gson gson = new Gson();
        String jsonParams = gson.toJson(map);

        ResponseHTTP responseAPI = Connection.requestWithBody(url, "POST", jsonParams, "application/json");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al asignar vehículo. Código: " + responseAPI.getCode());
        }
        return response;
    }

    public static MessageResponse unassignVehicle(int driverId) {
        MessageResponse response = new MessageResponse();

        String url = Constants.URL_WS + "conductor-asignacion/desasignar-vehiculo/" + driverId;

        ResponseHTTP responseAPI = Connection.requestWithouthBody(url, "POST");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al desasignar. Código: " + responseAPI.getCode());
        }
        return response;
    }
}
