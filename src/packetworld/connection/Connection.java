/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packetworld.connection;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import packetworld.pojo.ResponseHTTP;
import packetworld.utility.Constants;
import packetworld.utility.Utility;

/**
 *
 * @author Lenovo
 */
public class Connection {
     public static ResponseHTTP requestGET(String URL) {
        ResponseHTTP response = new ResponseHTTP();
        try {
            URL urlWS = new URL(URL);
            HttpURLConnection conecctionHTTP = (HttpURLConnection) urlWS.openConnection();
            int code = conecctionHTTP.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                response.setContent(
                        Utility.streamToString(conecctionHTTP.getInputStream()));
            }
            response.setCode(code);
        } catch (MalformedURLException e) {
            response.setCode(Constants.ERROR_MALFORMED_URL);
            response.setContent(e.getMessage());
        } catch (IOException ex) {
            response.setCode(Constants.ERROR_REQUEST);
            response.setContent(ex.getMessage());
        }
        return response;
    }

    public static ResponseHTTP requestWithBody(String URL, String methodHTTP, String params, String contentType) {
        ResponseHTTP response = new ResponseHTTP();
        try {
            URL urlWS = new URL(URL);
            HttpURLConnection ConnectionHTTP = (HttpURLConnection) urlWS.openConnection();
            ConnectionHTTP.setRequestMethod(methodHTTP);
            ConnectionHTTP.setRequestProperty("Content-Type", contentType);
            ConnectionHTTP.setDoOutput(true);
            OutputStream os = ConnectionHTTP.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();
            int code = ConnectionHTTP.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                response.setContent(
                        Utility.streamToString(ConnectionHTTP.getInputStream()));
            }
            response.setCode(code);
        } catch (MalformedURLException e) {
            response.setCode(Constants.ERROR_MALFORMED_URL);
            response.setContent(e.getMessage());
        } catch (IOException ex) {
            response.setCode(Constants.ERROR_REQUEST);
            response.setContent(ex.getMessage());
        }
        return response;
    }

    public static ResponseHTTP requestWithouthBody(String URL, String metodoHTTP) {
        ResponseHTTP response = new ResponseHTTP();
        try {
            URL urlWS = new URL(URL);
            HttpURLConnection connectionHTTP = (HttpURLConnection) urlWS.openConnection();
            connectionHTTP.setRequestMethod(metodoHTTP);
            int code = connectionHTTP.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                response.setContent(
                        Utility.streamToString(connectionHTTP.getInputStream()));
            }
            response.setCode(code);
        } catch (MalformedURLException e) {
            response.setCode(Constants.ERROR_MALFORMED_URL);
            response.setContent(e.getMessage());
        } catch (IOException ex) {
            response.setCode(Constants.ERROR_REQUEST);
            response.setContent(ex.getMessage());
        }
        return response;
    }

}
