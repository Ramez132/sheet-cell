package engine.permissions;

public enum PermissionLevel {
    NONE,
    READER,
    WRITER,
    OWNER;

    @Override
    public String toString() {
        return switch (this) {
            case READER -> "Reader";
            case WRITER -> "Writer";
            case OWNER -> "Owner";
            default -> "None";
        };
    }

    public String getPermissionLevel() {
        return this.toString();
    }
}
