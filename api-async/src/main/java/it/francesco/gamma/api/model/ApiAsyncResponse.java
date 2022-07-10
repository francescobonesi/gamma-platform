package it.francesco.gamma.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApiAsyncResponse {

    private String identifier;
    private String tenant;
    private String username;
    private List<String> documentIdList;
    private RequestStatus status;

    public ApiAsyncResponse(DbRequest dbElement) {
        this.identifier = dbElement.getIdentifier();
        this.tenant = dbElement.getTenant();
        this.username = dbElement.getUsername();
        this.documentIdList = Arrays.asList(dbElement.getDocumentIdList().split(","));
        this.status = dbElement.getStatus();
    }

}
