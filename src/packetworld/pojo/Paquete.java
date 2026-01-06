package packetworld.pojo;

import com.google.gson.annotations.SerializedName;

public class Paquete {

    @SerializedName("id")
    private Integer id;

    // Cambiado a camelCase para coincidir con el JSON que devuelve la API
    @SerializedName("idEnvio")
    private Integer idEnvio;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("peso")
    private Double peso;

    @SerializedName("alto")
    private Double alto;

    @SerializedName("ancho")
    private Double ancho;

    @SerializedName("profundidad")
    private Double profundidad;

    @SerializedName("cantidad")
    private Integer cantidad;

    @SerializedName("valor")
    private Double valor;

    // Cambiado a camelCase
    @SerializedName("fechaCreacion")
    private String fechaCreacion;

    public Paquete() {}

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdEnvio() { return idEnvio; }
    public void setIdEnvio(Integer idEnvio) { this.idEnvio = idEnvio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }

    public Double getAlto() { return alto; }
    public void setAlto(Double alto) { this.alto = alto; }

    public Double getAncho() { return ancho; }
    public void setAncho(Double ancho) { this.ancho = ancho; }

    public Double getProfundidad() { return profundidad; }
    public void setProfundidad(Double profundidad) { this.profundidad = profundidad; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Integer getEnvioId() { return getIdEnvio(); }
    public void setEnvioId(Integer envioId) { setIdEnvio(envioId); }
    
}

