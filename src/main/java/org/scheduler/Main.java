package org.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.scheduler.core.ConflictManager;
import org.scheduler.core.Scheduler;
import org.scheduler.models.InputConfig;
import org.scheduler.utils.Logger;

import java.io.InputStream;


public class Main {
    public static void main(String[] args) {
        Logger logger = new Logger("execution");
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = Main.class.getResourceAsStream("/input.json");
            InputConfig config = mapper.readValue(is, InputConfig.class);
            String executionMode = config.getSystem_config().get("execution_mode").toString();
            ConflictManager conflictManager = new ConflictManager();
            conflictManager.generateGraphs(config);
            Scheduler scheduler = new Scheduler(conflictManager);
            int mla = (int) config.getSystem_config().get("mla");
            int binSize = (int) config.getSystem_config().get("bin_size");
            logger.log("\nInput Data : ");
            logger.log(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config));
            conflictManager.printToolGraph(logger);
            conflictManager.printPathRegistry(logger);
            scheduler.run(config,logger,mla,binSize);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}