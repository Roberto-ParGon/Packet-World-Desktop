package packetworld.pojo;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Envio {

    @SerializedName("id")
    private Integer id;

    @SerializedName("idCliente")
    private Integer idCliente;

    @SerializedName("numGuia")
    private String numGuia;

    @SerializedName("direccionDestino")
    private String direccionDestino;

    @SerializedName("ciudadDestino")
    private String ciudadDestino;

    @SerializedName("estadoDestino")
    private String estadoDestino;

    @SerializedName("idSucursalOrigen")
    private Integer idSucursalOrigen;

    @SerializedName("sucursalOrigen")
    private String sucursalOrigen;

    @SerializedName("costo")
    private Double costo;

    @SerializedName("estatus")
    private String estatus;

    @SerializedName("fechaCreacion")
    private String fechaCreacion;

    @SerializedName("destinatarioNombre")
    private String destinatarioNombre;

    @SerializedName("destinatarioTelefono")
    private String destinatarioTelefono;

    @SerializedName("peso")
    private Double peso;

    @SerializedName("fechaActualizacion")
    private String fechaActualizacion;

    @SerializedName("idColaboradorActualizo")
    private Integer idColaboradorActualizo;

    @SerializedName("paquetes")
    private List<Paquete> paquetes;

    public Envio() {
    }

    // Getters / Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNumGuia() {
        return numGuia;
    }

    public void setNumGuia(String numGuia) {
        this.numGuia = numGuia;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }

    public String getCiudadDestino() {
        return ciudadDestino;
    }

    public void setCiudadDestino(String ciudadDestino) {
        this.ciudadDestino = ciudadDestino;
    }

    public String getEstadoDestino() {
        return estadoDestino;
    }

    public void setEstadoDestino(String estadoDestino) {
        this.estadoDestino = estadoDestino;
    }

    public Integer getIdSucursalOrigen() {
        return idSucursalOrigen;
    }

    public void setIdSucursalOrigen(Integer idSucursalOrigen) {
        this.idSucursalOrigen = idSucursalOrigen;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getDestinatarioNombre() {
        return destinatarioNombre;
    }

    public void setDestinatarioNombre(String destinatarioNombre) {
        this.destinatarioNombre = destinatarioNombre;
    }

    public String getDestinatarioTelefono() {
        return destinatarioTelefono;
    }

    public void setDestinatarioTelefono(String destinatarioTelefono) {
        this.destinatarioTelefono = destinatarioTelefono;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getIdColaboradorActualizo() {
        return idColaboradorActualizo;
    }

    public void setIdColaboradorActualizo(Integer idColaboradorActualizo) {
        this.idColaboradorActualizo = idColaboradorActualizo;
    }

    public List<Paquete> getPaquetes() {
        return paquetes;
    }

    public void setPaquetes(List<Paquete> paquetes) {
        this.paquetes = paquetes;
    }

    public Integer getClientId() {
        return this.idCliente;
    }

    public void setClientId(Integer clientId) {
        this.idCliente = clientId;
    }

    public String getTracking() {
        return this.numGuia;
    }

    public void setTracking(String tracking) {
        this.numGuia = tracking;
    }

    public String getFecha() {
        return this.fechaCreacion;
    }

    public void setFecha(String fecha) {
        this.fechaCreacion = fecha;
    }

    public String getOrigen() {
        return idSucursalOrigen == null ? "" : String.valueOf(idSucursalOrigen);
    }

    public void setOrigen(String origen) {
        if (origen == null || origen.trim().isEmpty()) {
            this.idSucursalOrigen = null;
            return;
        }
        try {
            this.idSucursalOrigen = Integer.parseInt(origen.trim());
        } catch (NumberFormatException ex) {
            this.idSucursalOrigen = null;
        }
    }

    public String getDestino() {
        return this.direccionDestino;
    }

    public void setDestino(String destino) {
        this.direccionDestino = destino;
    }

    public String getSucursalOrigen() {
        return sucursalOrigen;
    }

    public void setSucursalOrigen(String sucursalOrigen) {
        this.sucursalOrigen = sucursalOrigen;
    }

    public String getEstado() {
        return this.estatus;
    }

    public void setEstado(String estado) {
        this.estatus = estado;
    }
}
