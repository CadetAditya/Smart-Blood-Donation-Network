package service;

import model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class BloodBankManager {
    // Core DSA Data Structures
    private final HashMap<Integer, Donor> donorById;
    private final HashMap<String, Donor> donorByPhone;
    private final HashMap<String, HashMap<String, ArrayList<Donor>>> donorIndex;
    private final PriorityQueue<Request> emergencyQueue;
    private final LinkedList<Donation> donationHistory;
    private final Stack<Action> undoStack;

    // Auto-increment ID counters
    private int nextDonorId = 1;
    private int nextRequestId = 1;
    private int nextDonationId = 1;

    public BloodBankManager() {
        this.donorById = new HashMap<>();
        this.donorByPhone = new HashMap<>();
        this.donorIndex = new HashMap<>();
        this.donationHistory = new LinkedList<>();
        this.undoStack = new Stack<>();
        
        // PriorityQueue Comparator: Higher priority level first; if equal, earlier request time first
        this.emergencyQueue = new PriorityQueue<>((r1, r2) -> {
            if (r1.getPriority() != r2.getPriority()) {
                return Integer.compare(r2.getPriority().getLevel(), r1.getPriority().getLevel());
            }
            return r1.getRequestTime().compareTo(r2.getRequestTime());
        });

        // Seed mock data for demonstration
        seedMockData();
    }

    // ==========================================
    // DONOR OPERATIONS
    // ==========================================

    public synchronized void registerDonor(String name, int age, String phone, String bloodGroup, String city, LocalDate lastDonationDate, boolean available) {
        if (donorByPhone.containsKey(phone)) {
            Donor existing = donorByPhone.get(phone);
            throw new IllegalArgumentException("Phone number already registered to " + existing.getName());
        }
        
        Donor donor = new Donor(nextDonorId++, name, age, phone, bloodGroup, city, lastDonationDate, available);
        addDonorInternal(donor);
        
        // Push to Undo stack
        undoStack.push(new AddDonorAction(donor));
    }

    public synchronized void updateDonor(int id, String name, int age, String phone, String bloodGroup, String city, LocalDate lastDonationDate, boolean available) {
        Donor oldState = donorById.get(id);
        if (oldState == null) return;

        // If phone number changes, verify it doesn't collide with another donor
        if (!oldState.getPhone().equals(phone) && donorByPhone.containsKey(phone)) {
            Donor existing = donorByPhone.get(phone);
            throw new IllegalArgumentException("Phone number already registered to " + existing.getName());
        }

        Donor oldCopy = new Donor(oldState);
        Donor newState = new Donor(id, name, age, phone, bloodGroup, city, lastDonationDate, available);
        
        updateDonorInternal(oldCopy, newState);
        
        // Push to Undo stack
        undoStack.push(new UpdateDonorAction(oldCopy, newState));
    }

    public synchronized void deleteDonor(int id) {
        Donor donor = donorById.get(id);
        if (donor == null) return;

        Donor donorCopy = new Donor(donor);
        deleteDonorInternal(donorCopy);

        // Push to Undo stack
        undoStack.push(new DeleteDonorAction(donorCopy));
    }

    // Internal operations used during standard flow or Undo (bypasses pushing to undo stack again)
    public synchronized void addDonorInternal(Donor donor) {
        donorById.put(donor.getId(), donor);
        donorByPhone.put(donor.getPhone(), donor);
        addToIndex(donor);
    }

    public synchronized void updateDonorInternal(Donor oldState, Donor newState) {
        removeFromIndex(oldState);
        donorByPhone.remove(oldState.getPhone());
        
        donorById.put(newState.getId(), newState);
        donorByPhone.put(newState.getPhone(), newState);
        addToIndex(newState);
    }

    public synchronized void deleteDonorInternal(Donor donor) {
        donorById.remove(donor.getId());
        donorByPhone.remove(donor.getPhone());
        removeFromIndex(donor);
    }

    // ==========================================
    // INDEX MANAGEMENT
    // ==========================================

    private void addToIndex(Donor donor) {
        String bg = donor.getBloodGroup().toUpperCase();
        String city = donor.getCity().trim().toLowerCase();

        donorIndex.computeIfAbsent(bg, k -> new HashMap<>())
                  .computeIfAbsent(city, k -> new ArrayList<>())
                  .add(donor);
    }

    private void removeFromIndex(Donor donor) {
        String bg = donor.getBloodGroup().toUpperCase();
        String city = donor.getCity().trim().toLowerCase();

        HashMap<String, ArrayList<Donor>> cityMap = donorIndex.get(bg);
        if (cityMap != null) {
            ArrayList<Donor> list = cityMap.get(city);
            if (list != null) {
                list.removeIf(d -> d.getId() == donor.getId());
                if (list.isEmpty()) {
                    cityMap.remove(city);
                }
            }
            if (cityMap.isEmpty()) {
                donorIndex.remove(bg);
            }
        }
    }

    // ==========================================
    // RECIPIENT & QUEUE OPERATIONS
    // ==========================================

    public synchronized void addRequest(String patientName, String bloodGroup, String city, int unitsRequired, Priority priority, String hospital) {
        Request request = new Request(nextRequestId++, patientName, bloodGroup, city, unitsRequired, priority, hospital, LocalDateTime.now());
        emergencyQueue.add(request);
    }

    public synchronized void addRequestInternal(Request request) {
        emergencyQueue.add(request);
    }

    public synchronized Request pollNextRequest() {
        return emergencyQueue.poll();
    }

    public synchronized PriorityQueue<Request> getEmergencyQueue() {
        return emergencyQueue;
    }

    // ==========================================
    // SEARCH & COMPATIBILITY ALGORITHMS
    // ==========================================

    /**
     * Get blood groups that can donate to a patient of the given blood group.
     */
    public List<String> getCompatibleDonorGroups(String recipientBloodGroup) {
        List<String> compatible = new ArrayList<>();
        if (recipientBloodGroup == null) return compatible;

        switch (recipientBloodGroup.toUpperCase()) {
            case "O-":
                compatible.add("O-");
                break;
            case "O+":
                compatible.addAll(Arrays.asList("O+", "O-"));
                break;
            case "A-":
                compatible.addAll(Arrays.asList("A-", "O-"));
                break;
            case "A+":
                compatible.addAll(Arrays.asList("A+", "A-", "O+", "O-"));
                break;
            case "B-":
                compatible.addAll(Arrays.asList("B-", "O-"));
                break;
            case "B+":
                compatible.addAll(Arrays.asList("B+", "B-", "O+", "O-"));
                break;
            case "AB-":
                compatible.addAll(Arrays.asList("AB-", "A-", "B-", "O-"));
                break;
            case "AB+":
                compatible.addAll(Arrays.asList("AB+", "AB-", "A+", "A-", "B+", "B-", "O+", "O-"));
                break;
        }
        return compatible;
    }

    /**
     * Highly optimized indexed search for compatible, eligible donors in a specific city.
     * Complexity is O(1) per compatible blood group.
     */
    public synchronized List<Donor> searchCompatibleDonors(String recipientBloodGroup, String city) {
        List<Donor> results = new ArrayList<>();
        if (recipientBloodGroup == null || city == null) return results;

        List<String> compatibleGroups = getCompatibleDonorGroups(recipientBloodGroup);
        String searchCity = city.trim().toLowerCase();

        for (String bg : compatibleGroups) {
            HashMap<String, ArrayList<Donor>> cityMap = donorIndex.get(bg);
            if (cityMap != null) {
                ArrayList<Donor> donors = cityMap.get(searchCity);
                if (donors != null) {
                    for (Donor d : donors) {
                        // Filter out unavailable or currently ineligible donors
                        if (d.isEligible()) {
                            results.add(d);
                        }
                    }
                }
            }
        }
        return results;
    }

    /**
     * Raw exact match lookup from index (O(1) directly from nested Map)
     */
    public synchronized List<Donor> getExactIndexMatches(String bloodGroup, String city) {
        List<Donor> results = new ArrayList<>();
        if (bloodGroup == null || city == null) return results;
        
        HashMap<String, ArrayList<Donor>> cityMap = donorIndex.get(bloodGroup.toUpperCase());
        if (cityMap != null) {
            ArrayList<Donor> donors = cityMap.get(city.trim().toLowerCase());
            if (donors != null) {
                results.addAll(donors); // include all in this group/city regardless of current eligibility status
            }
        }
        return results;
    }

    // ==========================================
    // DONATION COMPLETION
    // ==========================================

    public synchronized void assignDonation(Donor donor, Request request, int units) {
        boolean oldAvailable = donor.isAvailable();
        LocalDate oldLastDonationDate = donor.getLastDonationDate();

        // 1. Update Donor State
        donor.setAvailable(false);
        donor.setLastDonationDate(LocalDate.now());

        // Update indexes (since eligibility depends on lastDonationDate, and availability changed)
        // Note: The index contains the Donor object references, so fields are mutated, but we re-index to be safe.
        removeFromIndex(donor);
        addToIndex(donor);

        // 2. Add to sequential History
        Donation donation = new Donation(nextDonationId++, donor, request, LocalDate.now(), units);
        donationHistory.addLast(donation); // LinkedList append

        // 3. Push Action to Undo Stack
        undoStack.push(new AssignDonorAction(donation, oldAvailable, oldLastDonationDate));
    }

    // ==========================================
    // UNDO HANDLING
    // ==========================================

    public synchronized void undoLastAction() {
        if (!undoStack.isEmpty()) {
            Action action = undoStack.pop();
            action.undo(this);
        }
    }

    public synchronized Stack<Action> getUndoStack() {
        return undoStack;
    }

    // Undo Concrete Implementations (called by Action classes)
    public synchronized void undoAddDonor(Donor donor) {
        deleteDonorInternal(donor);
    }

    public synchronized void undoDeleteDonor(Donor donor) {
        addDonorInternal(donor);
    }

    public synchronized void undoUpdateDonor(Donor oldState) {
        Donor currentState = donorById.get(oldState.getId());
        if (currentState != null) {
            updateDonorInternal(currentState, oldState);
        }
    }

    public synchronized void undoAssignDonor(Donation donation, boolean previousAvailable, LocalDate previousLastDonationDate) {
        // 1. Revert Donor availability & donation date
        Donor donor = donorById.get(donation.getDonor().getId());
        if (donor != null) {
            removeFromIndex(donor);
            donor.setAvailable(previousAvailable);
            donor.setLastDonationDate(previousLastDonationDate);
            addToIndex(donor);
        }

        // 2. Remove donation from LinkedList history
        donationHistory.removeIf(d -> d.getId() == donation.getId());

        // 3. Restore Request to PriorityQueue
        emergencyQueue.add(donation.getRequest());
    }

    // ==========================================
    // GETTERS & DATA EXPOSURE
    // ==========================================

    public synchronized List<Donor> getAllDonors() {
        return new ArrayList<>(donorById.values());
    }

    public synchronized List<Donation> getDonationHistory() {
        return new ArrayList<>(donationHistory);
    }

    public synchronized Donor getDonorById(int id) {
        return donorById.get(id);
    }

    // ==========================================
    // MOCK SEED DATA
    // ==========================================

    private void seedMockData() {
        // Seed Donors
        // 1. Rahul (O+, Indore, donated 100 days ago -> eligible)
        Donor d1 = new Donor(nextDonorId++, "Rahul", 24, "9876543210", "O+", "Indore", LocalDate.now().minusDays(100), true);
        // 2. Aman (O+, Indore, donated 10 days ago -> not eligible yet!)
        Donor d2 = new Donor(nextDonorId++, "Aman", 22, "8765432109", "O+", "Indore", LocalDate.now().minusDays(10), true);
        // 3. Riya (O+, Indore, never donated -> eligible)
        Donor d3 = new Donor(nextDonorId++, "Riya", 21, "7654321098", "O+", "Indore", null, true);
        // 4. Mohit (B+, Bhopal, donated 120 days ago -> eligible)
        Donor d4 = new Donor(nextDonorId++, "Mohit", 29, "6543210987", "B+", "Bhopal", LocalDate.now().minusDays(120), true);
        // 5. Priya (A+, Indore, never donated -> eligible)
        Donor d5 = new Donor(nextDonorId++, "Priya", 20, "5432109876", "A+", "Indore", null, true);
        // 6. Vikram (O-, Indore, never donated -> eligible)
        Donor d6 = new Donor(nextDonorId++, "Vikram", 35, "4321098765", "O-", "Indore", null, true);
        // 7. Rohit (B-, Indore, donated 200 days ago -> eligible)
        Donor d7 = new Donor(nextDonorId++, "Rohit", 27, "3210987654", "B-", "Indore", LocalDate.now().minusDays(200), true);
        // 8. Sneha (AB+, Indore, donated 50 days ago -> not eligible)
        Donor d8 = new Donor(nextDonorId++, "Sneha", 26, "2109876543", "AB+", "Indore", LocalDate.now().minusDays(50), true);

        addDonorInternal(d1);
        addDonorInternal(d2);
        addDonorInternal(d3);
        addDonorInternal(d4);
        addDonorInternal(d5);
        addDonorInternal(d6);
        addDonorInternal(d7);
        addDonorInternal(d8);

        // Seed Patient Requests in PriorityQueue
        // Critical request entered first
        Request r1 = new Request(nextRequestId++, "Patient A (Cancer Ward)", "O+", "Indore", 3, Priority.CRITICAL, "CHL Hospital", LocalDateTime.now().minusMinutes(10));
        // Critical request entered later (should be processed second)
        Request r2 = new Request(nextRequestId++, "Patient B (Accident)", "AB+", "Indore", 4, Priority.CRITICAL, "Medanta Hospital", LocalDateTime.now().minusMinutes(5));
        // High request
        Request r3 = new Request(nextRequestId++, "Patient C (Anemia)", "B+", "Bhopal", 2, Priority.HIGH, "Apollo Hospital", LocalDateTime.now().minusMinutes(8));
        // Low request
        Request r4 = new Request(nextRequestId++, "Patient D (Routine Surgery)", "O-", "Indore", 1, Priority.LOW, "MY Hospital", LocalDateTime.now().minusMinutes(12));

        addRequestInternal(r1);
        addRequestInternal(r2);
        addRequestInternal(r3);
        addRequestInternal(r4);
    }
}
