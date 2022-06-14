package com.example.flightsearch.service;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.flightsearch.model.FlightSchedule;
import com.example.flightsearch.model.FlightSearchRequest;
import com.example.flightsearch.model.FlightSearchResponse;
import com.example.flightsearch.util.ObjectUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;

@Service
public class ScatterGatherService {

	@Autowired
	private Connection nats;

	public Mono<FlightSearchResponse> broadcast(FlightSearchRequest flightSearchRequest){
		// create inbox
		String inbox = nats.createInbox();
		Subscription subscription = nats.subscribe(inbox);
		return Flux.generate((SynchronousSink<FlightSchedule[]> fluxSink) -> receiveSchedules(fluxSink, subscription))
				.flatMap(Flux::fromArray)
				.bufferTimeout(5, Duration.ofSeconds(1))
				.map(list -> {
					list.sort(Comparator.comparing(FlightSchedule::getPrice));
					return list;
				})
				.map(list -> FlightSearchResponse.of(flightSearchRequest, list))
				.next()
				.doFirst(() -> nats.publish("flight.search", inbox, ObjectUtil.toBytes(flightSearchRequest)))
				.doOnNext(i -> subscription.unsubscribe());
	}

	private void receiveSchedules(SynchronousSink<FlightSchedule[]> synchronousSink, Subscription subscription){
		try{
			Message message = subscription.nextMessage(Duration.ofSeconds(1));
			Optional<FlightSchedule[]> flightSchedules = ObjectUtil.toObject(message.getData(), FlightSchedule[].class);
			flightSchedules.ifPresent(synchronousSink::next);
		}catch (Exception e){
			synchronousSink.error(e);
		}
	}
}
