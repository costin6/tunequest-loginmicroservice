package tunequest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.github.cdimascio.dotenv.Dotenv;
import tunequest.entity.User;
import tunequest.service.UserService;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/spotifylogin")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class LoginController {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String CLIENT_ID = dotenv.get("CLIENT_ID");
    private static final String CLIENT_SECRET = dotenv.get("CLIENT_SECRET");
    private static final String REDIRECT_URI = dotenv.get("REDIRECT_URI");

    private final UserService userService;

        public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()));

            StringEntity entity = new StringEntity(
                    "grant_type=authorization_code&code=" + code + "&redirect_uri=" + REDIRECT_URI);
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            String jsonResponse = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(jsonResponse);

            // Parse Spotify token response
            String accessToken = responseJson.get("access_token").asText();
            String refreshToken = responseJson.get("refresh_token").asText();
            int expiresIn = responseJson.get("expires_in").asInt();

            // Fetch Spotify user details
            Map<String, String> userDetails = getSpotifyUserDetails(accessToken);
            String spotifyUserId = userDetails.get("id");
            String displayName = userDetails.get("displayName");

            // Save or update user in the database
            User user = new User(spotifyUserId, displayName);
            User dbUser = userService.getUserById(spotifyUserId);
            if (dbUser == null) {
                userService.saveUser(user);
            }

            // Set HttpOnly cookie with the access token
            ResponseCookie authCookie = ResponseCookie.from("authToken", accessToken)
                    .httpOnly(true) // Prevent JavaScript access
                    .secure(false) // Set to true in production with HTTPS
                    .path("/") // Cookie accessible throughout the site
                    .maxAge(expiresIn) // Expiration time in seconds
                    .sameSite("Strict") // Protect against CSRF
                    .build();

            // Prepare response body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("refreshToken", refreshToken);
            responseBody.put("expiresIn", expiresIn);

            return ResponseEntity.ok()
                    .header("Set-Cookie", authCookie.toString()) // Add cookie to response
                    .body(responseBody);
        } catch (Exception e) {
            // Handle and log errors
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error logging in");
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

        @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshAccessToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()));

            StringEntity entity = new StringEntity("grant_type=refresh_token&refresh_token=" + refreshToken);
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            String jsonResponse = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(jsonResponse);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("accessToken", responseJson.get("access_token").asText());
            responseBody.put("expiresIn", responseJson.get("expires_in").asInt());

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error refreshing token");
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

//        @DeleteMapping("/delete")
//    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String authHeader) {
//        try {
//            String accessToken = authHeader.replace("Bearer ", "").trim();
//
//            Map<String, String> userDetails = getSpotifyUserDetails(accessToken);
//            String spotifyUserId = userDetails.get("id");
//
//            userService.deleteUser(spotifyUserId);
//
//            return ResponseEntity.ok("User data deleted successfully.");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error deleting user data: " + e.getMessage());
//        }
//    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@CookieValue("authToken") String accessToken) {
        try {
            // Fetch Spotify user details using the token
            Map<String, String> userDetails = getSpotifyUserDetails(accessToken);
            String spotifyUserId = userDetails.get("id");

            // Delete the user from the database
            userService.deleteUser(spotifyUserId);

            return ResponseEntity.ok("User data deleted successfully.");
        } catch (Exception e) {
            // Log the error and return a failure response
            System.err.println("Error deleting user data: " + e.getMessage());
            return ResponseEntity.status(500).body("Error deleting user data: " + e.getMessage());
        }
    }


    private Map<String, String> getSpotifyUserDetails(String accessToken) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet("https://api.spotify.com/v1/me");
            get.setHeader("Authorization", "Bearer " + accessToken);

            HttpResponse response = client.execute(get);
            String jsonResponse = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(jsonResponse);

            Map<String, String> userDetails = new HashMap<>();
            userDetails.put("id", responseJson.get("id").asText());
            userDetails.put("displayName", responseJson.get("display_name").asText());

            return userDetails;
        }
    }

}
