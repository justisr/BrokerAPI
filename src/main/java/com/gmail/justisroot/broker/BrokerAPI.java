/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class BrokerAPI {

	private static BrokerAPI instance;

	private final BrokerEventService eventService = new BrokerEventService();
	private final Config config;

	BrokerAPI(Config config) {
		this.config = config;
		instance = this;
	}

	/**
	 * Get the active instance of BrokerAPI.
	 *
	 * @return the active instance of BrokerAPI
	 */
	public static final BrokerAPI instance() {
		return instance;
	}

	final BrokerEventService eventService() {
		return instance.eventService;
	}

	private final Map<String, PrioritizedBroker<?, ?>> brokers = new HashMap<>();
	private final SimilarMap similar = new SimilarMap();

	Map<Class<?>, SimilarBrokers<?>> brokers() {
		return similar.rawMap();
	}

	/**
	 * Register the provided Broker implementation.
	 *
	 * @param <T> the type of Object that this Broker transacts
	 * @param broker the Broker instance to register
	 * @return true if registration was successful, false if the implementation was invalid or the provider or instance was already registered
	 */
	public final <T> boolean register(Broker<T> broker) {
		if (broker == null || broker.getId() == null || broker.getId().isEmpty() || broker.getId().contains(" ") || brokers.containsKey(broker.getId())) return false;
		PrioritizedBroker<T, ?> entry = new PrioritizedBroker<>(broker, config.getPriority(broker));
		if (similar.contains(entry)) return false;
		brokers.put(broker.getId(), entry);
		similar.add(entry);
		eventService.createRegistrationEvent(broker);
		return true;
	}

	/**
	 * Unregister the provided Broker implementation.
	 *
	 * @param broker the Broker instance to unregister
	 * @return true if the Broker instance was successfully unregistered, false if the instance wasn't registered to begin with
	 */
	public final boolean unregister(Broker<?> broker) {
		return unregister(broker.getId());
	}

	/**
	 * Unregister a Broker instance by name.
	 *
	 * @param brokerName the name of the Broker instance to unregister
	 * @return true if the Broker was successfully unregistered, false if it wasn't registered to begin with
	 */
	public final boolean unregister(String brokerName) {
		if (brokerName == null || !brokers.containsKey(brokerName)) return false;
		PrioritizedBroker<?, ?> entry = brokers.remove(brokerName);
		boolean removed = similar.remove(entry);
		if (removed) eventService.createUnregistrationEvent(entry.get());
		return removed;
	}

	/**
	 * Provide a BrokerMediator for the provided player in the provided world and the provided Object.
	 *
	 * @param <T> tbe type of Object being transacted
	 * @param playerID UUID of the player making the transaction
	 * @param worldID UUID of the world the transaction is taking place in
	 * @param object the Object being transacted
	 * @return an Optional containing a BrokerMediator for the provided player in the provided world and the provided Object, empty if no Broker is available for this transaction
	 */
	public final <T> Optional<BrokerMediator<T>> provideFor(UUID playerID, UUID worldID, T object) {
		@SuppressWarnings("unchecked")
		Optional<SimilarBrokers<T>> ofType = similar.get((Class<T>) object.getClass());
		if (ofType.isEmpty()) return Optional.empty();
		Iterator<PrioritizedBroker<T, ?>> iterator = ofType.get().iterator();
		while (iterator.hasNext()) {
			PrioritizedBroker<T, ?> next = iterator.next();
			if (next.get().handles(playerID, worldID, object)) return Optional.of(new BrokerMediator<>(next.get(), playerID, worldID, object));
		}
		return Optional.empty();
	}

}
