package model;

import java.time.LocalDateTime;

public class Request {
    private int id;
    private String patientName;
    private String bloodGroup;
    private String city;
    private int unitsRequired;
    private Priority priority;
    private String hospital;
    private LocalDateTime requestTime;

    public Request(int id, String patientName, String bloodGroup, String city, int unitsRequired, Priority priority, String hospital, LocalDateTime requestTime) {
        this.id = id;
        this.patientName = patientName;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.unitsRequired = unitsRequired;
        this.priority = priority;
        this.hospital = hospital;
        this.requestTime = requestTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getUnitsRequired() {
        return unitsRequired;
    }

    public void setUnitsRequired(int unitsRequired) {
        this.unitsRequired = unitsRequired;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    @Override
    public String toString() {
        return patientName + " (" + priority + " - " + bloodGroup + ")";
    }
}
