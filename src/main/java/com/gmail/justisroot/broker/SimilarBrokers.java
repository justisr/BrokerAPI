/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * A collection of Broker instances all handling the same Object type, ordered by priority.
 *
 * @author Justis R
 *
 * @param <T> The Type that the contained Broker instances all handle
 */
final class SimilarBrokers<T> {

	private final TreeSet<PrioritizedBroker<T, ?>> prioritized = new TreeSet<>();
	private final Class<T> type;

	SimilarBrokers(Class<T> type) {
		this.type = type;
	}

	Class<T> type() {
		return this.type;
	}

	final boolean add(PrioritizedBroker<T, ?> broker) {
		return this.prioritized.add(broker);
	}

	final boolean remove(PrioritizedBroker<?, ?> broker) {
		return this.prioritized.remove(broker);
	}

	final boolean contains(PrioritizedBroker<T, ?> broker) {
		return this.prioritized.contains(broker);
	}

	final TreeSet<PrioritizedBroker<T, ?>> prioritized() {
		return this.prioritized;
	}

	final Iterator<PrioritizedBroker<T, ?>> iterator() {
		return this.prioritized.iterator();
	}

}
