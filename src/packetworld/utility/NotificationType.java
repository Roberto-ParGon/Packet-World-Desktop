/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package packetworld.utility;

import javafx.scene.image.Image;

/**
 *
 * @author Lenovo
 */
public enum NotificationType {
    SUCCESS("/packetworld/resources/icons/success.png", "custom-alert-success"),
    FAILURE("/packetworld/resources/icons/failure.png", "custom-alert-failure"),
    CONFIRMATION("/packetworld/resources/icons/confirmation.png", "custom-alert-confirmation"),
    INFORMATION("/packetworld/resources/icons/information.png", "custom-alert-information"),
    DELETE("/packetworld/resources/icons/delete.png", "custom-alert-delete");

    private final String iconPath;
    private final String styleClass;

    NotificationType(String iconPath, String styleClass) {
        this.iconPath = iconPath;
        this.styleClass = styleClass;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public String getIconPath() {
        return iconPath;
    }

    public Image getIcon() {
        java.net.URL url = NotificationType.class.getResource(iconPath);
        return (url != null) ? new Image(url.toExternalForm()) : null;
    }

}
