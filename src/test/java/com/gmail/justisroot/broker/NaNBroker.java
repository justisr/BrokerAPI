/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

import java.util.UUID;

/**
 * @author Justis R
 *
 */
public class NaNBroker extends MockBroker {

	public static final String ID = "NaNBroker";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean handlesPurchases(UUID playerID, UUID worldID, String object) {
		try {
			Double.valueOf(object);
			return false;
		} catch (NumberFormatException e) { }
		return true;
	}

	@Override
	public boolean handlesSales(UUID playerID, UUID worldID, String object) {
		try {
			Double.valueOf(object);
			return false;
		} catch (NumberFormatException e) { }
		return true;
	}
}
