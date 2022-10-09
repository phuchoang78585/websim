package com.telegram.service.twoline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.telegram.dto.twoline.Order;
import com.telegram.dto.twoline.OrderCreate;

import reactor.core.publisher.Mono;

@Service
public class TwondLineServiceImpl implements TwondLineService{
	
	@Autowired
	@Qualifier("TwondLine")
	WebClient webClient;
    
	@Override
	public Mono<Order> getOrderCheck(int idOrdered,String apiKey) {
		// bắt lỗi ở đây.
		//String apiGetOrederCheck = "/ordercheck?apikey="+apiKey+"&id="+idOrdered;
		Mono<Order> resultOrdered = webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/ordercheck")
						.queryParam("apikey", apiKey)
						.queryParam("id", idOrdered)
						.build()
				)
				.retrieve()
				.bodyToMono(Order.class);
		return resultOrdered;
	}

	@Override
	public Mono<OrderCreate> createOrder(String apiKey) {
		Mono<OrderCreate> resultOrdered = webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/order")
						.queryParam("apikey", apiKey)
						.queryParam("serviceId",269)
						.queryParam("allowVoiceSms", false)
						.build()
						
				)
				.retrieve()
				.bodyToMono(OrderCreate.class);
		return resultOrdered;
	}

	@Override
	public Mono<String> delay(){
		Mono<String> tweetFlux = WebClient.create()
			      .get()
			      .uri("http://localhost:8080/abc")
			      .retrieve()
			      .bodyToMono(String.class);
		return tweetFlux;
	}

	@Override
	public Order buyPhone(String apiKey) throws InterruptedException {
		for (int i = 0; i < 5; i++) {
			OrderCreate buy2ndline = createOrder(apiKey).block();
			if (buy2ndline != null && buy2ndline.getStatus() == 1)
            {
                int waitCode = 0;
                do
                {
                    Order orderCode = getOrderCheck(buy2ndline.getId(), apiKey).block();
                    if (orderCode.getData().getStatusCode() == 0 && orderCode.getData().getPhone() != null)
                    {
                        return orderCode;
                    }
                    Thread.sleep(1000);
                    waitCode++;
                } while (waitCode < 20);
            }
		}
		return null;
	}
}
