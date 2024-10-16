package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class ScoreControllerRA {
	
	private Long nomExistingMovieId;
	private String clientUsername, clientPassword, adminUsername, adminPassword;	
	private String clientToken, adminToken, invalidToken;
	
	private Map<String, Object> putScoreInstance;
	
	@BeforeEach
	public void setup() throws JSONException {
		
		nomExistingMovieId = 2222222L;
		
		putScoreInstance.put("movieId", "1");
		putScoreInstance.put("score", "4");
		
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken 	= TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		invalidToken = adminToken + "xpto";		
		
			
	}
	
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {	
		putScoreInstance.put("movieId", nomExistingMovieId);
		JSONObject putnovoScore = new JSONObject(putScoreInstance);
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.body(putnovoScore)
		.when()
		.put("/scores")
		.then()
			.statusCode(404)
			.body("error", equalTo("Recurso não encontrado"))
			.body("status", equalTo(404));
		
		
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		putScoreInstance.put("movieId", "");
		JSONObject putnovoScore = new JSONObject(putScoreInstance);
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.body(putnovoScore)
		.when()
		.put("/scores")
		.then()
			.statusCode(422)
			.body("error", equalTo("Dados inválidos"))
			.body("status", equalTo(422));
		
		
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {	
		
		putScoreInstance.put("movieId", "");
		JSONObject putnovoScore = new JSONObject(putScoreInstance);
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.body(putnovoScore)
		.when()
		.put("/scores")
		.then()
			.statusCode(422)
			.body("error", equalTo("Dados inválidos"))
			.body("status", equalTo(422))
			.body("errors.message[0]", equalTo("Score should be greater than or equal to zero"));
	}
}
