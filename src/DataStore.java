import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DataStore {
    public static final List<String> ZONES = List.of(
            "North Zone",
            "South Zone",
            "East Zone",
            "West Zone",
            "Central Zone"
    );

    public static final List<String> DEPARTMENTS = List.of(
            "Sanitation Department",
            "Roads and Transport",
            "Water Board",
            "Electricity and Lighting",
            "Drainage Department",
            "Public Health Wing"
    );

    public static final List<String> PRIORITIES = List.of("High", "Medium", "Low");
    public static final List<String> STATUSES = List.of("Submitted", "Assigned", "In Progress", "Resolved");

    private static final List<Complaint> complaints = new ArrayList<>();
    private static final List<AdminAccount> adminAccounts = List.of(
            new AdminAccount("Aarav Nair", "North Zone", "north@123"),
            new AdminAccount("Meera Shah", "South Zone", "south@123"),
            new AdminAccount("Rohan Verma", "East Zone", "east@123"),
            new AdminAccount("Sana Khan", "West Zone", "west@123"),
            new AdminAccount("Admin Control", "Central Zone", "central@123")
    );

    private static int idCounter = 1000;

    private DataStore() {
    }

    public static synchronized int generateId() {
        return ++idCounter;
    }

    public static synchronized void addComplaint(Complaint complaint) {
        complaints.add(complaint);
    }

    public static synchronized Complaint findComplaint(int id) {
        for (Complaint complaint : complaints) {
            if (complaint.getId() == id) {
                return complaint;
            }
        }
        return null;
    }

    public static synchronized List<Complaint> getComplaints() {
        return new ArrayList<>(complaints);
    }

    public static synchronized List<Complaint> getSortedComplaints() {
        List<Complaint> sorted = new ArrayList<>(complaints);
        sorted.sort(Comparator
                .comparingInt(Complaint::getPriorityWeight)
                .thenComparingInt(Complaint::getStatusWeight)
                .thenComparing(Complaint::getCreatedAt));
        return sorted;
    }

    public static AdminAccount authenticateAdmin(String zone, String password) {
        for (AdminAccount account : adminAccounts) {
            if (account.matches(zone, password)) {
                return account;
            }
        }
        return null;
    }
}
