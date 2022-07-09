package it.francesco.gamma.pec.module;

import lombok.Data;

import java.util.List;

@Data
public class Message {
    private String id;
    private String text;
    private List<String> documentIds;
}
