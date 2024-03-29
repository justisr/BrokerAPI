/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

import java.util.Optional;
import java.util.UUID;

public class NaNBroker extends MockBroker {

	public static final String ID = "NaNBroker";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean handlesPurchases(Optional<UUID> playerID, Optional<UUID> worldID, String object) {
		try {
			Double.valueOf(object);
			return false;
		} catch (NumberFormatException e) { }
		return true;
	}

	@Override
	public boolean handlesSales(Optional<UUID> playerID, Optional<UUID> worldID, String object) {
		try {
			Double.valueOf(object);
			return false;
		} catch (NumberFormatException e) { }
		return true;
	}
}
