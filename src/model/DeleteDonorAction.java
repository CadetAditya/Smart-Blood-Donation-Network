package model;

import service.BloodBankManager;

public class DeleteDonorAction implements Action {
    private final Donor donor;

    public DeleteDonorAction(Donor donor) {
        this.donor = new Donor(donor); // Store a snapshot of the donor
    }

    @Override
    public void undo(BloodBankManager manager) {
        manager.undoDeleteDonor(donor);
    }

    @Override
    public String getDescription() {
        return "Deleted Donor: " + donor.getName() + " (ID: " + donor.getId() + ")";
    }
}
