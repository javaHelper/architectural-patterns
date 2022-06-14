package com.example.flightsearch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.flightsearch.model.FlightSearchRequest;
import com.example.flightsearch.model.FlightSearchResponse;
import com.example.flightsearch.service.ScatterGatherService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("flight")
public class FlightSearchController {

	@Autowired
	private ScatterGatherService service;

	@GetMapping("/{from}/{to}")
	public Mono<FlightSearchResponse> search(@PathVariable String from, @PathVariable String to) {
		return this.service.broadcast(FlightSearchRequest.of(from, to));
	}
}