package com.vinsguru.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinsguru.dto.ProductDto;
import com.vinsguru.product.service.ProductService;

@RestController
@RequestMapping("product")
public class ProductController {

	@Autowired
    private ProductService productService;

    @GetMapping("product/{productId}")
    public ProductDto getProduct(@PathVariable int productId){
        return this.productService.getProductDto(productId);
    }

    @GetMapping("products")
    public List<ProductDto> getAllProducts() throws InterruptedException {
        Thread.sleep(50);
        return this.productService.getAllProducts();
    }
}
