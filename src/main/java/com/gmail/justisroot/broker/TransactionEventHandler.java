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
interface TransactionEventHandler {

	/**
	 * Run the event
	 * @param info the BrokerInfo associated with this event
	 * @param record the TransactionRecord associated with this event
	 */
	void run(BrokerInfo info, TransactionRecord<?> record);

}
