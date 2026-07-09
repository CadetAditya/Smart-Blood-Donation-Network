package model;

import java.time.LocalDate;

public class Donor {
    private int id;
    private String name;
    private int age;
    private String phone;
    private String bloodGroup;
    private String city;
    private LocalDate lastDonationDate;
    private boolean available;

    public Donor(int id, String name, int age, String phone, String bloodGroup, String city, LocalDate lastDonationDate, boolean available) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.lastDonationDate = lastDonationDate;
        this.available = available;
    }

    // Copy constructor for Undo functionality
    public Donor(Donor other) {
        this.id = other.id;
        this.name = other.name;
        this.age = other.age;
        this.phone = other.phone;
        this.bloodGroup = other.bloodGroup;
        this.city = other.city;
        this.lastDonationDate = other.lastDonationDate;
        this.available = other.available;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public LocalDate getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // A donor is eligible for donation if they are available AND either haven't donated before, or donated > 90 days ago.
    public boolean isEligible() {
        if (!available) {
            return false;
        }
        if (lastDonationDate == null) {
            return true;
        }
        return lastDonationDate.plusDays(90).isBefore(LocalDate.now()) || lastDonationDate.plusDays(90).isEqual(LocalDate.now());
    }

    @Override
    public String toString() {
        return name + " (" + bloodGroup + ", " + city + ")";
    }
}
