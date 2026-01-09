/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packetworld.utility;

import packetworld.pojo.Collaborator;

/**
 *
 * @author Lenovo
 */
public class UserSession {

    private static UserSession instance;
    private Collaborator user;

    private UserSession(Collaborator user) {
        this.user = user;
    }

    public static void setInstance(Collaborator user) {
        instance = new UserSession(user);
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static void cleanUserSession() {
        instance = null;
    }

    public Collaborator getUser() {
        return user;
    }
}
