import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

import java.util.HashMap;
import java.util.Map;

public class UserManagementApiTest {
	private String baseUrl = "https://reqres.in";
	private int userId;
	private Map<String, String> userData = new HashMap<>();

	@BeforeClass
	public void setup() {
		RestAssured.baseURI = baseUrl;
		userData.put("name", "John Doe");
		userData.put("job", "Software Engineer");
	}

	@Test(priority = 1)
	public void testCreateUser() {
		Response response = given().contentType(ContentType.JSON).body(userData).when().post("/api/users").then()
				.statusCode(201).extract().response();

		userId = response.jsonPath().getInt("id");
		Assert.assertNotNull(userId, "User ID should not be null");
		System.out.println("User created with ID: " + userId);
	}

	@Test(priority = 2, dependsOnMethods = "testCreateUser")
	public void testGetUser() {
		Response response = given().when().get("/api/users/" + userId).then().statusCode(200).extract().response();

		String name = response.jsonPath().getString("data.first_name");
		Assert.assertEquals(name, userData.get("name"), "User name should match");
		System.out.println("Retrieved user: " + name);
	}

	@Test(priority = 3, dependsOnMethods = "testGetUser")
	public void testUpdateUser() {
		userData.put("job", "QA Lead");

		Response response = given().contentType(ContentType.JSON).body(userData).when().put("/api/users/" + userId)
				.then().statusCode(200).extract().response();

		String updatedJob = response.jsonPath().getString("job");
		Assert.assertEquals(updatedJob, "QA Lead", "Job title should be updated");
		System.out.println("User updated to job: " + updatedJob);
	}

	@Test(priority = 4)
	public void testErrorHandling() {
		Response response = given().when().get("/api/users/99999").then().statusCode(404).extract().response();

		Assert.assertTrue(response.asString().isEmpty(), "Response should be empty for non-existing user");
		System.out.println("Handled error for invalid user request.");
	}
}
