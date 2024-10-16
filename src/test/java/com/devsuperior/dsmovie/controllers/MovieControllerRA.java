package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class MovieControllerRA {
	
	private String movieTitle;
	private Long existingMovieId, nomExistingMovieId;
	private String clientUsername, clientPassword, adminUsername, adminPassword;	
	private String clientToken, adminToken, invalidToken;
	
	private Map<String, Object> postMovieInstance;
	
	@BeforeEach
	public void setup() throws JSONException{

		baseURI = "http://localhost:8080";
		
		movieTitle = "The Witcher";
		
		existingMovieId = 1L;
		nomExistingMovieId = 2222222L;
		
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken 	= TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		invalidToken = adminToken + "xpto";		
		
		postMovieInstance = new HashMap<>();
		postMovieInstance.put("title", "Test Movie");
		postMovieInstance.put("score", "0.0");
		postMovieInstance.put("count", "0");
		postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		
		
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		
		given()	
			.get("/movies")
		 .then()
		 	.statusCode(200);		 	
		
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {	
				
		given()	
			.get("/movies?title={movieTitle}",movieTitle)
		 .then()
		 	.statusCode(200)
			.body("content.id[0]",is(1))
			.body("content.title[0]", equalTo("The Witcher"));
							
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {	
				
		given()	
		.get("/movies/{id}",existingMovieId)
	 .then()
	 	.statusCode(200)
	 	.body("id", is(1))
	 	.body("title", equalTo("The Witcher"));
		
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {	
		given()	
		.get("/movies/{id}",nomExistingMovieId)
	 .then()
	 	.statusCode(404);
		
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {		
		
		postMovieInstance.put("title", "");
		JSONObject novoMovie = new JSONObject(postMovieInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer " + adminToken)
		.body(novoMovie)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
	.when()
		.post("/movies")
	.then()
		.statusCode(422)
		.body("errors[0].message", equalTo("Title must be between 5 and 80 characters"));
		
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		
		JSONObject novoMovie = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(novoMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(403);
		
		
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		
		JSONObject novoMovie = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + invalidToken)
			.body(novoMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/products")
		.then()
			.statusCode(401);
			
	}
}
