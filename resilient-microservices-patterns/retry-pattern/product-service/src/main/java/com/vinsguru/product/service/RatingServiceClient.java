package com.vinsguru.product.service;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.vinsguru.dto.ProductRatingDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class RatingServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${rating.service.endpoint}")
    private String ratingService;

    
    @CircuitBreaker(name = "ratingService", fallbackMethod = "getDefault")
    @Retry(name = "ratingService", fallbackMethod = "getDefault")
    public CompletionStage<ProductRatingDto> getProductRatingDto(int productId){
        Supplier<ProductRatingDto> supplier = () ->
            this.restTemplate.getForEntity(this.ratingService + productId, ProductRatingDto.class)
                    .getBody();
        return CompletableFuture.supplyAsync(supplier);
    }

    private CompletionStage<ProductRatingDto> getDefault(int productId, HttpClientErrorException throwable){
    	System.out.println("Fallback called");
        return CompletableFuture.supplyAsync(() -> ProductRatingDto.of(0, Collections.emptyList()));
    }

}
