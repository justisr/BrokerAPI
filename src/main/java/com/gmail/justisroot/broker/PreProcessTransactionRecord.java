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
	 * Get the UUID of the world involved in this transaction.
	 *
	 * @return the UUID of the world involved in this transaction
	 */
	public final UUID worldID() {
		return builder.worldID();
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
	public final BigDecimal getValue() {
		return builder.getValue();
	}

}
