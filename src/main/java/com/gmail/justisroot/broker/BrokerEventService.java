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
final class BrokerEventService {

	private BrokerEventHandler regHandler;
	private BrokerEventHandler unregHandler;
	private TransactionEventHandler transactionHandler;
	private TransactionPreProcessEventHandler transPreProcessHandler;

	BrokerEventService() {}

	final void setRegistrationHandler(BrokerEventHandler handler) {
		this.regHandler = handler;
	}

	final void setUnregistrationHandler(BrokerEventHandler handler) {
		this.unregHandler = handler;
	}

	void setTransactionHandler(TransactionEventHandler handler) {
		this.transactionHandler = handler;
	}

	final void setPreProcessTransactionHandler(TransactionPreProcessEventHandler handler) {
		this.transPreProcessHandler = handler;
	}

	/**
	 * Generate a BrokerRegistrationEvent
	 * @param broker the Broker associated with this event
	 */
	final void createRegistrationEvent(Broker<?> broker) {
		BrokerInfo info = BrokerInfo.get(broker);
		if (regHandler != null) regHandler.run(info);
	}

	/**
	 * Generate a BrokerUnregistrationEvent
	 * @param broker the Broker associated with this event
	 */
	final void createUnregistrationEvent(Broker<?> broker) {
		BrokerInfo info = BrokerInfo.get(broker);
		if (unregHandler != null) unregHandler.run(info);
	}

	/**
	 * Generate a TransactionEvent
	 * @param broker the Broker associated with this event
	 * @param record the TransactionRecord associated with this event
	 */
	final void createTransactionEvent(Broker<?> broker, TransactionRecord<?> record) {
		BrokerInfo info = BrokerInfo.get(broker);
		if (transactionHandler != null) transactionHandler.run(info, record);
	}

	/**
	 * Generate a TransactionPreProcessEvent
	 * @param brokerInfo the Broker associated with this event
	 * @param preProcessTransactionRecord the TransactionRecordBuilder associated with this evvent
	 * @return true if the event was cancelled, false otherwise
	 */
	final boolean createTransactionPreProcessEvent(Broker<?> brokerInfo, PreProcessTransactionRecord preProcessTransactionRecord) {
		BrokerInfo info = BrokerInfo.get(brokerInfo);
		if (transactionHandler != null) return transPreProcessHandler.run(info, preProcessTransactionRecord);
		return false;
	}

}
