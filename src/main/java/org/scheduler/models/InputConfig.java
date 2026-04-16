package org.scheduler.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputConfig {
    @JsonProperty("system_config")
    public Map<String, Object> system_config;
    @JsonProperty("infrastructure")
    public Infrastructure infrastructure;
    @JsonProperty("tool_registry")
    public List<Tool> tool_registry;
    @JsonProperty("jobs")
    public List<Job> jobs;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Infrastructure {
        public List<String> servers;
        public List<Link> links;
    }
}
