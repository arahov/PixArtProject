package Selenium1;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class ApiTest {

    @Test
    public void testApi() {
        // Set the base URI for REST Assured
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        // 1. Get a random user (userID) and print out their email address to the console
        Response userResponse = given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        // Extract a random user
        int userID = userResponse.jsonPath().getInt("id[0]");
        String email = userResponse.jsonPath().getString("email[0]");
        
        // Print the email address to the console
        System.out.println("User Email: " + email);

        // 2. Using this userID, get the user’s associated posts and verify they contain valid Post IDs
        Response postsResponse = given()
                .when()
                .get("/posts?userId=" + userID)
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Verify that post IDs are valid (between 1 and 100)
        List<Integer> postIds = postsResponse.jsonPath().getList("id");
        for (Integer postId : postIds) {
            assertThat("Post ID is valid", postId, allOf(greaterThanOrEqualTo(1), lessThanOrEqualTo(100)));
        }

        // 3. Create a post using the same userID with a non-empty title and body, and verify the correct response is returned
        String title = "Sample Title";
        String body = "Sample Body";

        Response createPostResponse = given()
                .header("Content-Type", "application/json")
                .body("{ \"title\": \"" + title + "\", \"body\": \"" + body + "\", \"userId\": " + userID + " }")
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .extract()
                .response();

        // Verify the response
        assertThat(createPostResponse.jsonPath().getString("title"), equalTo(title));
        assertThat(createPostResponse.jsonPath().getString("body"), equalTo(body));
        assertThat(createPostResponse.jsonPath().getInt("userId"), equalTo(userID));
    }
}
