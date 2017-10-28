package com.isgr8;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.SimpleReportAggregator;
import guru.nidi.ramltester.junit.RamlMatchers;
import guru.nidi.ramltester.restassured.RestAssuredClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestApiTest {

    private static final String API_BASE_URI = "http://localhost/api/v1";

    private static SimpleReportAggregator aggregator = new SimpleReportAggregator();

    private static RamlDefinition api;

    @LocalServerPort
    private int randomServerPort;

    private RequestSpecification client;

    private RestAssuredClient restAssured;

    @Before
    public void setUp() {
        if (api == null) {
            api = RamlLoaders.fromClasspath().load("musicplayer-api.raml").assumingBaseUri(API_BASE_URI);
            testSchemaValid();
        }
        restAssured = api.createRestAssured();
        client = restAssured.given().baseUri(API_BASE_URI).port(randomServerPort);
    }

    private void testSchemaValid() {
        Assert.assertThat(api.validate(), RamlMatchers.validates());
    }

    @Test
    public void testGetCurrentSong() {
        Response response = client.get("/songs/current").andReturn();
        Assert.assertTrue(restAssured.getLastReport().toString(), restAssured.getLastReport().isEmpty());
        response.body().print();
    }

    @Test
    public void testGetStatus() {
        Response response = client.get("/status").andReturn();
        Assert.assertTrue(restAssured.getLastReport().toString(), restAssured.getLastReport().isEmpty());
        response.body().print();
    }


}
