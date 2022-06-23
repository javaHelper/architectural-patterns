# Retry-Pattern

<img width="900" alt="Screenshot 2022-06-23 at 11 20 09 PM" src="https://user-images.githubusercontent.com/54174687/175362831-93e4db70-0d86-4f98-9810-b264f3d5dec4.png">

<img width="1367" alt="Screenshot 2022-06-23 at 11 19 50 PM" src="https://user-images.githubusercontent.com/54174687/175362871-582bfe22-9c7a-49c1-bc07-66e82b52edfe.png">

<img width="1363" alt="Screenshot 2022-06-23 at 11 19 31 PM" src="https://user-images.githubusercontent.com/54174687/175362877-339e8c27-69c3-4d3b-bcd6-1ee0e252ad66.png">

- rating-service

```java
@RestController
@RequestMapping("ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @GetMapping("{prodId}")
    public ResponseEntity<ProductRatingDto> getRating(@PathVariable int prodId) {
        ProductRatingDto productRatingDto = this.ratingService.getRatingForProduct(prodId);
        return this.failRandomly(productRatingDto);
    }

    private ResponseEntity<ProductRatingDto> failRandomly(ProductRatingDto productRatingDto){
        int random = ThreadLocalRandom.current().nextInt(1, 4);
        System.out.println("== Ramdom Delay :"+random);
        if(random < 2){
            return ResponseEntity.status(500).build();
        }else if(random < 3){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(productRatingDto);
    }
}
```

- product-service

```properties
resilience4j.retry:
  instances:
    ratingService:
      max-attempts: 3
      waitDuration: 5s
      retryExceptions:
        - org.springframework.web.client.HttpServerErrorException
      ignoreExceptions:
        - org.springframework.web.client.HttpClientErrorException
    someOtherService:
      max-attempts: 3
      waitDuration: 10s
      retryExceptions:
        - org.springframework.web.client.HttpServerErrorException
        - java.io.IOException
---
rating:
  service:
    endpoint: http://localhost:7070/ratings/
```

Java code:

```java
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
```
