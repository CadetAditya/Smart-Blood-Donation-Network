package model;

import java.time.LocalDate;

public class Donation {
    private int id;
    private Donor donor;
    private Request request;
    private LocalDate donationDate;
    private int units;

    public Donation(int id, Donor donor, Request request, LocalDate donationDate, int units) {
        this.id = id;
        this.donor = donor;
        this.request = request;
        this.donationDate = donationDate;
        this.units = units;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }
}
