package io.ambershogun.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ambershogun.Application;
import io.ambershogun.request.entity.BuyRequest;
import io.ambershogun.request.entity.Request;
import io.ambershogun.request.entity.SellRequest;
import io.ambershogun.request.enums.RequestState;
import io.ambershogun.response.MessageResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class RequestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testPostBuyRequest() throws Exception {
        BuyRequest buyRequest = new BuyRequest(1000, "NBC");
        postRequest(buyRequest).andExpect(status().isOk());
    }

    @Test
    public void testPostSellRequest() throws Exception {
        SellRequest sellRequest = new SellRequest(1000);
        postRequest(sellRequest).andExpect(status().isOk());
    }

    @Test
    public void testProcessingTime() throws Exception {
        final int sellAmount = 1000;
        SellRequest sellRequest = new SellRequest(sellAmount);

        String idAsJson = postRequest(sellRequest).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        MessageResponse idResponse = objectMapper.readValue(idAsJson, MessageResponse.class);
        long id = Long.valueOf(idResponse.getMessage());

        String messageAsJson = getRequest(id).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        MessageResponse processingResponse = objectMapper.readValue(messageAsJson, MessageResponse.class);
        assertEquals("Request still processing...", processingResponse.getMessage());

        Thread.sleep(2000);

        String requestAsJson = getRequest(id).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        SellRequest processedRequest = objectMapper.readValue(requestAsJson, SellRequest.class);
        assertEquals(RequestState.PROCESSED, processedRequest.getState());
        assertEquals(sellAmount, processedRequest.getSellAmount());
    }

    @Test
    public void testRequestNotFound() throws Exception {
        long notExistingId = 31337265;

        String requestAsJson = getRequest(notExistingId).andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MessageResponse response = objectMapper.readValue(requestAsJson, MessageResponse.class);
        assertEquals("No request with such id were found", response.getMessage());
    }

    @Test
    public void testBuyRequestConstraints() throws Exception {
        BuyRequest buyRequest = new BuyRequest(-1, "NBC");
        postRequest(buyRequest).andExpect(status().isBadRequest());

        buyRequest = new BuyRequest(100, null);
        postRequest(buyRequest).andExpect(status().isBadRequest());

        buyRequest = new BuyRequest(100, stringWithLength(129));
        postRequest(buyRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void testSellRequestConstraints() throws Exception {
        SellRequest sellRequest = new SellRequest(-1);
        postRequest(sellRequest).andExpect(status().isBadRequest());
    }

    private ResultActions postRequest(Request request) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.request(HttpMethod.POST, "/requests");
        requestBuilder.content(objectMapper.writeValueAsString(request));
        requestBuilder.contentType(MediaType.APPLICATION_JSON);

        return mockMvc.perform(requestBuilder).andDo(print());
    }

    private ResultActions getRequest(long id) throws Exception {
        String url = String.format("/requests/%s", id);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.request(HttpMethod.GET, url);
        requestBuilder.contentType(MediaType.APPLICATION_JSON);
        return mockMvc.perform(requestBuilder).andDo(print());
    }

    private String stringWithLength(int n) {
        return new String(new char[n]).replace("\0", "1");
    }
}
