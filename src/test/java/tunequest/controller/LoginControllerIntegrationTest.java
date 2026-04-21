package tunequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.applicationinsights.core.dependencies.http.HttpResponse;
import com.microsoft.applicationinsights.core.dependencies.http.client.HttpClient;
import com.microsoft.applicationinsights.core.dependencies.http.client.methods.HttpPost;
import com.microsoft.applicationinsights.core.dependencies.http.entity.StringEntity;
import com.microsoft.applicationinsights.core.dependencies.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tunequest.entity.User;
import tunequest.service.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private LoginController loginController;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void refreshAccessToken_ShouldReturnTokens_WhenSuccessful() throws Exception {
        Map<String, String> requestBody = Map.of("refreshToken", "mock-refresh-token");

        // Mock the Spotify API response directly
        String spotifyApiResponse = """
        {
            "access_token": "mock-access-token",
            "expires_in": 3600
        }
        """;

        // Mocking HTTP Client behavior
        HttpClient mockHttpClient = Mockito.mock(CloseableHttpClient.class);
        HttpResponse mockHttpResponse = Mockito.mock(HttpResponse.class);

        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity(spotifyApiResponse));
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);

        // Inject the mocked HttpClient into the controller
        ReflectionTestUtils.setField(loginController, "httpClient", mockHttpClient);

        mockMvc.perform(post("/spotifylogin/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }


    @Test
    void login_ShouldReturnTokensAndSaveUser_WhenSuccessful() throws Exception {
        Map<String, String> requestBody = Map.of("code", "mock-auth-code");

        User user = new User("123", "mock-display-name");
        when(userService.getUserById("123")).thenReturn(null);
        when(userService.saveUser(any(User.class))).thenReturn(user);

        // Mock response for access token retrieval
        Map<String, Object> mockResponse = Map.of(
                "accessToken", "mock-access-token",
                "refreshToken", "mock-refresh-token",
                "expiresIn", 3600
        );

        // Mocking service interaction
        when(userService.saveUser(user)).thenReturn(user);

        mockMvc.perform(post("/spotifylogin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    void deleteUser_ShouldDeleteUserAndReturnSuccess_WhenValidAccessToken() throws Exception {
        // Mocking service interaction
        Mockito.doNothing().when(userService).deleteUser(eq("123"));

        mockMvc.perform(delete("/spotifylogin/delete")
                        .header("Authorization", "Bearer mock-access-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("User data deleted successfully."));
    }

    @Test
    void deleteUser_ShouldReturnError_WhenExceptionOccurs() throws Exception {
        Mockito.doThrow(new RuntimeException("User not found"))
                .when(userService).deleteUser(eq("123"));

        mockMvc.perform(delete("/spotifylogin/delete")
                        .header("Authorization", "Bearer mock-access-token"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error deleting user data: User not found"));
    }
}
