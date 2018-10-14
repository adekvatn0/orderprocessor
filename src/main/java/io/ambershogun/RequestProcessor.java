package io.ambershogun;

import io.ambershogun.request.entity.Request;
import io.ambershogun.request.enums.RequestState;
import io.ambershogun.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RequestProcessor {

    private final RequestRepository requestRepository;

    @Autowired
    public RequestProcessor(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public void receiveMessage(Request request) {
        try {
            request = processRequest(request);
            request.setState(RequestState.PROCESSED);
        } catch (Exception e) {
            request.setState(RequestState.ERROR);
        }

        requestRepository.save(request);
    }

    private Request processRequest(Request request) throws Exception {
        Thread.sleep(1000);
        return request;
    }
}
