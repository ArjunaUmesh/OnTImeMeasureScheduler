package org.scheduler.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job {
    @JsonProperty("id")
    public String id;
    @JsonProperty("source")
    public String source;
    @JsonProperty("destination")
    public String destination;
    @JsonProperty("tool")
    public String tool;
    @JsonProperty("duration")
    public int duration;
}
