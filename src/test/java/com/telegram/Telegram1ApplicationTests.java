package com.telegram;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.telegram.security.service.UserDetailsImpl;

import java.lang.instrument.Instrumentation;
@SpringBootTest
class Telegram1ApplicationTests {

	@Autowired
	@Qualifier("Host")
	WebClient webClient;
	@Test
	void contextLoads() throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			System.out.println(i);
			Thread thread = new Thread(){
			    public void run(){
			    	String resultOrdered = webClient.get()
							.uri(uriBuilder -> uriBuilder
									.path("/api/test/2")
									.queryParam("name",currentThread().getName())
									.build()
							)
							.retrieve()
							.bodyToMono(String.class).blockOptional().get();
			    	System.out.println(resultOrdered);
			    }
			};
			thread.start();
			Thread.sleep(100);
			
		}
	}
	
    
}
