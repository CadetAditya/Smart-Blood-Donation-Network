package model;

import service.BloodBankManager;
import java.time.LocalDate;

public class AssignDonorAction implements Action {
    private final Donation donation;
    private final boolean previousAvailable;
    private final LocalDate previousLastDonationDate;

    public AssignDonorAction(Donation donation, boolean previousAvailable, LocalDate previousLastDonationDate) {
        this.donation = donation;
        this.previousAvailable = previousAvailable;
        this.previousLastDonationDate = previousLastDonationDate;
    }

    @Override
    public void undo(BloodBankManager manager) {
        manager.undoAssignDonor(donation, previousAvailable, previousLastDonationDate);
    }

    @Override
    public String getDescription() {
        return "Matched " + donation.getDonor().getName() + " to Patient " + donation.getRequest().getPatientName();
    }
}
