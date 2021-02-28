/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

import java.math.BigDecimal;
import java.util.UUID;

import com.gmail.justisroot.broker.TransactionRecord.TransactionRecordBuilder;

/**
 * @author Justis R
 *
 */
public final class PreProcessTransactionRecord {

	private final TransactionRecordBuilder<?> builder;

	PreProcessTransactionRecord(TransactionRecordBuilder<?> builder) {
		this.builder = builder;
	}

	/**
	 * Was this transaction a sale?
	 *
	 * @return true if it was a sale, false if it was a purchase.
	 */
	public final boolean isSale() {
		return builder.isSale();
	}

	/**
	 * Was this transaction a purchase?
	 *
	 * @return true if it was a purchase, false if it was a sale
	 */
	public final boolean isPurchase() {
		return builder.isPurchase();
	}

	/**
	 * Get the item that was transacted.<br>
	 * <br>
	 * Could be an ItemStack, Command, Permission, etc.
	 *
	 * @return the item that was transacted
	 */
	public final Object item() {
		return builder.item();
	}

	/**
	 * Get the UUID of the player involved in this transaction.
	 *
	 * @return the UUID of the player involved in this transaction
	 */
	public final UUID playerID() {
		return builder.playerID();
	}

	/**
	 * Get the volume (amount) of the item which was transacted.
	 *
	 * @return the volume (amount) of the item transacted
	 */
	public final int getVolume() {
		return builder.getVolume();
	}

	/**
	 * Get the current recorded amount of money moved either to or from the player's balance as a result of this transaction.
	 *
	 * @return the current recorded amount of money moved as a result of this transaction
	 */
	public final BigDecimal getMoneyMoved() {
		return builder.getMoneyMoved();
	}

	//	/**
	//	 * Set the volume (amount) of the item which is to be transacted.<br>
	//	 * <br>
	//	 * If the providing Broker was correctly implemented, this change will be reflected in the final transaction.
	//	 */
	//	public final void setVolume(int volume) {
	//		builder.setVolume(volume);
	//	}
	//
	//	/**
	//	 * Set the amount of money to be moved either to or from the player's balance as a result of this transaction.<br>
	//	 * <br>
	//	 * If the providing Broker was correctly implemented, this change will be reflected in the final transaction.
	//	 */
	//	public final void setMoneyMoved(BigDecimal money) {
	//		builder.setMoneyMoved(money);
	//	}

}
