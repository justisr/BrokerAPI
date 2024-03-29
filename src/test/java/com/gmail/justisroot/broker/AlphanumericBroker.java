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

public class AlphanumericBroker extends MockBroker {

	public static final String ID = "AlphaNumericBroker";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public byte getPriority() {
		return -10;
	}

	@Override
	public boolean handlesPurchases(Optional<UUID> playerID, Optional<UUID> worldID, String object) {
		for (int i = 0; i < object.length(); i++){
			char c = object.charAt(i);
			if (!Character.isLetterOrDigit(c)) return false;
		}
		return true;
	}

	@Override
	public boolean handlesSales(Optional<UUID> playerID, Optional<UUID> worldID, String object) {
		for (int i = 0; i < object.length(); i++){
			char c = object.charAt(i);
			if (!Character.isLetterOrDigit(c)) return false;
		}
		return true;
	}

}
