package org.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.scheduler.core.ConflictManager;
import org.scheduler.models.InputConfig;
import org.scheduler.models.Job;
import org.scheduler.utils.Logger;

import java.io.InputStream;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        Logger logger = new Logger("execution");
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = Main.class.getResourceAsStream("/input.json");

            InputConfig config = mapper.readValue(is, InputConfig.class);

            String fullDetail = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
            System.out.println("Full Project Configuration:");
            logger.log(fullDetail);

            ConflictManager conflictManager = new ConflictManager();
            // 3. Generate the Tool Conflict Graph
            conflictManager.generateGraphs(config);
            // 4. Print the result
            conflictManager.printToolGraph(logger);

            conflictManager.printPathRegistry(logger);

            List<Job> jobs = config.getJobs();
            // 2. Nested loop to check every unique pair of tasks
            for (int i = 0; i < jobs.size(); i++) {
                for (int j = i + 1; j < jobs.size(); j++) {
                    Job t1 = jobs.get(i);
                    Job t2 = jobs.get(j);

                    // 3. Call your hasLinkConflict method
                    boolean hasConflict = conflictManager.hasLinkConflict(
                            t1.getSource(), t1.getDestination(),
                            t2.getSource(), t2.getDestination()
                    );

                    // 4. Print the result neatly
                    String result = hasConflict ? "HAS CONFLICT" : "NO CONFLICT";
                    String output = String.format("%-20s vs \t\t%-20s -> %s",
                            t1.getId() + " (" + t1.getTool() + ")",
                            t2.getId() + " (" + t2.getTool() + ")",
                            result);

                    logger.log(output);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}