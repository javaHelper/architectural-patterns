package com.vinsguru.product.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vinsguru.dto.ProductRatingDto;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;

@Service
public class RatingServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${rating.service.endpoint}")
    private String ratingService;

    
    @Bulkhead(name = "ratingService", fallbackMethod = "getDefault")
    public ProductRatingDto getProductRatingDto(int productId){
        return this.restTemplate.getForEntity(this.ratingService + productId, ProductRatingDto.class)
                .getBody();
    }

    public ProductRatingDto getDefault(int productId, Throwable throwable){
        return ProductRatingDto.of(0, Collections.emptyList());
    }
}
