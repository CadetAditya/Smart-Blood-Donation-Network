# Smart Blood Donation Network - DSA Implementation Guide

This document isolates and explains the core Data Structures and Algorithms (DSA) used in the project. These snippets are extracted directly from the codebase for use in project reports and viva preparation.

---

## 1. O(1) Search by ID (HashMap)

### Concept
Retrieving a donor directly by their unique ID must be fast. A standard `HashMap` maps the `Integer` ID to the `Donor` object using hashing, providing constant-time lookup.

### Implementation
```java
// Declaration in BloodBankManager.java
private final HashMap<Integer, Donor> donorById = new HashMap<>();

// O(1) Insertion
public synchronized void addDonor(Donor donor) {
    donorById.put(donor.getId(), donor);
}

// O(1) Lookup
public synchronized Donor getDonorById(int id) {
    return donorById.get(id); // Returns Donor in O(1)
}

// O(1) Deletion
public synchronized void deleteDonor(int id) {
    donorById.remove(id);
}
```


---

## 2. O(1) Unique Constraint Validation (HashMap)

### Concept
To prevent duplicate registrations, the system uses the donor's phone number as a unique key. Instead of looping through all records ($O(N)$), a `HashMap` checks existence in $O(1)$ time.

### Implementation 
```java
// Declaration in BloodBankManager.java
private final HashMap<String, Donor> donorByPhone = new HashMap<>();

// Validation Logic during Registration
public synchronized void registerDonor(String name, int age, String phone, ...) {
    // O(1) key check
    if (donorByPhone.containsKey(phone)) {
        Donor existing = donorByPhone.get(phone);
        throw new IllegalArgumentException("Phone number already registered to " + existing.getName());
    }
    
    Donor donor = new Donor(nextDonorId++, name, age, phone, ...);
    donorById.put(donor.getId(), donor);
    donorByPhone.put(donor.getPhone(), donor); // Index phone number
}
```


---

## 3. Nested HashMap Search Index

### Concept
This is the application's primary query optimization. Instead of scanning all donors, a nested map structure indexes donors by **Blood Group** and then by **City**:
$$\text{Blood Group} \rightarrow \text{City} \rightarrow \text{List of Donors}$$

### Implementation
```java
// Declaration in BloodBankManager.java
private final HashMap<String, HashMap<String, ArrayList<Donor>>> donorIndex = new HashMap<>();

// Indexing a Donor (Adding to nested maps)
private void addToIndex(Donor donor) {
    String bg = donor.getBloodGroup().toUpperCase();
    String city = donor.getCity().trim().toLowerCase();

    // computeIfAbsent handles map creation on-the-fly
    donorIndex.computeIfAbsent(bg, k -> new HashMap<>())
              .computeIfAbsent(city, k -> new ArrayList<>())
              .add(donor);
}

// De-indexing a Donor (Removing from nested maps)
private void removeFromIndex(Donor donor) {
    String bg = donor.getBloodGroup().toUpperCase();
    String city = donor.getCity().trim().toLowerCase();

    HashMap<String, ArrayList<Donor>> cityMap = donorIndex.get(bg);
    if (cityMap != null) {
        ArrayList<Donor> list = cityMap.get(city);
        if (list != null) {
            list.removeIf(d -> d.getId() == donor.getId()); // Remove matching ID
            if (list.isEmpty()) {
                cityMap.remove(city);
            }
        }
        if (cityMap.isEmpty()) {
            donorIndex.remove(bg);
        }

    }
}

// O(1) Compatibility Indexed Search
public synchronized List<Donor> searchCompatibleDonors(String recipientBloodGroup, String city) {
    List<Donor> results = new ArrayList<>();
    List<String> compatibleGroups = getCompatibleDonorGroups(recipientBloodGroup); // Returns O-, O+ etc.
    String searchCity = city.trim().toLowerCase();

    // Loop compatibility list (Max 8 iterations, O(1) constant time overall)
    for (String bg : compatibleGroups) {
        HashMap<String, ArrayList<Donor>> cityMap = donorIndex.get(bg);
        if (cityMap != null) {
            ArrayList<Donor> donors = cityMap.get(searchCity); // Direct hash lookup
            if (donors != null) {
                for (Donor d : donors) {
                    if (d.isEligible()) { // Check availability and 90-day rule
                        results.add(d);
                    }
                }
            }
        }
    }
    return results;
}
``

---

## 4. Emergency Requests Heap (Priority Queue)

### Concept
Patient requests are prioritized based on emergency levels. The emergency requests are stored in a binary heap (`PriorityQueue`). It uses a custom comparator with a timestamp tie-breaker for FIFO execution under the same priority class.


### Implementation
```java
// Priority Levels mapping
public enum Priority {
    CRITICAL(4), HIGH(3), MEDIUM(2), LOW(1);
    private final int level;
    Priority(int level) { this.level = level; }
    public int getLevel() { return level; }
}

