public class AdminAccount {
    private final String displayName;
    private final String zone;
    private final String password;

    public AdminAccount(String displayName, String zone, String password) {
        this.displayName = displayName;
        this.zone = zone;
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getZone() {
        return zone;
    }

    public boolean matches(String zone, String password) {
        return this.zone.equalsIgnoreCase(zone) && this.password.equals(password);
    }
}
