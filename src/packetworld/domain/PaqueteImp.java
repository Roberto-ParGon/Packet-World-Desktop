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
import packetworld.pojo.Paquete;
import packetworld.pojo.ResponseHTTP;
import packetworld.utility.Constants;

public class PaqueteImp {

    public static HashMap<String, Object> getAll() {
        HashMap<String,Object> responseMap = new LinkedHashMap<>();
        String url = Constants.URL_WS + "paquete/obtener-todos";
        ResponseHTTP apiResp = Connection.requestGET(url);
        if (apiResp != null && apiResp.getCode() == HttpURLConnection.HTTP_OK) {
            try {
                Type listType = new TypeToken<List<Paquete>>() {}.getType();
                List<Paquete> list = new Gson().fromJson(apiResp.getContent(), listType);
                responseMap.put("error", false);
                responseMap.put("data", list);
            } catch (Exception e) {
                responseMap.put("error", true);
                responseMap.put("message", "Error al procesar JSON de paquetes: " + e.getMessage());
            }
        } else {
            int code = apiResp == null ? -1 : apiResp.getCode();
            responseMap.put("error", true);
            responseMap.put("message", "Error de conexión: " + code);
        }
        return responseMap;
    }

    public static HashMap<String, Object> getByEnvio(int idEnvio) {
        HashMap<String,Object> responseMap = new LinkedHashMap<>();
        String url = Constants.URL_WS + "paquete/obtener-por-envio/" + idEnvio;
        ResponseHTTP apiResp = Connection.requestGET(url);
        if (apiResp != null && apiResp.getCode() == HttpURLConnection.HTTP_OK) {
            try {
                Type listType = new TypeToken<List<Paquete>>() {}.getType();
                List<Paquete> list = new Gson().fromJson(apiResp.getContent(), listType);
                responseMap.put("error", false);
                responseMap.put("data", list);
            } catch (Exception e) {
                responseMap.put("error", true);
                responseMap.put("message", "Error al procesar JSON de paquetes: " + e.getMessage());
            }
        } else {
            int code = apiResp == null ? -1 : apiResp.getCode();
            responseMap.put("error", true);
            responseMap.put("message", "Error de conexión: " + code);
        }
        return responseMap;
    }

    public static MessageResponse addPackage(Paquete p) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "paquete/registrar";
        String json = new Gson().toJson(p);
        ResponseHTTP apiResp = Connection.requestWithBody(url, "POST", json, "application/json");
        if (apiResp != null && apiResp.getCode() == HttpURLConnection.HTTP_OK) {
            response = new Gson().fromJson(apiResp.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al agregar paquete: " + (apiResp == null ? "sin respuesta" : apiResp.getCode()));
        }
        return response;
    }

    public static MessageResponse editPackage(Paquete p) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "paquete/editar";
        String json = new Gson().toJson(p);
        ResponseHTTP apiResp = Connection.requestWithBody(url, "PUT", json, "application/json");
        if (apiResp != null && apiResp.getCode() == HttpURLConnection.HTTP_OK) {
            response = new Gson().fromJson(apiResp.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al actualizar paquete: " + (apiResp == null ? "sin respuesta" : apiResp.getCode()));
        }
        return response;
    }

    public static MessageResponse deletePackage(Integer paqueteId) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "paquete/eliminar/" + paqueteId;
        // Intentamos DELETE; si tu Connection no soporta DELETE, cambia a GET (temporal) o adapta Connection
        ResponseHTTP apiResp = Connection.requestWithBody(url, "DELETE", "", "application/json");
        if (apiResp != null && apiResp.getCode() == HttpURLConnection.HTTP_OK) {
            response = new Gson().fromJson(apiResp.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al eliminar paquete: " + (apiResp == null ? "sin respuesta" : apiResp.getCode()));
        }
        return response;
    }
}