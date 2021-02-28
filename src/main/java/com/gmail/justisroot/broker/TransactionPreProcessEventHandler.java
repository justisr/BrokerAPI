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
interface TransactionPreProcessEventHandler {

	/**
	 * Run the event
	 * @param info the BrokerInfo associated with this event
	 * @param preProcessTransactionRecord the TransactionRecordBuilder associated with this event
	 * @return true if the event was cancelled, otherwise false
	 */
	boolean run(BrokerInfo info, PreProcessTransactionRecord preProcessTransactionRecord);

}
