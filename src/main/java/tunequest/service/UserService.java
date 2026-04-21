package tunequest.service;

import org.springframework.stereotype.Service;
import tunequest.entity.User;
import tunequest.repository.UserRepository;
import tunequest.validator.UserValidator;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MessagingService messagingService;

    public UserService(UserRepository userRepository, MessagingService messagingService) {
        this.userRepository = userRepository;
        this.messagingService = messagingService;
    }

    public User saveUser(User user) {
        // Validating the user before saving it
        UserValidator.validateUser(user);

        User savedUser = userRepository.save(user);
        messagingService.sendMessage("add" + user.getId());
        return savedUser;
    }

    public User getUserById(String id) {
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
        messagingService.sendMessage("del" + id);
    }
}
