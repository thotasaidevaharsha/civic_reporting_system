import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Complaint {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final int id;
    private final String name;
    private final String zone;
    private final String location;
    private final String description;
    private final LocalDateTime createdAt;

    private String status;
    private String department;
    private String priority;
    private String imagePath;

    public Complaint(int id, String name, String zone, String location, String description) {
        this.id = id;
        this.name = name;
        this.zone = zone;
        this.location = location;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.status = "Submitted";
        this.department = "Awaiting Assignment";
        this.priority = "Medium";
        this.imagePath = "";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getZone() {
        return zone;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtDisplay() {
        return createdAt.format(DISPLAY_FORMAT);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath == null ? "" : imagePath;
    }

    public String getComplaintId() {
        return "COMPLAINT-" + id;
    }

    public int getPriorityWeight() {
        return switch (priority) {
            case "High" -> 0;
            case "Medium" -> 1;
            case "Low" -> 2;
            default -> 3;
        };
    }

    public int getStatusWeight() {
        return switch (status) {
            case "Submitted" -> 0;
            case "Assigned" -> 1;
            case "In Progress" -> 2;
            case "Resolved" -> 3;
            default -> 4;
        };
    }
}
