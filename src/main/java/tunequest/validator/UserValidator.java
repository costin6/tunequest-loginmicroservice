package tunequest.validator;

import tunequest.entity.User;

public class UserValidator {

    public static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Validate userId
        if (user.getId() == null || user.getId().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        // Validate displayName
        if (user.getDisplayName() == null || user.getDisplayName().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be null or empty");
        }
        if (user.getDisplayName().length() > 30) {
            throw new IllegalArgumentException("Display name cannot exceed 30 characters");
        }
    }
}
