package it.francesco.gamma.job.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class JobResponse {

    private String identifier;
    private boolean success;

    public JobResponse(ClientResponse clientResponse) {
        this.identifier = clientResponse.getIdentifier();
        this.success = clientResponse.isSuccess();
    }

}
