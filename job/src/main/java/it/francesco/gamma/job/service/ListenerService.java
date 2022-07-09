package it.francesco.gamma.job.service;

import it.francesco.gamma.job.exception.JobApiException;
import it.francesco.gamma.job.exception.ListenerException;
import it.francesco.gamma.job.feign.JobClient;
import it.francesco.gamma.job.model.ClientRequest;
import it.francesco.gamma.job.model.ClientResponse;
import it.francesco.gamma.job.model.JobRequest;
import it.francesco.gamma.job.model.JobResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ListenerService {

    public final RabbitTemplate rabbitTemplate;
    public final JobClient jobClient;
    public final String sendQueue;

    @Autowired
    public ListenerService(RabbitTemplate rabbitTemplate,
                           JobClient jobClient,
                           @Value("${queue.response}") String sendQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.jobClient = jobClient;
        this.sendQueue = sendQueue;
    }

    private void sendSignatureToQueue(JobResponse jobResponse) {
        log.info("Sending message to queue: {}", jobResponse);
        rabbitTemplate.convertAndSend(sendQueue, jobResponse);
    }

    private JobResponse apiCall(JobRequest jobRequest) {
        try {
            ClientResponse clientResponse = jobClient.invokeApi(jobRequest.getAuthorizationToken(), new ClientRequest(jobRequest));
            return new JobResponse(clientResponse);
        } catch (JobApiException ex) {
            log.info("Api call failed for jobRequest={}, responseCode={}", jobRequest, ex.getApiResponseCode());
            JobResponse response = new JobResponse();
            response.setIdentifier(jobRequest.getIdentifier());
            response.setSuccess(false);
            return response;
        }
    }

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",
            queues = "${queue.request}")
    public void requestsListener(JobRequest jobRequest) {

        log.info("Request received={}", jobRequest);

        try {
            JobResponse jobResponse = apiCall(jobRequest);
            log.info("Completed for identifier = {}", jobRequest.getIdentifier());
            sendSignatureToQueue(jobResponse);
        } catch (Exception ex) {
            throw new ListenerException(
                    String.format("error %s when elaborating %s: not sending ACK to rabbit",
                            ex.getMessage(), jobRequest)
            );
        }


    }

}
