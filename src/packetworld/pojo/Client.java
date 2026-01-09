package packetworld.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * POJO para Cliente. Incluye getters/setters en español y métodos de compatibilidad
 * con nombres antiguos en inglés (getFirstName, getLastName, getStreet, etc.)
 */
public class Client {

    @SerializedName("id")
    private Integer id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("calle")
    private String calle;

    @SerializedName("num_ext")
    private String numExt;

    @SerializedName("colonia")
    private String colonia;

    @SerializedName("cp")
    private String cp;

    @SerializedName("ciudad")
    private String ciudad; // opcional si existe

    @SerializedName("correo")
    private String correo;

    @SerializedName("telefono")
    private String telefono;

    public Client() {}

    // --- Getters/Setters "nativos" (español) ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getNumExt() { return numExt; }
    public void setNumExt(String numExt) { this.numExt = numExt; }

    public String getColonia() { return colonia; }
    public void setColonia(String colonia) { this.colonia = colonia; }

    public String getCp() { return cp; }
    public void setCp(String cp) { this.cp = cp; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    // --- Métodos helper (mantengo los que ya creaste) ---
    public String getFullName() {
        String n = nombre == null ? "" : nombre;
        String a = apellido == null ? "" : apellido;
        String full = (n + " " + a).trim();
        return full.isEmpty() ? "Cliente " + (id == null ? "" : id) : full;
    }

    public String getFullAddress() {
        String c = calle == null ? "" : calle;
        String n = numExt == null ? "" : numExt;
        String col = colonia == null ? "" : colonia;
        String cpv = cp == null ? "" : cp;
        String ciudadS = ciudad == null ? "" : ciudad;
        StringBuilder sb = new StringBuilder();
        if (!c.isEmpty()) sb.append(c);
        if (!n.isEmpty()) sb.append(" ").append(n);
        if (!col.isEmpty()) sb.append(", ").append(col);
        if (!ciudadS.isEmpty()) sb.append(", ").append(ciudadS);
        if (!cpv.isEmpty()) sb.append(" CP ").append(cpv);
        String res = sb.toString().trim();
        return res.isEmpty() ? "" : res;
    }

    // ----------------------------
    // Métodos de compatibilidad (nombres antiguos en inglés)
    // Estos delegan a los getters/setters en español para no tocar controladores.
    // ----------------------------

    // firstName / lastName
    public String getFirstName() { return getNombre(); }
    public void setFirstName(String firstName) { setNombre(firstName); }

    public String getLastName() { return getApellido(); }
    public void setLastName(String lastName) { setApellido(lastName); }

    // street / number
    public String getStreet() { return getCalle(); }
    public void setStreet(String street) { setCalle(street); }

    public String getNumber() { return getNumExt(); }
    public void setNumber(String number) { setNumExt(number); }

    // colony / zipCode
    public String getColony() { return getColonia(); }
    public void setColony(String colony) { setColonia(colony); }

    public String getZipCode() { return getCp(); }
    public void setZipCode(String zip) { setCp(zip); }

    // phone / email
    public String getPhone() { return getTelefono(); }
    public void setPhone(String phone) { setTelefono(phone); }

    public String getEmail() { return getCorreo(); }
    public void setEmail(String email) { setCorreo(email); }

    // Si controladores esperan métodos con otros nombres, dímelos y los agrego aquí.

  public Object getStatus() {
    return null;
}
public void setStatus(String activo) {
    // no-op
}
    // Compatibilidad: nombres en español usados en controladores antiguos
public String getNombreCompleto() {
    return getFullName();
}

/**
 * Setter de compatibilidad. Recibe el nombre completo y lo asigna al campo 'nombre'.
 * No intentamos dividir en apellido paterno/materno aquí; si necesitas eso puedes
 * implementar un parser más sofisticado.
 */
public void setNombreCompleto(String nombreCompleto) {
    if (nombreCompleto != null) {
        // Simplemente guardamos todo en 'nombre' para mantener compatibilidad con controladores
        setNombre(nombreCompleto);
    }
}

@Override
    public String toString() {
        return getFullName();
    }
}