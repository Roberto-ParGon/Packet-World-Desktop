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
import packetworld.pojo.Envio;
import packetworld.pojo.Paquete;
import packetworld.pojo.ResponseHTTP;
import packetworld.utility.Constants;

public class EnvioImp {

    public static HashMap<String, Object> getAll() {
        HashMap<String, Object> responseMap = new LinkedHashMap<>();
        String url = Constants.URL_WS + "envio/obtener-todos";

        ResponseHTTP apiResp = Connection.requestGET(url);
        if (apiResp == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Respuesta nula del servidor");
            return responseMap;
        }

        if (apiResp.getCode() == HttpURLConnection.HTTP_OK) {
            try {
                Type listType = new TypeToken<List<Envio>>() {
                }.getType();
                List<Envio> list = new Gson().fromJson(apiResp.getContent(), listType);
                responseMap.put("error", false);
                responseMap.put("data", list == null ? java.util.Collections.emptyList() : list);
            } catch (Exception e) {
                responseMap.put("error", true);
                responseMap.put("message", "Error al procesar JSON de envíos: " + e.getMessage());
            }
        } else {
            responseMap.put("error", true);
            responseMap.put("message", "Error de conexión: " + apiResp.getCode());
        }
        return responseMap;
    }

    public static Envio getById(int idEnvio) {
        Envio envio = null;
        String url = Constants.URL_WS + "envio/obtener/" + idEnvio;
        ResponseHTTP resp = Connection.requestGET(url);
        if (resp != null && resp.getCode() == HttpURLConnection.HTTP_OK) {
            envio = new Gson().fromJson(resp.getContent(), Envio.class);
        }
        return envio;
    }

    public static MessageResponse register(Envio envio, List<Paquete> paquetes) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "envio/registrar";

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("envio", envio);
        payload.put("paquetes", paquetes);

        String json = new Gson().toJson(payload);

        System.out.println("DEBUG EnvioImp.register -> Payload: " + json);

        ResponseHTTP apiResp = Connection.requestWithBody(url, "POST", json, "application/json");

        if (apiResp != null && apiResp.getCode() == HttpURLConnection.HTTP_OK) {
            response = new Gson().fromJson(apiResp.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            String msg = apiResp != null ? apiResp.getContent() : "sin respuesta";
            response.setMessage("Error al registrar envío: " + msg);
        }
        return response;
    }

    public static MessageResponse edit(Envio envio) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "envio/editar";
        String json = new Gson().toJson(envio);
        ResponseHTTP apiResp = Connection.requestWithBody(url, "PUT", json, "application/json");
        if (apiResp != null && apiResp.getCode() == HttpURLConnection.HTTP_OK) {
            response = new Gson().fromJson(apiResp.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al actualizar envío: " + (apiResp == null ? "sin respuesta" : apiResp.getCode()));
        }
        return response;
    }

