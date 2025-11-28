package com.atlasdblite.server;

import com.atlasdblite.engine.GraphEngine;
import com.atlasdblite.models.Node;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class APIServerTest {

    private static final int TEST_PORT = 8099;
    private static final String BASE_URL = "http://localhost:" + TEST_PORT;
    private static final String TEST_DB_FILE = "api_test_data.enc";
    
    private APIServer server;
    private GraphEngine engine;
    private HttpClient client;
    private Gson gson;

    @BeforeClass
    public void setup() throws IOException {
        // 1. Clean previous runs
        Files.deleteIfExists(Paths.get(TEST_DB_FILE));

        // 2. Start Engine & Server
        engine = new GraphEngine(TEST_DB_FILE);
        server = new APIServer(engine);
        server.start(TEST_PORT);

        // 3. Init Client
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    @AfterClass
    public void tearDown() throws IOException {
        if (server != null) {
            server.stop();
        }
        Files.deleteIfExists(Paths.get(TEST_DB_FILE));
    }

    @Test(priority = 1)
    public void testStatusEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/status"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.body().contains("AtlasDB-Lite"));
    }

    @Test(priority = 2)
    public void testCreateNode() throws Exception {
        String jsonBody = "{\"id\":\"api_u1\", \"label\":\"User\", \"props\":{\"name\":\"TestUser\"}}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/node"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(response.statusCode(), 201);
        
        // Direct Verification in Engine
        Assert.assertNotNull(engine.getNode("api_u1"));
    }

    @Test(priority = 3)
    public void testListNodes() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/nodes"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(response.statusCode(), 200);

        // Parse JSON List
        Type listType = new TypeToken<List<Node>>(){}.getType();
        List<Node> nodes = gson.fromJson(response.body(), listType);
        
        Assert.assertFalse(nodes.isEmpty());
        Assert.assertEquals(nodes.get(0).getId(), "api_u1");
    }

    @Test(priority = 4)
    public void testCreateLink() throws Exception {
        // Create second node first
        String node2 = "{\"id\":\"api_s1\", \"label\":\"Server\"}";
        client.send(HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/node"))
                .POST(HttpRequest.BodyPublishers.ofString(node2)).build(), HttpResponse.BodyHandlers.ofString());

        // Create Link
        String linkBody = "{\"from\":\"api_u1\", \"to\":\"api_s1\", \"type\":\"OWNS\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/link"))
                .POST(HttpRequest.BodyPublishers.ofString(linkBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(response.statusCode(), 201);
        
        // Verify Logic
        Assert.assertEquals(engine.getAllRelations().size(), 1);
        Assert.assertEquals(engine.getAllRelations().get(0).getType(), "OWNS");
    }

    @Test(priority = 5)
    public void testSearch() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/search?q=testuser"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        Type listType = new TypeToken<List<Node>>(){}.getType();
        List<Node> results = gson.fromJson(response.body(), listType);

        Assert.assertEquals(results.size(), 1);
        Assert.assertEquals(results.get(0).getId(), "api_u1");
    }
}