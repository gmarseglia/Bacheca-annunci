package Model;

public class ActiveUser {
    private static Role role;

    public static Role getRole() {
        return role;
    }

    public static void setRole(Role role) {
        ActiveUser.role = role;
    }
}
