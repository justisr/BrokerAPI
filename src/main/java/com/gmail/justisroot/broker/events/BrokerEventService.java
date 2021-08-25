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
package com.gmail.justisroot.broker.events;

import com.gmail.justisroot.broker.BrokerInfo;
import com.gmail.justisroot.broker.record.PreProcessPurchaseRecord;
import com.gmail.justisroot.broker.record.PreProcessSaleRecord;
import com.gmail.justisroot.broker.record.PurchaseRecord;
import com.gmail.justisroot.broker.record.SaleRecord;

/**
 * The internal BrokerAPI event service.
 */
public final class BrokerEventService {

	private static final BrokerEventService INSTANCE = new BrokerEventService();

	private BrokerEventHandler regHandler;
	private BrokerEventHandler unregHandler;
	private SaleEventHandler saleHandler;
	private PurchaseEventHandler purchaseHandler;
	private SalePreProcessEventHandler salePreProcessHandler;
	private PurchasePreProcessEventHandler purchasePreProcessHandler;

	private BrokerEventService() {}

	public static BrokerEventService current() {
		return INSTANCE;
	}

	final void setRegistrationHandler(BrokerEventHandler handler) {
		this.regHandler = handler;
	}

	final void setUnregistrationHandler(BrokerEventHandler handler) {
		this.unregHandler = handler;
	}

	final void setPurchaseHandler(PurchaseEventHandler handler) {
		this.purchaseHandler = handler;
	}

	final void setSaleHandler(SaleEventHandler handler) {
		this.saleHandler = handler;
	}

	final void setSalePreProcessHandler(SalePreProcessEventHandler handler) {
		this.salePreProcessHandler = handler;
	}

	final void setPurchasePreProcessHandler(PurchasePreProcessEventHandler handler) {
		this.purchasePreProcessHandler = handler;
	}

	/**
	 * Generate a BrokerRegistrationEvent
	 * @param info the BrokerInfo for the Broker associated with this event
	 */
	public final void createRegistrationEvent(BrokerInfo info) {
		if (regHandler != null) regHandler.run(info);
	}

	/**
	 * Generate a BrokerUnregistrationEvent
	 * @param info the BrokerInfo for the broker associated with this event
	 */
	public final void createUnregistrationEvent(BrokerInfo info) {
		if (unregHandler != null) unregHandler.run(info);
	}

	/**
	 * Generate a PurchaseEvent
	 * @param info the BrokerInfo for the Broker associated with this event
	 * @param record the PurchaseRecord associated with this event
	 */
	public final void createPurchaseEvent(BrokerInfo info, PurchaseRecord<?> record) {
		if (purchaseHandler != null) purchaseHandler.run(info, record);
	}

	/**
	 * Generate a SaleEvent
	 * @param info the BrokerInfo for the Broker associated with this event
	 * @param record the SaleRecord associated with this event
	 */
	public final void createSaleEvent(BrokerInfo info, SaleRecord<?> record) {
		if (saleHandler != null) saleHandler.run(info, record);
	}

	/**
	 * Generate a SalePreProcessEvent
	 * @param info the BrokerInfo for the Broker associated with this event
	 * @param record the PreProcessSaleRecord associated with this event
	 * @return true if the event was cancelled, false otherwise
	 */
	public final boolean createSalePreProcessEvent(BrokerInfo info, PreProcessSaleRecord record) {
		if (salePreProcessHandler != null) return salePreProcessHandler.run(info, record);
		return false;
	}

	/**
	 * Generate a PurchasePreProcessEvent
	 * @param info the BrokerInfo for the Broker associated with this event
	 * @param record the PreProcessPurchaseRecord associated with this event
	 * @return true if the event was cancelled, false otherwise
	 */
	public final boolean createPurchasePreProcessEvent(BrokerInfo info, PreProcessPurchaseRecord record) {
		if (purchasePreProcessHandler != null) return purchasePreProcessHandler.run(info, record);
		return false;
	}

}
