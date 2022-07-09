package it.francesco.gamma.pec.service;

import it.francesco.gamma.pec.feign.PecClient;
import it.francesco.gamma.pec.module.Message;
import it.francesco.gamma.pec.module.Pec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PecService {

    private final PecClient pecClient;

    @Autowired
    public PecService(PecClient pecClient) {
        this.pecClient = pecClient;
    }

    public List<Pec> getPecList(String serviceAuthorizationToken) {
        return pecClient.getPecList(serviceAuthorizationToken);
    }

    public List<Message> getPecMessages(String serviceAuthorizationToken, String pecId) {
        return pecClient.getMessages(serviceAuthorizationToken, pecId);
    }

}
