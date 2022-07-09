package it.francesco.gamma.job.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClientResponse {
    private String identifier;
    private boolean completed;
}
