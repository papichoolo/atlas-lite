AtlasDB-Lite Command ReferenceAtlasDB-Lite uses a UNIX-style shell syntax. Arguments are space-separated.1. add-nodeCreates a new node in the graph. Properties are optional.Syntax:add-node <id> <label> [key:value] [key:value]...Examples:# Create a User
add-node u1 User name:Alice role:Admin

# Create a Server
add-node s1 Server ip:192.168.1.50 os:Linux
2. linkCreates a directional relationship between two existing nodes.Syntax:link <source_id> <target_id> <relationship_type>Examples:# Link Alice to the Server
link u1 s1 MANAGES

# Link Server to an Incident
link s1 inc55 HAS_ALERT
3. showLists all nodes currently stored in the database memory.Syntax:show4. queryTraverses the graph from a specific starting node to find what it connects to.Syntax:query <start_node_id> <relationship_type>Examples:# Find what u1 manages
query u1 MANAGES

# Find alerts on s1
query s1 HAS_ALERT
5. helpDisplays the list of available commands inside the shell.6. exitCloses the shell and ensures the final state is saved to atlas_data.json.