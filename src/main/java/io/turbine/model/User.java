package io.turbine.model;

import io.vertx.core.json.JsonObject;

import java.time.Instant;

public class User {
    private long id;
    private String username;
    private Instant creationDate;
    private Instant lastConnection;
    private String password;
    private String salt;
    private String email;

    public User() {
    }

    public User(JsonObject row) {
        id = row.getLong("user_id");
        username = row.getString("username");
        creationDate = row.getInstant("creation_date");
        lastConnection = row.getInstant("last_connection");
        password = row.getString("password");
        salt = row.getString("salt");
        email = row.getString("email");
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(Instant lastConnection) {
        this.lastConnection = lastConnection;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
