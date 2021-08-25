/*
 * BrokerAPI Copyright 2020 Justis Root
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package com.gmail.justisroot.broker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.gmail.justisroot.broker.events.BrokerEventService;

/**
 * Provides an abstraction layer for transactions.<br>
 * <br>
 * This is the access point for Broker callers and the point of registration management for Broker implementors.<br>
 * Project maintained here: <a href="https://github.com/justisr/BrokerAPI">https://github.com/justisr/BrokerAPI</a>
 */
public final class BrokerAPI {

	private static BrokerAPI instance;

	private final Config config;

	private SimilarMap similar = new SimilarMap();
	private Map<String, PrioritizedBroker<?, ?>> brokers = new HashMap<>();

	BrokerAPI(Config config) {
		this.config = config;
		instance = this;
	}

	/**
	 * Get the active instance of BrokerAPI.
	 *
	 * @return the active instance of BrokerAPI
	 */
	public static final BrokerAPI current() {
		return instance;
	}

	/**
	 * Get a raw map of type grouped brokers
	 *
	 * @return a raw map of type grouped brokers
	 */
	final Map<Class<?>, SimilarBrokers<?>> brokerMap() {
		return similar.rawMap();
	}

	/**
	 * Get a collection of the installed brokers
	 *
	 * @return collection of installed brokers
	 */
	final Collection<PrioritizedBroker<?, ?>> brokers() {
		return brokers.values();
	}

	/**
	 * Register the provided Broker implementation.<br>
	 * Unregisters any existing Broker with the same ID prior to registration.
	 *
	 * @param <T> the type of Object that this Broker transacts
	 * @param broker the Broker instance to register
	 * @return true if registration was successful, false if the implementation was invalid
	 */
	public synchronized final <T> boolean register(Broker<T> broker) {
		if (broker == null || broker.getId() == null || broker.getId().isEmpty() || broker.getId().contains(" ") || config.isDisabled(broker)) return false;
		if (brokers.containsKey(broker.getId())) this.unregister(broker.getId());
		PrioritizedBroker<T, ?> entry = new PrioritizedBroker<>(broker, config.getPriority(broker));
		if (similar.contains(entry)) return false;
		config.ensureEntry(broker);
		brokers.put(broker.getId(), entry);
		similar.add(entry);
		BrokerEventService.current().createRegistrationEvent(BrokerInfo.get(broker));
		return true;
	}

	/**
	 * Unregister the provided Broker implementation.
	 *
	 * @param broker the Broker instance to unregister
	 * @return true if the Broker instance was successfully unregistered, false if the instance wasn't registered to begin with
	 */
	public synchronized final boolean unregister(Broker<?> broker) {
		return unregister(broker.getId());
	}

	/**
	 * Unregister a Broker instance by name.
	 *
	 * @param brokerID the ID of the Broker instance to unregister
	 * @return true if the Broker was successfully unregistered, false if it wasn't registered to begin with
	 */
	public synchronized final boolean unregister(String brokerID) {
		if (brokerID == null || !brokers.containsKey(brokerID)) return false;
		PrioritizedBroker<?, ?> entry = brokers.remove(brokerID);
		boolean removed = similar.remove(entry);
		if (removed) BrokerEventService.current().createUnregistrationEvent(BrokerInfo.get(entry.get()));
		return removed;
	}

	/**
	 * Return whether or not the given Broker implementation is registered
	 * @param broker to check if registered
	 * @return true if the Broker is registered, false if not
	 */
	public synchronized final boolean isRegistered(Broker<?> broker) {
		return isRegistered(broker.getId());
	}

	/**
	 * Return whether or not the Broker implementation with the given ID is registered
	 * @param brokerID the ID of the Broker to check if registered
	 * @return true if the Broker is registered, false if not
	 */
	public synchronized final boolean isRegistered(String brokerID) {
		return brokers.containsKey(brokerID);
	}

	/**
	 * Reload all Brokers with the current configuration
	 */
	final synchronized void reload() {
		config.reload();
		Collection<PrioritizedBroker<?, ?>> old = brokers.values();
		brokers = new HashMap<>();
		similar = new SimilarMap();
		for (PrioritizedBroker<?, ?> broker : old) register(broker.get());
	}

	/**
	 * Unregister all Brokers
	 */
	final synchronized void unregisterAll() {
		brokers = new HashMap<>();
		similar = new SimilarMap();
	}

	/**
	 * Provide a PurchaseMediator for the provided player in the provided world with the provided Object.
	 *
	 * @param <T> the type of Object being transacted
	 * @param playerID UUID of the player making the transaction
	 * @param worldID UUID of the world the transaction is taking place in
	 * @param object the Object being transacted, singular
	 * @return an Optional containing a BrokerMediator for the provided player in the provided world and the provided Object, empty if no Broker is available for this transaction
	 */
	public synchronized final <T> Optional<PurchaseMediator<T>> forPurchase(UUID playerID, UUID worldID, T object) {
		if (object == null) return Optional.empty();
		@SuppressWarnings("unchecked")
		Optional<SimilarBrokers<T>> ofType = similar.get((Class<T>) object.getClass());
		if (ofType.isEmpty()) return Optional.empty();
		Iterator<PrioritizedBroker<T, ?>> iterator = ofType.get().iterator();
		while (iterator.hasNext()) {
			PrioritizedBroker<T, ?> next = iterator.next();
			if (config.isGenerous(next.get()) && !next.get().canBeBought(Optional.ofNullable(playerID), Optional.ofNullable(worldID), object)) continue;
			if (next.get().handlesPurchases(Optional.ofNullable(playerID), Optional.ofNullable(worldID), object))
				return Optional.of(new PurchaseMediator<>(next.get(), playerID, worldID, object));
		}
		return Optional.empty();
	}

	/**
	 * Provide a SaleMediator for the provided player in the provided world with the provided Object.
	 *
	 * @param <T> the type of Object being sold
	 * @param playerID UUID of the player making the sale
	 * @param worldID UUID of the world the sale is taking place in
	 * @param object the Object being sold, singular
	 * @return an Optional containing a SaleMediator for the provided player in the provided world with the provided Object, empty if no Broker is available for this transaction
	 */
	public synchronized final <T> Optional<SaleMediator<T>> forSale(UUID playerID, UUID worldID, T object) {
		if (object == null) return Optional.empty();
		@SuppressWarnings("unchecked")
		Optional<SimilarBrokers<T>> ofType = similar.get((Class<T>) object.getClass());
		if (ofType.isEmpty()) return Optional.empty();
		Iterator<PrioritizedBroker<T, ?>> iterator = ofType.get().iterator();
		while (iterator.hasNext()) {
			PrioritizedBroker<T, ?> next = iterator.next();
			if (config.isGenerous(next.get()) && !next.get().canBeSold(Optional.ofNullable(playerID), Optional.ofNullable(worldID), object)) continue;
			if (next.get().handlesSales(Optional.ofNullable(playerID), Optional.ofNullable(worldID), object))
				return Optional.of(new SaleMediator<>(next.get(), playerID, worldID, object));
		}
		return Optional.empty();
	}

}
