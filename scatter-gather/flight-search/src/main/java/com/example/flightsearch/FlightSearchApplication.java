package com.example.flightsearch;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.nats.client.Connection;
import io.nats.client.Nats;

@SpringBootApplication
public class FlightSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightSearchApplication.class, args);
	}
	
	@Bean
    public Connection nats(@Value("${nats.server}") String natsServer) throws IOException, InterruptedException {
        return Nats.connect(natsServer);
    }
}
