
package packetworld.pojo;

import com.google.gson.annotations.SerializedName;

public class DriverAssignment {

    @SerializedName("conductorId")
    private Integer driverId;

    @SerializedName("vehiculoId")
    private Integer vehicleId;

    @SerializedName("fechaActualizacion")
    private String updateDate;

    @SerializedName("fechaCreacion")
    private String creationDate;

    public DriverAssignment() {
    }

    public DriverAssignment(Integer driverId, Integer vehicleId, String updateDate, String creationDate) {
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.updateDate = updateDate;
        this.creationDate = creationDate;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

}
