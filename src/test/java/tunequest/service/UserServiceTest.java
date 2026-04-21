package tunequest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tunequest.entity.User;
import tunequest.repository.UserRepository;
import tunequest.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUser_ShouldReturnSavedUser() {
        // Arrange
        User user = new User("123", "Test User");
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.saveUser(user);

        // Assert
        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("Test User", result.getDisplayName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        User user = new User("123", "Test User");
        when(userRepository.findById("123")).thenReturn(user);

        // Act
        User result = userService.getUserById("123");

        // Assert
        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("Test User", result.getDisplayName());
        verify(userRepository, times(1)).findById("123");
    }

    @Test
    void getUserById_ShouldReturnNull_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById("123")).thenThrow(new RuntimeException("User not found"));

        // Act
        User result = userService.getUserById("123");

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findById("123");
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        User user1 = new User("123", "User 1");
        User user2 = new User("456", "User 2");
        List<User> userList = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("123", result.get(0).getId());
        assertEquals("456", result.get(1).getId());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUser_ShouldCallDeleteById() {
        // Act
        userService.deleteUser("123");

        // Assert
        verify(userRepository, times(1)).deleteById("123");
    }

    @Test
    public void testSaveUserAndSendMessage() {
        // Arrange
        User user = new User("testUserId", "Test User");

        // Act
        userService.saveUser(user);

        // Assert: Check that the user was saved in the database
        User savedUser = userRepository.findById("testUserId");
        assertEquals("Test User", savedUser.getDisplayName());

        // Assert: Check if a message was sent to the queue (you can add a listener mock to validate this)
        // In this test, we only confirm no exceptions were thrown during message sending.
    }
}
