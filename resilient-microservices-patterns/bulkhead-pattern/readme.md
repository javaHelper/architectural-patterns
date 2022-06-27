# Bulkhead Patterns

<img width="900" alt="Screenshot 2022-06-10 at 8 29 17 PM" src="https://user-images.githubusercontent.com/54174687/175859820-239f9241-970d-44cf-b48c-d8262d5c6d45.png">

GET -> `http://localhost:8080/product/products`

Response:

```json
[
    {
        "productId": 2,
        "description": "The Eminem Show",
        "price": 12.12,
        "productRating": {
            "avgRating": 0.0,
            "reviews": []
        }
    },
    {
        "productId": 1,
        "description": "Blood On The Dance Floor",
        "price": 12.45,
        "productRating": {
            "avgRating": 0.0,
            "reviews": []
        }
    }
]
```

<img width="1344" alt="Screenshot 2022-06-27 at 11 24 06 AM" src="https://user-images.githubusercontent.com/54174687/175869104-81113996-a986-40ec-a6b7-9a68c4489df8.png">
