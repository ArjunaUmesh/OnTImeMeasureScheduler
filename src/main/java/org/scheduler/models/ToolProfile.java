package org.scheduler.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolProfile {
    public String name;
    @JsonProperty("cpu_intensive")
    public boolean cpu_intensive;
    @JsonProperty("channel_intensive")
    public boolean channel_intensive;
}
