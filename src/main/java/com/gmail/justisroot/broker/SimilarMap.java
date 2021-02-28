/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class SimilarMap {

	private final Map<Class<?>, SimilarBrokers<?>> map = new HashMap<>();

	@SuppressWarnings("unchecked")
	final <T> boolean add(PrioritizedBroker<T, ?> broker) {
		SimilarBrokers<T> similar;
		if (!map.containsKey(broker.get().getType())) {
			map.put(broker.get().getType(), similar = new SimilarBrokers<>(broker.get().getType()));
		} else similar = (SimilarBrokers<T>) map.get(broker.get().getType());
		return similar.add(broker);
	}

	@SuppressWarnings("unchecked")
	final <T> boolean contains(PrioritizedBroker<T, ?> broker) {
		SimilarBrokers<T> similar = (SimilarBrokers<T>) map.get(broker.get().getType());
		if (similar == null) return false;
		return similar.contains(broker);
	}

	@SuppressWarnings("unchecked")
	final <T> Optional<SimilarBrokers<T>> get(Class<T> key) {
		return Optional.ofNullable((SimilarBrokers<T>) map.get(key));
	}

	@SuppressWarnings("unchecked")
	final <T> boolean remove(PrioritizedBroker<T, ?> broker) {
		SimilarBrokers<T> similar = (SimilarBrokers<T>) map.get(broker.get().getType());
		if (similar == null) return false;
		return similar.remove(broker);
	}

	final Map<Class<?>, SimilarBrokers<?>> rawMap() {
		return this.map;
	}

}
