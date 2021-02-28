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

/**
 * @author Justis R
 *
 */
public final class BrokerInfo {

	private static final Map<String, BrokerInfo> CACHE = new HashMap<>();

	private final String id, provider;
	private final Class<?> type;

	private BrokerInfo(Broker<?> broker) {
		this.id = broker.getId();
		this.provider = broker.getProvider();
		this.type = broker.getType();
	}

	static final BrokerInfo get(Broker<?> broker) {
		BrokerInfo info = CACHE.get(broker.getId());
		if (info == null) CACHE.put(broker.getId(), info = new BrokerInfo(broker));
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
