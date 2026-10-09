package com.fittrack.model;

/**
 * Base class for anyone who can log into the system.
 * Subclasses decide where they land after login.
 */
public abstract class Person {

    protected int id;
    protected String name;
    protected String email;

    protected Person() { }

    protected Person(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /** page the person is sent to right after login */
    public abstract String getHomePath();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + ", " + name + ", " + email + "]";
    }
}
