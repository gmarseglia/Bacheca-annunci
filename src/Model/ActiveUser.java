package Model;

public class ActiveUser {
    private static String username;
    private static Role role;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        ActiveUser.username = username;
    }

    public static Role getRole() {
        return role;
    }

    public static void setRole(Role role) {
        ActiveUser.role = role;
    }
}
