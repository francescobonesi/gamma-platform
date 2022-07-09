package it.francesco.gamma.job.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class JobResponse {

    private String identifier;
    private boolean completed;

    public JobResponse(ClientResponse clientResponse) {
        this.identifier = clientResponse.getIdentifier();
        this.completed = clientResponse.isCompleted();
    }

}