    public static MessageResponse changeStatus(Integer envioId, String nuevoEstatus, Integer colaboradorId) {
        MessageResponse response = new MessageResponse();
        if (envioId == null || nuevoEstatus == null || nuevoEstatus.trim().isEmpty()) {
            response.setError(true);
            response.setMessage("Parámetros inválidos para changeStatus");
            return response;
        }

        String url = Constants.URL_WS + "envio/cambiar-estatus";
        try {
            HashMap<String, Object> payload = new HashMap<>();
            payload.put("id", envioId);
            payload.put("estatus", nuevoEstatus);
            if (colaboradorId != null) {
                payload.put("idColaboradorActualizo", colaboradorId);
            }

            String body = new Gson().toJson(payload);
            ResponseHTTP apiResp = Connection.requestWithBody(url, "POST", body, "application/json");

            if (apiResp == null) {
                response.setError(true);
                response.setMessage("No response from server");
                return response;
            }

            if (apiResp.getCode() == HttpURLConnection.HTTP_OK) {
                response = new Gson().fromJson(apiResp.getContent(), MessageResponse.class);
                return response == null ? new MessageResponse() : response;
            } else {
                try {
                    MessageResponse parsed = new Gson().fromJson(apiResp.getContent(), MessageResponse.class);
                    if (parsed != null) {
                        return parsed;
                    }
                } catch (Exception ignore) {
                }
                response.setError(true);
                response.setMessage("Error al cambiar estatus: HTTP " + apiResp.getCode());
                return response;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            response.setError(true);
            response.setMessage("Error al cambiar estatus: " + ex.getMessage());
            return response;
        }
    }

    public static MessageResponse delete(Integer envioId) {
        MessageResponse mr = new MessageResponse();
        if (envioId == null) {
            mr.setError(true);
            mr.setMessage("ID de envío nulo");
            return mr;
        }

        String url = Constants.URL_WS + "envio/eliminar/" + envioId;
        try {
            ResponseHTTP resp = Connection.requestWithouthBody(url, "DELETE");
            if (resp == null) {
                resp = Connection.requestWithouthBody(url, "POST");
            }

            if (resp == null) {
                mr.setError(true);
                mr.setMessage("No response from server");
                return mr;
            }

            int code = resp.getCode();
            String content = resp.getContent() == null ? "" : resp.getContent();

            if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_NO_CONTENT) {
                try {
                    if (content.trim().isEmpty()) {
                        mr.setError(false);
                        mr.setMessage("Envío eliminado");
                    } else {
                        MessageResponse parsed = new Gson().fromJson(content, MessageResponse.class);
                        mr = (parsed == null) ? mr : parsed;
                    }
                } catch (Exception ex) {
                    mr.setError(false);
                    mr.setMessage("Envío eliminado");
                }
                return mr;
            } else {
                mr.setError(true);
                mr.setMessage("Error: HTTP " + code);
                return mr;
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
            mr.setError(true);
            mr.setMessage("Error al eliminar: " + ex.getMessage());
            return mr;
        }
    }

    public static MessageResponse assignDriver(Integer envioId, Integer driverId, Integer usuarioLogueadoId) {
        MessageResponse mr = new MessageResponse();

        if (envioId == null || driverId == null) {
            mr.setError(true);
            mr.setMessage("Parámetros inválidos");
            return mr;
        }

        try {
            String url = Constants.URL_WS + "conductor-asignacion/asignar-envio/" + driverId;

            HashMap<String, Object> payload = new HashMap<>();
            payload.put("envioId", envioId);

            payload.put("usuarioLogueadoId", usuarioLogueadoId);

            String bodyJson = new Gson().toJson(payload);

            ResponseHTTP resp = Connection.requestWithBody(url, "POST", bodyJson, "application/json");

            if (resp != null && resp.getCode() == HttpURLConnection.HTTP_OK) {
                try {
                    MessageResponse parsed = new Gson().fromJson(resp.getContent(), MessageResponse.class);
                    return parsed == null ? mr : parsed;
                } catch (Exception ex) {
                    mr.setError(false);
                    mr.setMessage("Conductor asignado");
                    return mr;
                }
            } else {
                mr.setError(true);
                mr.setMessage("Error al asignar: " + (resp != null ? resp.getContent() : "sin respuesta"));
                return mr;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            mr.setError(true);
            mr.setMessage("Error: " + ex.getMessage());
            return mr;
        }
    }

    public static MessageResponse unassignDriver(int envioId, Integer usuarioLogueadoId) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "conductor-asignacion/desasignar-envio-por-envio/" + envioId;

        HashMap<String, Object> params = new HashMap<>();
        params.put("usuarioLogueadoId", usuarioLogueadoId);

        Gson gson = new Gson();
        String jsonParams = gson.toJson(params);

        ResponseHTTP responseAPI = Connection.requestWithBody(url, "POST", jsonParams, "application/json");

        if (responseAPI.getCode() == HttpURLConnection.HTTP_OK) {
            response = gson.fromJson(responseAPI.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al desasignar. Código: " + responseAPI.getCode());
        }
        return response;
    }
}
