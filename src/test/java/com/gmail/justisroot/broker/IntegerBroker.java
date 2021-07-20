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
public class IntegerBroker extends MockBroker {

	public static final String ID = "IntegerBroker";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean handlesPurchases(UUID playerID, UUID worldID, String object) {
		try {
			Integer.parseInt(object);
			return true;
		} catch (NumberFormatException e) { }
		return false;
	}


	@Override
	public boolean handlesSales(UUID playerID, UUID worldID, String object) {
		try {
			Integer.parseInt(object);
			return true;
		} catch (NumberFormatException e) { }
		return false;
	}

}
