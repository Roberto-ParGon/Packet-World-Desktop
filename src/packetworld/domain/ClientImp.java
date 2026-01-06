package packetworld.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import packetworld.connection.Connection;
import packetworld.pojo.ResponseHTTP;
import packetworld.pojo.Client;
import packetworld.utility.Constants;
import java.lang.reflect.Type;
import java.util.List;
import packetworld.dto.MessageResponse;

public class ClientImp {

    public static HashMap<String, Object> getAll() {
        HashMap<String,Object> responseMap = new LinkedHashMap<>();
        String url = Constants.URL_WS + "cliente/obtener-todos";
        System.out.println("DEBUG ClientImp.getAll -> URL: " + url);

        ResponseHTTP apiResp = Connection.requestGET(url);

        if (apiResp == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Respuesta nula del servidor (ClientImp)");
            return responseMap;
        }

        System.out.println("DEBUG ClientImp.getAll -> HTTP code: " + apiResp.getCode());
        System.out.println("DEBUG ClientImp.getAll -> body: " + apiResp.getContent());

        if (apiResp.getCode() == HttpURLConnection.HTTP_OK) {
            try {
                Type listType = new TypeToken<List<Client>>(){}.getType();
                List<Client> list = new Gson().fromJson(apiResp.getContent(), listType);
                responseMap.put("error", false);
                responseMap.put("data", list == null ? java.util.Collections.emptyList() : list);
            } catch (Exception e) {
                e.printStackTrace();
                responseMap.put("error", true);
                responseMap.put("message", "Error al procesar JSON de clientes: " + e.getMessage());
            }
        } else {
            responseMap.put("error", true);
            responseMap.put("message", "Error de conexión: " + apiResp.getCode());
        }
        return responseMap;
    }

    public static MessageResponse register(Client client) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "cliente/registrar";

        Gson gson = new Gson();
        String jsonParams = gson.toJson(client);

        ResponseHTTP responseAPI = Connection.requestWithBody(url, "POST", jsonParams, "application/json");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al registrar cliente. Código: " + responseAPI.getCode());
        }
        return response;
    }

    public static MessageResponse edit(Client client) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "cliente/editar";

        Gson gson = new Gson();
        String jsonParams = gson.toJson(client);

        ResponseHTTP responseAPI = Connection.requestWithBody(url, "PUT", jsonParams, "application/json");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al actualizar cliente. Código: " + responseAPI.getCode());
        }
        return response;
    }

    public static MessageResponse delete(Integer idClient) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "cliente/eliminar/" + idClient;

        ResponseHTTP responseAPI = Connection.requestWithouthBody(url, "DELETE");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al eliminar cliente. Código: " + responseAPI.getCode());
        }
        return response;
    }

    public static Client getById(Integer idClient) {
        Client c = null;
        String url = Constants.URL_WS + "cliente/obtener/" + idClient;

        ResponseHTTP responseAPI = Connection.requestGET(url);

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                c = gson.fromJson(responseAPI.getContent(), Client.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return c;
    }
}