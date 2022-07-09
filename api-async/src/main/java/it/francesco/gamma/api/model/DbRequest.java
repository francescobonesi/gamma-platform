package it.francesco.gamma.api.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@ToString
@Table(name = "requests")
@Entity
public class DbRequest {

    @Id
    private String identifier;
    private RequestStatus status;
    private String tenant;
    private String username;
    private String documentIdList;

}