// Declaration inside constructor in BloodBankManager.java
this.emergencyQueue = new PriorityQueue<>((r1, r2) -> {
    // 1. Compare Severity Levels (Descending order: High level first)
    if (r1.getPriority() != r2.getPriority()) {
        return Integer.compare(r2.getPriority().getLevel(), r1.getPriority().getLevel());
    }
    // 2. Tie-Breaker: Compare timestamps (Ascending order: Older request first)
    return r1.getRequestTime().compareTo(r2.getRequestTime());
});


// Enqueue Request: O(log N)
public synchronized void addRequest(Request request) {
    emergencyQueue.add(request);
}

// Dequeue Highest Priority Request: O(log N)
public synchronized Request pollNextRequest() {
    return emergencyQueue.poll();
}
```

---

## 5. Donation Ledger (LinkedList)

### Concept
Completed donations represent a sequential audit trail. New logs are always appended to the end of the history. A double-linked list (`LinkedList`) provides constant-time $O(1)$ insertions.

### Implementation
```java
// Declaration in BloodBankManager.java
private final LinkedList<Donation> donationHistory = new LinkedList<>();

// Record Donation (Append: O(1))
public synchronized void assignDonation(Donor donor, Request request, int units) {
    // ... Update donor details ...
    
    Donation donation = new Donation(nextDonationId++, donor, request, LocalDate.now(), units);
    donationHistory.addLast(donation); // O(1) double-ended append
}
```

---

## 6. Multi-Structure Undo Stack (Command Pattern)

### Concept
To undo actions, operations are pushed onto a LIFO `Stack`. The Command Pattern is utilized: each action is wrapped in an object that knows how to reverse its modifications across all indices and queues.

### Implementation
```java
// Command Contract
public interface Action {
    void undo(BloodBankManager manager);
    String getDescription();
}

// Stack declaration in BloodBankManager.java
private final Stack<Action> undoStack = new Stack<>();

// Trigger Reversal: O(1) pop
public synchronized void undoLastAction() {
    if (!undoStack.isEmpty()) {
        Action action = undoStack.pop();
        action.undo(this); // Execute custom rollback
    }
}

// Example Command Implementation: Reverting Match/Donation
public class AssignDonorAction implements Action {
    private final Donation donation;
    private final boolean previousAvailable;
    private final LocalDate previousLastDonationDate;

    public AssignDonorAction(Donation donation, boolean prevAvail, LocalDate prevDate) {
        this.donation = donation;
        this.previousAvailable = prevAvail;
        this.previousLastDonationDate = prevDate;
    }

    @Override
    public void undo(BloodBankManager manager) {
        // 1. Revert Donor Availability & Last Donation Date
        Donor donor = donation.getDonor();
        donor.setAvailable(previousAvailable);
        donor.setLastDonationDate(previousLastDonationDate);
        
        // 2. Remove transaction record from LinkedList
        manager.getDonationHistory().removeIf(d -> d.getId() == donation.getId());

        // 3. Restore Request to PriorityQueue
        manager.addRequestInternal(donation.getRequest());
    }
}
```

---

## 7. Donor Eligibility Validation (Time Algebra)

### Concept
A donor is only eligible to donate blood if they are marked available AND have not donated within the last 90 days.

### Implementation
```java
// Inside Donor.java
public boolean isEligible() {
    if (!available) {
        return false; // Donor marked unavailable manually
    }
    if (lastDonationDate == null) {
        return true; // Donor has never donated
    }
    // Calculate if 90 days have elapsed since last donation
    return lastDonationDate.plusDays(90).isBefore(LocalDate.now()) || 
           lastDonationDate.plusDays(90).isEqual(LocalDate.now());
}
```
