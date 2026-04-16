package org.scheduler.core;

import lombok.Data;
import org.scheduler.models.*;
import org.scheduler.utils.Logger;

import java.util.*;

@Data
public class ConflictManager {
    private final Map<String, Set<String>> toolConflictGraph = new HashMap<>();
    private final Map<String, List<String>> serverPaths = new HashMap<>();
    private final Map<String, List<String>> serverAdjacency = new HashMap<>();
    private final Map<String, Set<String>> jobConflictMap = new HashMap<>();

    public void generateGraphs(InputConfig config) {
        buildServerAdjacency(config.getInfrastructure().getLinks());
        buildToolGraph(config.getTool_registry());
        buildPathRegistry(config.getInfrastructure().getServers());
        buildJobConflictMatrix(config.getJobs());
    }

    private void buildToolGraph(List<Tool> tools) {
        for (Tool tool : tools) {
            toolConflictGraph.put(tool.getName(), new HashSet<>());
            if (tool.isCpu_intensive() || tool.isChannel_intensive()) {
                toolConflictGraph.get(tool.getName()).add(tool.getName());
            }
        }

        for (int i = 0; i < tools.size(); i++) {
            for (int j = i + 1; j < tools.size(); j++) {
                Tool t1 = tools.get(i);
                Tool t2 = tools.get(j);
                if ((t1.isCpu_intensive() && t2.isCpu_intensive()) || (t1.isChannel_intensive() && t2.isChannel_intensive())) {
                    toolConflictGraph.get(t1.getName()).add(t2.getName());
                    toolConflictGraph.get(t2.getName()).add(t1.getName());
                }
            }
        }
    }

    private void buildServerAdjacency(List<Link> links) {
        serverAdjacency.clear();
        for (Link link : links) {
            serverAdjacency.computeIfAbsent(link.getFrom(), k -> new ArrayList<>()).add(link.getTo());
            serverAdjacency.computeIfAbsent(link.getTo(), k -> new ArrayList<>()).add(link.getFrom());
        }
    }

    private void buildPathRegistry(List<String> servers) {
        for (int i = 0; i < servers.size(); i++) {
            for (int j = i + 1; j < servers.size(); j++) {
                String s1 = servers.get(i);
                String s2 = servers.get(j);
                List<String> path = findShortestPathBFS(s1, s2);
                if (!path.isEmpty()) {
                    serverPaths.put(s1 + ":" + s2, path);
                    List<String> reversePath = new ArrayList<>(path);
                    Collections.reverse(reversePath);
                    serverPaths.put(s2 + ":" + s1, reversePath);
                }
            }
        }
    }

    private List<String> findShortestPathBFS(String start, String end) {
        Queue<List<String>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(start));
        Set<String> visited = new HashSet<>();
        visited.add(start);
        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String current = path.getLast();
            if (current.equals(end)) return path;
            List<String> neighbors = serverAdjacency.getOrDefault(current, Collections.emptyList());
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    List<String> nextPath = new ArrayList<>(path);
                    nextPath.add(neighbor);
                    queue.add(nextPath);
                }
            }
        }
        return Collections.emptyList();
    }

    public boolean hasLinkConflict(String start1, String end1, String start2, String end2) {
        List<String> path1 = serverPaths.get(start1 + ":" + end1);
        List<String> path2 = serverPaths.get(start2 + ":" + end2);
        if (path1 == null || path2 == null) {
            return false;
        }
        Set<String> nodesInPath2 = new HashSet<>(path2);
        for (String node : path1) {
            if (nodesInPath2.contains(node)) {
                return true;
            }
        }
        return false;
    }


    public void buildJobConflictMatrix(List<Job> jobs) {
        for (Job job : jobs) {
            jobConflictMap.put(job.getId(), new HashSet<>());
        }
        for (int i = 0; i < jobs.size(); i++) {
            for (int j = i + 1; j < jobs.size(); j++) {
                Job j1 = jobs.get(i);
                Job j2 = jobs.get(j);
                boolean toolConflict = toolConflictGraph.get(j1.getTool()).contains(j2.getTool());
                if(!toolConflict) {
                    continue;
                }
                boolean linkConflict = hasLinkConflict(
                        j1.getSource(), j1.getDestination(),
                        j2.getSource(), j2.getDestination()
                );
                if (linkConflict) {
                    jobConflictMap.get(j1.getId()).add(j2.getId());
                    jobConflictMap.get(j2.getId()).add(j1.getId());
                }
            }
        }
    }

    public boolean isJobsConflicting(String jobId1, String jobId2) {
        if (!jobConflictMap.containsKey(jobId1)) {
            return false;
        }
        return jobConflictMap.get(jobId1).contains(jobId2);
    }

    public void printPathRegistry(Logger logger) {
        logger.log("\nShortest path between servers:");
        if (serverPaths.isEmpty()) {
            return;
        }
        List<String> sortedKeys = new ArrayList<>(serverPaths.keySet());
        Collections.sort(sortedKeys);
        for (String key : sortedKeys) {
            List<String> path = serverPaths.get(key);
            String output = String.format("%-15s ->\t\t %s", key, path);
            logger.log(output);
        }
        logger.log("\n");
    }

    public void printToolGraph(Logger logger) {
        logger.log("\nTool conflict graph : ");
        if (toolConflictGraph.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Set<String>> entry : toolConflictGraph.entrySet()) {
            String tool = entry.getKey();
            Set<String> conflicts = entry.getValue();
            String output = String.format("%-20s <-->\t\t %s",
                    tool,
                    conflicts.isEmpty() ? "[]" : conflicts);

            logger.log(output);
        }
    }
}
