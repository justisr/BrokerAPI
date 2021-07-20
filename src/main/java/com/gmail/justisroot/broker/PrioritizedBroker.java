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

	@Override
	public final int hashCode() {
		return this.hash;
	}

	@Override
	public final boolean equals(Object o) {
		if (o == null || !(o instanceof PrioritizedBroker)) return false;
		PrioritizedBroker<?,?> that = (PrioritizedBroker<?,?>) o;
		if (this.broker == that.broker || this.broker.getId().equals(that.broker.getId())) return true;
		return false;
	}

	@Override
	public final int compareTo(PrioritizedBroker<T,B> that) {
		if (equals(that)) return 0;
		int value = Integer.compare(that.priority, this.priority);
		if (value == 0) value = this.broker.getId().compareTo(that.broker.getId());
		return value;
	}

}
