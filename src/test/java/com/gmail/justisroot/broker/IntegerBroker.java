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

public class IntegerBroker extends MockBroker {

	public static final String ID = "IntegerBroker";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public byte getPriority() {
		return 10;
	}

	@Override
	public boolean handlesPurchases(Optional<UUID> playerID, Optional<UUID> worldID, String object) {
		try {
			Integer.parseInt(object);
			return true;
		} catch (NumberFormatException e) { }
		return false;
	}


	@Override
	public boolean handlesSales(Optional<UUID> playerID, Optional<UUID> worldID, String object) {
		try {
			Integer.parseInt(object);
			return true;
		} catch (NumberFormatException e) { }
		return false;
	}

}
