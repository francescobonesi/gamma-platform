package it.francesco.gamma.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {

    private String identifier;
    private String tenant;
    private String username;
    private List<String> documentIdList;
    private String authorizationToken;

}
