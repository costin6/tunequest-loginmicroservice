//package tunequest.service;
//
//import jakarta.jms.Queue;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.transaction.annotation.Transactional;
//import tunequest.configuration.TestConfig;
//import tunequest.entity.User;
//import tunequest.repository.UserRepository;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//@ContextConfiguration(classes = {TestConfig.class, UserService.class})
//@Transactional // Ensures database changes are rolled back after each test
//public class UserServiceTest {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    public void testSaveUserAndSendMessage() {
//        // Arrange
//        User user = new User("testUserId", "Test User");
//
//        // Act
//        userService.saveUser(user);
//
//        // Assert: Check that the user was saved in the database
//        User savedUser = userRepository.findById("testUserId");
//        assertEquals("Test User", savedUser.getDisplayName());
//
//        // Assert: Check if a message was sent to the queue (you can add a listener mock to validate this)
//        // In this test, we only confirm no exceptions were thrown during message sending.
//    }
//}
