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
import packetworld.pojo.ResponseHTTP;
import packetworld.utility.Constants;

/**
 * Cliente HTTP para envíos.
 */
public class EnvioImp {

    public static HashMap<String, Object> getAll() {
        HashMap<String,Object> responseMap = new LinkedHashMap<>();
        String url = Constants.URL_WS + "envio/obtener-todos"; // ajusta si tu backend usa otra ruta
        System.out.println("DEBUG EnvioImp.getAll -> URL: " + url);

        ResponseHTTP apiResp = Connection.requestGET(url);
        if (apiResp == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Respuesta nula del servidor");
            return responseMap;
        }

        System.out.println("DEBUG EnvioImp.getAll -> HTTP code: " + apiResp.getCode());
        System.out.println("DEBUG EnvioImp.getAll -> body: " + apiResp.getContent());

        if (apiResp.getCode() == HttpURLConnection.HTTP_OK) {
            try {
                Type listType = new TypeToken<List<Envio>>(){}.getType();
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

    public static MessageResponse register(Envio envio) {
        MessageResponse response = new MessageResponse();
        String url = Constants.URL_WS + "envio/registrar";
        String json = new Gson().toJson(envio);
        ResponseHTTP apiResp = Connection.requestWithBody(url, "POST", json, "application/json");
        if (apiResp != null && apiResp.getCode() == HttpURLConnection.HTTP_OK) {
            response = new Gson().fromJson(apiResp.getContent(), MessageResponse.class);
        } else {
            response.setError(true);
            response.setMessage("Error al registrar envío: " + (apiResp == null ? "sin respuesta" : apiResp.getCode()));
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

    // Reemplaza SOLO este método en packetworld.domain.EnvioImp
public static MessageResponse changeStatus(Integer envioId, String nuevoEstatus, Integer colaboradorId) {
    MessageResponse response = new MessageResponse();
    if (envioId == null || nuevoEstatus == null || nuevoEstatus.trim().isEmpty()) {
        response.setError(true);
        response.setMessage("Parámetros inválidos para changeStatus");
        return response;
    }

    String url = Constants.URL_WS + "envio/cambiar-estatus";
    try {
        // Construir JSON con las claves que espera el WS: id, estatus, idColaboradorActualizo
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("id", envioId);
        payload.put("estatus", nuevoEstatus);
        if (colaboradorId != null) payload.put("idColaboradorActualizo", colaboradorId);

        String body = new Gson().toJson(payload);
        System.out.println("DEBUG EnvioImp.changeStatus -> URL: " + url + " BODY: " + body);

        ResponseHTTP apiResp = Connection.requestWithBody(url, "POST", body, "application/json");
        if (apiResp == null) {
            response.setError(true);
            response.setMessage("No response from server");
            return response;
        }

        System.out.println("DEBUG EnvioImp.changeStatus -> HTTP code: " + apiResp.getCode());
        System.out.println("DEBUG EnvioImp.changeStatus -> content: " + apiResp.getContent());

        if (apiResp.getCode() == HttpURLConnection.HTTP_OK) {
            // parsear MessageResponse devuelto por el servidor
            response = new Gson().fromJson(apiResp.getContent(), MessageResponse.class);
            return response == null ? new MessageResponse() : response;
        } else {
            // intentar parsear un MessageResponse de error si el servidor lo envió
            try {
                MessageResponse parsed = new Gson().fromJson(apiResp.getContent(), MessageResponse.class);
                if (parsed != null) return parsed;
            } catch (Exception ignore) {}
            response.setError(true);
            response.setMessage("Error al cambiar estatus: HTTP " + apiResp.getCode() + " - " + apiResp.getContent());
            return response;
        }
    } catch (Throwable ex) {
        ex.printStackTrace();
        response.setError(true);
        response.setMessage("Error al cambiar estatus: " + ex.getMessage());
        return response;
    }
}

    // compatibilidad: métodos que espera el controlador
    public static packetworld.dto.MessageResponse createEnvio(Envio envio) {
        return register(envio);
    }

    public static packetworld.dto.MessageResponse updateEnvio(Envio envio) {
        return edit(envio);
    }
    public static MessageResponse delete(Integer envioId) {
        MessageResponse mr = new MessageResponse();
        if (envioId == null) {
            mr.setError(true);
            mr.setMessage("ID de envío nulo");
            return mr;
        }

        Gson gson = new Gson();
        String url = Constants.URL_WS + "envio/eliminar/" + envioId;
        System.out.println("DEBUG EnvioImp.delete -> URL: " + url);

        try {
            // Primero intentar DELETE sin body
            ResponseHTTP resp = Connection.requestWithouthBody(url, "DELETE");
            if (resp == null) {
                // fallback: intentar POST (algunos servidores usan POST para eliminar)
                System.out.println("DEBUG EnvioImp.delete -> respuesta nula al DELETE, intentando POST como fallback");
                resp = Connection.requestWithouthBody(url, "POST"); // o requestWithBody(url,"POST","{}", "application/json") si tu Connection lo requiere
            }

            if (resp == null) {
                mr.setError(true);
                mr.setMessage("No response from server");
                return mr;
            }

            System.out.println("DEBUG EnvioImp.delete -> HTTP code: " + resp.getCode());
            System.out.println("DEBUG EnvioImp.delete -> content: " + resp.getContent());

            int code = resp.getCode();
            String content = resp.getContent() == null ? "" : resp.getContent();

            if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_NO_CONTENT) {
                // Si devuelve 200 con JSON MessageResponse, parsearlo; si no, asumir éxito
                try {
                    if (content == null || content.trim().isEmpty()) {
                        mr.setError(false);
                        mr.setMessage("Envío eliminado"); // no hay body, pero fue OK
                    } else {
                        MessageResponse parsed = gson.fromJson(content, MessageResponse.class);
                        mr = (parsed == null) ? mr : parsed;
                    }
                } catch (Exception ex) {
                    mr.setError(false);
                    mr.setMessage("Envío eliminado (respuesta no JSON)");
                }
                return mr;
            } else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                mr.setError(true);
                mr.setMessage("No existe envío con id " + envioId + " (404)");
                return mr;
            } else if (code == HttpURLConnection.HTTP_CONFLICT) {
                mr.setError(true);
                mr.setMessage("Conflicto al eliminar (posible restricción de integridad).");
                return mr;
            } else {
                // intentar parsear body con mensaje de error si existe
                try {
                    MessageResponse parsed = gson.fromJson(content, MessageResponse.class);
                    if (parsed != null) return parsed;
                } catch (Exception ignore) {}
                mr.setError(true);
                mr.setMessage("Error: HTTP " + code + " - " + content);
                return mr;
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
            mr.setError(true);
            mr.setMessage("Error al eliminar: " + ex.getMessage());
            return mr;
        }
    }

   public static MessageResponse assignDriver(Integer envioId, Integer driverId) {
        MessageResponse mr = new MessageResponse();
        if (envioId == null || driverId == null) {
            mr.setError(true);
            mr.setMessage("Parámetros inválidos");
            return mr;
        }
        try {
            String url = Constants.URL_WS + "conductor-asignacion/asignar-envio/" + driverId;
            System.out.println("DEBUG EnvioImp.assignDriver -> URL: " + url);
            // cuerpo JSON con envioId (según tu implementación previa)
            Gson gson = new Gson();
            HashMap<String, Object> payload = new HashMap<>();
            payload.put("envioId", envioId);
            String bodyJson = gson.toJson(payload);

            ResponseHTTP resp = Connection.requestWithBody(url, "POST", bodyJson, "application/json");
            if (resp == null) {
                mr.setError(true);
                mr.setMessage("No response from server");
                return mr;
            }
            System.out.println("DEBUG EnvioImp.assignDriver -> HTTP code: " + resp.getCode());
            System.out.println("DEBUG EnvioImp.assignDriver -> content: " + resp.getContent());

            String content = resp.getContent() == null ? "" : resp.getContent();

            if (resp.getCode() == HttpURLConnection.HTTP_OK) {
                // intentar parsear MessageResponse estándar
                try {
                    MessageResponse parsed = gson.fromJson(content, MessageResponse.class);
                    return parsed == null ? mr : parsed;
                } catch (Exception ex) {
                    mr.setError(false);
                    mr.setMessage("Conductor asignado");
                    return mr;
                }
            } else {
                // Mapeo de mensajes técnicos a mensajes amigables
                String lower = content.toLowerCase();
                if (lower.contains("duplicate") || lower.contains("duplicate entry") || lower.contains("unique constraint")) {
                    mr.setError(true);
                    mr.setMessage("El envío ya está asignado a un conductor.");
                } else if (resp.getCode() == HttpURLConnection.HTTP_CONFLICT) {
                    mr.setError(true);
                    mr.setMessage("Conflicto al asignar (posible duplicado).");
                } else {
                    mr.setError(true);
                    mr.setMessage("HTTP " + resp.getCode() + ": " + content);
                }
                return mr;
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
            mr.setError(true);
            mr.setMessage("Error: " + ex.getMessage());
            return mr;
        }
    }
   
   // EnvioImp.java (cliente) - reemplaza el método unassignDriver existente
public static packetworld.dto.MessageResponse unassignDriver(Integer envioId) {
    packetworld.dto.MessageResponse mr = new packetworld.dto.MessageResponse();
    if (envioId == null) {
        mr.setError(true);
        mr.setMessage("envioId nulo");
        return mr;
    }
    try {
        // NUEVA RUTA: desasignar por envio (la que implementaste en el backend)
        String url = Constants.URL_WS + "conductor-asignacion/desasignar-envio-por-envio/" + envioId;
        System.out.println("DEBUG EnvioImp.unassignDriver -> URL: " + url);

        // Usamos POST porque el WS lo definiste como POST
        packetworld.pojo.ResponseHTTP resp = Connection.requestWithouthBody(url, "POST");
        if (resp == null) {
            mr.setError(true);
            mr.setMessage("No response from server");
            return mr;
        }

        System.out.println("DEBUG EnvioImp.unassignDriver -> HTTP code: " + resp.getCode() + " content: " + resp.getContent());

        if (resp.getCode() == java.net.HttpURLConnection.HTTP_OK) {
            mr = new com.google.gson.Gson().fromJson(resp.getContent(), packetworld.dto.MessageResponse.class);
            // after success, optionally you can force a reload in caller (FXMLEnviosFormController already calls loadData())
        } else {
            mr.setError(true);
            // try to parse server message if any
            try {
                packetworld.dto.MessageResponse parsed = new com.google.gson.Gson().fromJson(resp.getContent(), packetworld.dto.MessageResponse.class);
                mr = parsed == null ? mr : parsed;
            } catch (Exception ex) {
                mr.setMessage("HTTP " + resp.getCode() + ": " + resp.getContent());
            }
        }
    } catch (Throwable ex) {
        ex.printStackTrace();
        mr.setError(true);
        mr.setMessage("Error: " + ex.getMessage());
    }
    return mr;
}
}
