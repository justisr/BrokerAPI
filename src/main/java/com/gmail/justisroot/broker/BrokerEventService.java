/*
 * BrokerAPI Copyright 2020 Justis Root
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package com.gmail.justisroot.broker;

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
	 * @param info the BrokerInfo for the Broker associated with this event
	 */
	final void createRegistrationEvent(BrokerInfo info) {
		if (regHandler != null) regHandler.run(info);
	}

	/**
	 * Generate a BrokerUnregistrationEvent
	 * @param info the BrokerInfo for the broker associated with this event
	 */
	final void createUnregistrationEvent(BrokerInfo info) {
		if (unregHandler != null) unregHandler.run(info);
	}

	/**
	 * Generate a TransactionEvent
	 * @param info the BrokerInfo for the Broker associated with this event
	 * @param record the TransactionRecord associated with this event
	 */
	final void createTransactionEvent(BrokerInfo info, TransactionRecord<?> record) {
		if (transactionHandler != null) transactionHandler.run(info, record);
	}

	/**
	 * Generate a TransactionPreProcessEvent
	 * @param info the BrokerInfo for the Broker associated with this event
	 * @param preProcessTransactionRecord the TransactionRecordBuilder associated with this evvent
	 * @return true if the event was cancelled, false otherwise
	 */
	final boolean createTransactionPreProcessEvent(BrokerInfo info, PreProcessTransactionRecord preProcessTransactionRecord) {
		if (transactionHandler != null) return transPreProcessHandler.run(info, preProcessTransactionRecord);
		return false;
	}

}
