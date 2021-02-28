/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

final class PrioritizedBroker<T, B extends Broker<T>> implements Comparable<PrioritizedBroker<T,B>> {

	private final B broker;
	private final int priority;
	private final int hash;

	PrioritizedBroker(B broker, int priority) {
		this.broker = broker;
		this.priority = priority;
		this.hash = (217 + broker.hashCode()) * 31 + priority;
	}

	/**
	 * @return the implementation of this Broker
	 */
	final B get() {
		return this.broker;
	}

	/**
	 * @return the priority of this Broker
	 */
	final int priority() {
		return this.priority;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return this.hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object o) {
		if (o == null || !(o instanceof PrioritizedBroker)) return false;
		PrioritizedBroker<?,?> that = (PrioritizedBroker<?,?>) o;
		if (this.broker == that.broker || this.broker.getId().equals(that.broker.getId())) return true;
		return false;
	}

	/**
	 * <p>
	 * Note: this class has a natural ordering that is inconsistent with #equals
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(PrioritizedBroker<T,B> that) {
		return Integer.compare(this.priority, that.priority);
	}

}
