package model;

import service.BloodBankManager;

public class UpdateDonorAction implements Action {
    private final Donor oldDonorState;
    private final Donor newDonorState;

    public UpdateDonorAction(Donor oldDonorState, Donor newDonorState) {
        this.oldDonorState = new Donor(oldDonorState);
        this.newDonorState = new Donor(newDonorState);
    }

    @Override
    public void undo(BloodBankManager manager) {
        manager.undoUpdateDonor(oldDonorState);
    }

    @Override
    public String getDescription() {
        return "Updated Donor: " + oldDonorState.getName() + " (ID: " + oldDonorState.getId() + ")";
    }
}
