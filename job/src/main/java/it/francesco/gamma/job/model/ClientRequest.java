package it.francesco.gamma.job.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ClientRequest {

    private String identifier;
    private String tenant;
    private String username;
    private List<String> documentIdList;

    public ClientRequest(JobRequest jobRequest) {
        this.identifier = jobRequest.getIdentifier();
        this.tenant = jobRequest.getTenant();
        this.username = jobRequest.getUsername();
        this.documentIdList = jobRequest.getDocumentIdList();
    }

}
