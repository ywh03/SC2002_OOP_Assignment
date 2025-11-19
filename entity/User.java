package entity;

import java.io.Serial;
import java.io.Serializable;

public abstract class User implements Serializable, Identifiable, Notifiable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;
    private String fullName;
    private String password;

    public User(String userId, String fullName, String password) {
        this.userId = userId;
        this.fullName = fullName;
        this.password = password;
    }

    @Override
    public String getId() {return userId;}

    public void setUserID(String userId) {this.userId = userId;}
    public String getFullName() {return fullName;}
    public void setFullName(String fullName) {this.fullName = fullName;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    
}