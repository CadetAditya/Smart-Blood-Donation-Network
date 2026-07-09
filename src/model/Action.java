package model;

import service.BloodBankManager;

public interface Action {
    void undo(BloodBankManager manager);
    String getDescription();
}
