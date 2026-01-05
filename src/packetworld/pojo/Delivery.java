package packetworld.pojo;

public class Delivery {
    private int id;
    private String trackingNumber;
    private String senderName;
    private String recipientName;
    private String origin;
    private String destination;
    private String weight;
    private String status; // Ej: Pendiente, Enviado, Entregado, Cancelado
    private String phone;
    private String email;
    private String date; // formato simple: YYYY-MM-DD o lo que prefieras

    public Delivery() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getSummary() {
        return (trackingNumber == null ? "" : trackingNumber) + " — " +
               (recipientName == null ? "" : recipientName);
    }
}
