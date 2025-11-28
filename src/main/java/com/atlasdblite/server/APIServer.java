package com.atlasdblite.server;

import com.atlasdblite.engine.GraphEngine;
import com.atlasdblite.models.Node;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class APIServer {
    private final GraphEngine engine;
    private HttpServer server;
    private final Gson gson;

    public APIServer(GraphEngine engine) {
        this.engine = engine;
        this.gson = new Gson();
    }

    public void start(int port) throws IOException {
        if (server != null) return;

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        // --- Define Routes ---
        
        // GET /api/status
        server.createContext("/api/status", exchange -> 
            sendResponse(exchange, 200, "{\"status\":\"online\",\"engine\":\"AtlasDB-Lite\"}"));

        // GET /api/nodes
        server.createContext("/api/nodes", exchange -> {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String json = gson.toJson(engine.getAllNodes());
                sendResponse(exchange, 200, json);
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        });

        // POST /api/node -> Body: { "id": "u1", "label": "User" }
        server.createContext("/api/node", exchange -> {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    NodeDTO dto = parseBody(exchange, NodeDTO.class);
                    Node node = new Node(dto.id, dto.label);
                    if (dto.props != null) {
                        dto.props.forEach(node::addProperty);
                    }
                    engine.persistNode(node);
                    sendResponse(exchange, 201, "{\"message\":\"Node Created\"}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            }
        });

        // POST /api/link -> Body: { "from": "u1", "to": "s1", "type": "OWNS" }
        server.createContext("/api/link", exchange -> {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    LinkDTO dto = parseBody(exchange, LinkDTO.class);
                    engine.persistRelation(dto.from, dto.to, dto.type);
                    sendResponse(exchange, 201, "{\"message\":\"Link Created\"}");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            }
        });

        // GET /api/search?q=admin
        server.createContext("/api/search", exchange -> {
            String query = exchange.getRequestURI().getQuery(); // returns "q=admin"
            if (query != null && query.startsWith("q=")) {
                String term = query.split("=")[1].toLowerCase();
                List<Node> matches = engine.getAllNodes().stream()
                    .filter(n -> n.toString().toLowerCase().contains(term))
                    .collect(Collectors.toList());
                sendResponse(exchange, 200, gson.toJson(matches));
            } else {
                sendResponse(exchange, 400, "Missing query param 'q'");
            }
        });

        server.start();
        System.out.println(" [WEB] API Server listening on http://localhost:" + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            System.out.println(" [WEB] Server stopped.");
        }
    }

    // --- Helpers ---

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private <T> T parseBody(HttpExchange exchange, Class<T> clazz) {
        return gson.fromJson(new InputStreamReader(exchange.getRequestBody()), clazz);
    }

    // DTOs for JSON parsing
    private static class NodeDTO {
        String id;
        String label;
        Map<String, String> props;
    }

    private static class LinkDTO {
        String from;
        String to;
        String type;
    }
}