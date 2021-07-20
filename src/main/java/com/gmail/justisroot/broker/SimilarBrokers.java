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
