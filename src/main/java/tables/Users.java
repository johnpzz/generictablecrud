package tables;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name="users")
public class Users implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String username;
    private boolean enabled;
    private Date last_login;

    public Users(){

        Date date = new Date();
        this.username = "";
        this.enabled = false;
        this.last_login = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEnabled(String enabled) { this.enabled = Boolean.parseBoolean(enabled); }

    public Date getLast_login() {
        return last_login;
    }

    public void setLast_login(Date last_login) {
        this.last_login = last_login;
    }


    public String toString() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(last_login);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = month + "/" + day + "/" + year;
        return "User: " + this.username + "\tEnabled: " + this.enabled + "\tLast login: " + date + "\n";
    }

}
