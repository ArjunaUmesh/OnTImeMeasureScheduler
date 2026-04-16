package org.scheduler.core;

import org.scheduler.models.InputConfig;
import org.scheduler.models.Job;
import org.scheduler.utils.Logger;
import java.util.*;

public class Scheduler {
    private final ConflictManager conflictManager;

    public Scheduler(ConflictManager conflictManager) {
        this.conflictManager = conflictManager;
    }

    public int run(InputConfig config, Logger logger,int mla,int binSize) {
        List<Job> jobs = new ArrayList<>(config.getJobs());
        jobs.sort((a, b) -> Integer.compare(b.getDuration(), a.getDuration()));
        if (jobs.getFirst().getDuration() > binSize) {
            logger.log(String.format("INVALID: Max Job Duration (%d) > Bin Size (%d)",
                    jobs.getFirst().getDuration(), binSize));

            return 0;
        }
        int currentTime = 0;
        int binId = 1;
        while (!jobs.isEmpty()) {
            List<Job> currentBin = new ArrayList<>();
            Job head = jobs.removeFirst();
            currentBin.add(head);
            Iterator<Job> iterator = jobs.iterator();
            while (iterator.hasNext()) {
                Job candidate = iterator.next();
                if (currentBin.size() < mla && !hasConflictWithBin(candidate, currentBin)) {
                        currentBin.add(candidate);
                        iterator.remove();
                    }
            }
            printBin(logger, binId++, currentTime, currentBin, binSize);
            currentTime += binSize;
        }
        logger.log("\nTotal execution time : "+currentTime);
        return currentTime;
    }
    private boolean hasConflictWithBin(Job candidate, List<Job> bin) {
        for (Job jobInBin : bin) {
            if (conflictManager.isJobsConflicting(candidate.getId(), jobInBin.getId())) {
                return true;
            }
        }
        return false;
    }

    private void printBin(Logger logger, int id, int start, List<Job> jobs, int binSize) {
        List<String> ids = new ArrayList<>();

        for (Job j : jobs) ids.add(j.getId());
        String output = String.format("Bin %d | Window: [%d - %d] | Jobs: %s",
                id, start, (start + binSize), ids);
        logger.log(output);
    }

}
