package com.atlasdblite.engine;

import com.atlasdblite.models.Node;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GraphEngineTest {

    private static final String TEST_DB_FILE = "test_atlas_data.enc";
    private GraphEngine engine;

    @BeforeMethod
    public void setup() {
        // Clean up previous runs
        deleteTestFile();
        engine = new GraphEngine(TEST_DB_FILE);
    }

    @AfterMethod
    public void tearDown() {
        // Clean up after tests
        deleteTestFile();
    }

    private void deleteTestFile() {
        try {
            Files.deleteIfExists(Paths.get(TEST_DB_FILE));
        } catch (Exception ignored) {}
    }

    @Test
    public void testAddAndGetNode() {
        Node n = new Node("u1", "User");
        n.addProperty("name", "Alice");
        
        engine.persistNode(n);
        
        Node retrieved = engine.getNode("u1");
        Assert.assertNotNull(retrieved, "Node should exist in DB");
        Assert.assertEquals(retrieved.getLabel(), "User");
        Assert.assertEquals(retrieved.getProperty("name"), "Alice");
    }

    @Test
    public void testUpdateNode() {
        Node n = new Node("u1", "User");
        engine.persistNode(n);
        
        boolean success = engine.updateNode("u1", "role", "Admin");
        Assert.assertTrue(success);
        
        Node updated = engine.getNode("u1");
        Assert.assertEquals(updated.getProperty("role"), "Admin");
    }

    @Test
    public void testDeleteNodeCascades() {
        engine.persistNode(new Node("A", "Node"));
        engine.persistNode(new Node("B", "Node"));
        engine.persistRelation("A", "B", "LINKS_TO");
        
        // Ensure setup is correct
        Assert.assertEquals(engine.getAllNodes().size(), 2);
        Assert.assertEquals(engine.getAllRelations().size(), 1);
        
        // Delete Node A
        engine.deleteNode("A");
        
        // Assertions
        Assert.assertNull(engine.getNode("A"));
        Assert.assertEquals(engine.getAllNodes().size(), 1);
        Assert.assertEquals(engine.getAllRelations().size(), 0, "Relation should be removed when node is deleted");
    }

    @Test
    public void testTraverse() {
        engine.persistNode(new Node("Source", "S"));
        engine.persistNode(new Node("Target1", "T"));
        engine.persistNode(new Node("Target2", "T"));
        
        engine.persistRelation("Source", "Target1", "CONN");
        engine.persistRelation("Source", "Target2", "CONN");
        
        List<Node> results = engine.traverse("Source", "CONN");
        Assert.assertEquals(results.size(), 2);
    }

    @Test
    public void testPersistenceReboot() {
        // 1. Create data
        engine.persistNode(new Node("p1", "PersistentNode"));
        engine.persistRelation("p1", "p1", "SELF_LOOP");
        
        // 2. "Reboot" engine (Create new instance reading same file)
        GraphEngine reloadedEngine = new GraphEngine(TEST_DB_FILE);
        
        // 3. Verify data loaded correctly
        Node n = reloadedEngine.getNode("p1");
        Assert.assertNotNull(n, "Node should persist across restarts");
        Assert.assertEquals(n.getLabel(), "PersistentNode");
        Assert.assertEquals(reloadedEngine.getAllRelations().size(), 1);
    }
}