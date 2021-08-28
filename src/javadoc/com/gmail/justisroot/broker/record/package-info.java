/**
 * This package contains the portion of BrokerAPI pertaining to transaction records.<br>
 * <br>
 * Transaction records are a key aspect of BrokerAPI, as they contain all of the valuable information returned as a result of a transaction attempt.<br>
 * All implementations of {@link com.gmail.justisroot.broker.Broker} will need to create both {@link SaleRecord}s and {@link PurchaseRecord}s to return for each transaction call.
 */
package com.gmail.justisroot.broker.record;