# Timeout Pattern

<img width="880" alt="Screenshot 2022-06-23 at 11 00 39 PM" src="https://user-images.githubusercontent.com/54174687/175360069-4ae2e446-3fc6-4329-95ae-4b0f169c5571.png">

<img width="1365" alt="Screenshot 2022-06-23 at 10 59 32 PM" src="https://user-images.githubusercontent.com/54174687/175360105-11ed1778-4fab-4783-bca1-61ce565141ef.png">

<img width="1368" alt="Screenshot 2022-06-23 at 10 59 55 PM" src="https://user-images.githubusercontent.com/54174687/175360096-2dcc5134-ee38-4282-90dc-1bb8c482299f.png">

- rating-service

```java
@RestController
@RequestMapping("ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @GetMapping("{prodId}")
    public ProductRatingDto getRating(@PathVariable int prodId) throws InterruptedException {
    	int randomDelay = ThreadLocalRandom.current().nextInt(10, 5000);
    	System.out.println("== Random Delay :"+randomDelay);
    	
    	Thread.sleep(randomDelay);
        return this.ratingService.getRatingForProduct(prodId);
    }
}
```

- product-service

```properties
resilience4j.timelimiter:
  instances:
    ratingService:
      timeoutDuration: 2s
      cancelRunningFuture: true
    someOtherService:
      timeoutDuration: 1s
      cancelRunningFuture: false
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

    
    @TimeLimiter(name = "ratingService", fallbackMethod = "getDefault")
    public CompletionStage<ProductRatingDto> getProductRatingDto(int productId){
        Supplier<ProductRatingDto> supplier = () ->
            this.restTemplate.getForEntity(this.ratingService + productId, ProductRatingDto.class)
                    .getBody();
        return CompletableFuture.supplyAsync(supplier);
    }

    private CompletionStage<ProductRatingDto> getDefault(int productId, Throwable throwable){
    	System.out.println("Fallback called");
        return CompletableFuture.supplyAsync(() -> ProductRatingDto.of(0, Collections.emptyList()));
    }
}
```

