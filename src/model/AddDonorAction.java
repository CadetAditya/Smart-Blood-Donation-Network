package model;

import service.BloodBankManager;

public class AddDonorAction implements Action {
    private final Donor donor;

    public AddDonorAction(Donor donor) {
        this.donor = donor;
    }

    @Override
    public void undo(BloodBankManager manager) {
        manager.undoAddDonor(donor);
    }

    @Override
    public String getDescription() {
        return "Registered Donor: " + donor.getName() + " (ID: " + donor.getId() + ")";
    }
}
