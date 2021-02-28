/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public final class TransactionRecord<T> {

	private final boolean sale;
	private final T item;
	private final UUID playerID;
	private final int volume;
	private final BigDecimal money;
	private final Optional<String> failReason;

	private TransactionRecord(boolean sale, T item, UUID playerID, int volume, BigDecimal money, Optional<String> failReason) {
		this.sale = sale;
		this.item = item;
		this.volume = volume;
		this.money = money;
		this.playerID = playerID;
		this.failReason = failReason;
	}

	/**
	 * Was this transaction a purchase transaction?
	 *
	 * @return true if it was a purchase, false if it was a sale.
	 */
	public final boolean isSale() {
		return this.sale;
	}

	/**
	 * Was this transaction a purchase transaction?
	 *
	 * @return true if it was a purchase, false if it was a sale
	 */
	public final boolean isPurchase() {
		return !this.sale;
	}

	/**
	 * Get the item involved in this transaction.
	 *
	 * @return the item involved in this transaction
	 */
	public final T item() {
		return this.item;
	}

	/**
	 * Get the volume (amount) of the item which was transacted.
	 *
	 * @return the volume (amount) of the item which was transacted
	 */
	public final int volume() {
		return this.volume;
	}

	/**
	 * Get the amount of money moved either to or from the player's balance as a result of this transaction.
	 *
	 * @return the amount of money moved as a result of this transaction
	 */
	public final BigDecimal moneyMoved() {
		return this.money;
	}

	/**
	 * Get the UUID of the player associated with this transaction.
	 *
	 * @return the UUID of the player associated with this transaction
	 */
	public final UUID playerID() {
		return this.playerID;
	}

	/**
	 * Get the fail reason, if applicable, for this transaction.
	 *
	 * @return an Optional containing the fail reason for this transaction, if it failed, empty if it was successful.
	 */
	public final Optional<String> failReason() {
		return this.failReason;
	}

	public static final class TransactionRecordBuilder<T> {

		private final Broker<T> broker;
		private final boolean sale;
		private final T item;
		private final UUID playerID;
		private int volume = 1;
		private BigDecimal money = BigDecimal.ZERO;

		private TransactionRecordBuilder(Broker<T> broker, boolean sale, T item, UUID playerID) {
			this.broker = broker;
			this.sale = sale;
			this.item = item;
			this.playerID = playerID;
		}

		/**
		 * Was this transaction a sale?
		 *
		 * @return true if it was a sale, false if it was a purchase.
		 */
		public final boolean isSale() {
			return this.sale;
		}

		/**
		 * Was this transaction a purchase?
		 *
		 * @return true if it was a purchase, false if it was a sale
		 */
		public final boolean isPurchase() {
			return !this.sale;
		}

		/**
		 * Get the item that was transacted.
		 *
		 * @return the item that was transacted
		 */
		public final T item() {
			return this.item;
		}

		/**
		 * Get the UUID of the player involved in this transaction.
		 *
		 * @return the UUID of the player involved in this transaction
		 */
		public final UUID playerID() {
			return this.playerID;
		}

		/**
		 * Get the volume (amount) of the item which was transacted.
		 *
		 * @return the volume (amount) of the item transacted
		 */
		public final int getVolume() {
			return this.volume;
		}

		/**
		 * Get the current recorded amount of money moved either to or from the player's balance as a result of this transaction.
		 *
		 * @return the current recorded amount of money moved as a result of this transaction
		 */
		public final BigDecimal getMoneyMoved() {
			return this.money;
		}

		/**
		 * Record the volume (amount) of the item which was transacted.
		 *
		 * @param volume the volume (amount) of the item transacted
		 * @return this TransactionBuilder
		 */
		public final TransactionRecordBuilder<T> setVolume(int volume) {
			this.volume = volume;
			return this;
		}

		/**
		 * Record the amount of money that has been moved to or from the player's balance as a result of this transaction.<br>
		 * Values must always be positive.
		 *
		 * @param money the amount of money moved either to or from the player's balance
		 * @return this TransactionBuilder
		 */
		public final TransactionRecordBuilder<T> setMoneyMoved(BigDecimal money) {
			this.money = money.abs();
			return this;
		}

		/**
		 * Attempt to build a successful TransactionReport for this transaction<br>
		 * <br>
		 * <b>This will trigger the TransactionPreProcessEvent</b><br>
		 * Ensure that your transaction respects the returned record by canceling the transaction if there is a failure reason.
		 *
		 * @return A TransactionReport for this transaction, potentially cancelled by 3rd party Listeners
		 */
		public final TransactionRecord<T> buildSuccess() {
			boolean cancelled = BrokerAPI.instance().eventService().createTransactionPreProcessEvent(broker, new PreProcessTransactionRecord(this));
			if (cancelled) return build(Optional.of("Transaction event cancelled by a 3rd party Listener"));
			return build(Optional.empty());
		}

		/**
		 * Build a failed TransactionReport for this transaction with a specified failure reason.
		 *
		 * @param failReason The failure reason for this transaction
		 * @return A TransactionReport for a failed transaction
		 */
		public final TransactionRecord<T> buildFailure(String failReason) {
			return build(Optional.ofNullable(failReason));
		}

		private final TransactionRecord<T> build(Optional<String> failReason) {
			return new TransactionRecord<>(sale, item, playerID, volume, money, failReason);
		}
	}

	/**
	 * Start building a transaction record for a sale of the provided item with a player with the provided id.
	 *
	 * @param <T> The type of item being transacted
	 * @param item The item being transacted
	 * @param playerID The ID of the player participating in the transaction
	 * @return A new TransactionRecordBuilder with the provided data
	 * @throws IllegalArgumentException if either provided arguments are null
	 */
	public static final <T> TransactionRecordBuilder<T> startSale(Broker<T> broker, T item, UUID playerID) {
		if (broker == null || item == null || playerID == null) throw new IllegalArgumentException("No null arguments!");
		return new TransactionRecordBuilder<>(broker, true, item, playerID);
	}

	/**
	 * Start building a transaction record for a purchase of the provided item with a player with the provided id.
	 *
	 * @param <T> The type of item being transacted
	 * @param item The item being transacted
	 * @param playerID The ID of the player participating in the transaction
	 * @return A new TransactionRecordBuilder with the provided data
	 * @throws IllegalArgumentException if either provided arguments are null
	 */
	public static final <T> TransactionRecordBuilder<T> startPurchase(Broker<T> broker, T item, UUID playerID) {
		if (broker == null || item == null || playerID == null) throw new IllegalArgumentException("No null arguments!");
		return new TransactionRecordBuilder<>(broker, false, item, playerID);
	}

}
