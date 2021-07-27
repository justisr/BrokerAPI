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

import java.util.HashMap;
import java.util.Map;

public final class BrokerInfo {

	private static final Map<Broker<?>, BrokerInfo> CACHE = new HashMap<>();

	private final String id, provider;
	private final Class<?> type;

	private BrokerInfo(Broker<?> broker) {
		this.id = broker.getId();
		this.provider = broker.getProvider();
		this.type = broker.getType();
	}

	static final BrokerInfo get(Broker<?> broker) {
		BrokerInfo info = CACHE.get(broker);
		if (info == null) CACHE.put(broker, info = new BrokerInfo(broker));
		return info;
	}

	/**
	 * Get the name of the provider of the Broker involved in this event
	 * @return the name of the provider of the involved Broker
	 */
	public final String provider() {
		return this.provider;
	}

	/**
	 * Get the ID of the Broker involved in this event.
	 *
	 * @return the ID of the Broker involved in this event
	 */
	public final String id() {
		return this.id;
	}

	/**
	 * Get the Object type that this Broker handles.
	 *
	 * @return the Object type that this Broker handles
	 */
	public final Class<?> type() {
		return this.type;
	}
}
