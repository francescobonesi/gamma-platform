package it.francesco.gamma.job.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class JobRequest {

    private String identifier;
    private String tenant;
    private String username;
    private List<String> documentIdList;
    private String authorizationToken;

}
