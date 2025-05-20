package engine.permissions;

public enum PermissionRequestStatus {
    PENDING,
    APPROVED,
    REJECTED;

    @Override
    public String toString() {
        return switch (this) {
            case PENDING -> "Pending";
            case APPROVED -> "Approved";
            case REJECTED -> "Rejected";
        };
    }
}
