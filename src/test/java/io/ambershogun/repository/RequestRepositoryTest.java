package io.ambershogun.repository;

import io.ambershogun.request.entity.BuyRequest;
import io.ambershogun.request.entity.SellRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@AutoConfigureTestDatabase
@DataJpaTest
@RunWith(SpringRunner.class)
public class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Test
    public void testSaveAndGetRequests() {
        final int sellAmount = 150;
        long id = requestRepository.save(new SellRequest(sellAmount)).getId();
        SellRequest sellRequest = (SellRequest) requestRepository.findById(id).get();
        assertEquals(sellAmount, sellRequest.getSellAmount());

        final int buyAmount = 300;
        final String stockName = "STOCK_NAME";
        long id1 = requestRepository.save(new BuyRequest(buyAmount, stockName)).getId();
        BuyRequest buyRequest = (BuyRequest) requestRepository.findById(id1).get();
        assertEquals(buyAmount, buyRequest.getBuyAmount());
        assertEquals(stockName, buyRequest.getStockName());
    }
}
