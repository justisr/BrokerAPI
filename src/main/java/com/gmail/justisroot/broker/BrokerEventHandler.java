/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

/**
 * @author Justis R
 *
 */
interface BrokerEventHandler {

	void run(BrokerInfo info);

}
