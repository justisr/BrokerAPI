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
import java.util.Optional;
import java.util.UUID;

/**
 * A record of a potentially cancelled or incomplete transaction.<br>
 * The record is only complete once the transaction initiator has run {@link #complete()}
 *
 * @param <T> The object type that this record's transaction handles
 */
public final class TransactionRecord<T> {

	private Runnable onComplete;

	private final BrokerInfo info;
	private final boolean sale;
	private final T object;
	private final Optional<UUID> playerID, worldID;
	private final int volume;
	private final BigDecimal value;
	private final Optional<String> failReason;

	private TransactionRecord(BrokerInfo info, boolean sale, T object, Optional<UUID> playerID, Optional<UUID> worldID, int volume, BigDecimal value, Runnable onComplete, Optional<String> failReason) {
		this.info = info;
		this.sale = sale;
		this.object = object;
		this.volume = volume;
		this.value = value;
		this.playerID = playerID;
		this.worldID = playerID;
		this.onComplete = onComplete;
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
	 * Get the object involved in this transaction.
	 *
	 * @return the object involved in this transaction
	 */
	public final T object() {
		return this.object;
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
	 * Get the monetary value of this transaction
	 *
	 * @return the amount of money moved as a result of this transaction
	 */
	public final BigDecimal value() {
		return this.value;
	}

	/**
	 * Get the optional UUID of the player associated with this transaction.
	 *
	 * @return An optional UUID of the player associated with this transaction
	 */
	public final Optional<UUID> playerID() {
		return this.playerID;
	}

	/**
	 * Get the optional UUID of the world associated with this transaction.
	 *
	 * @return An optional UUID of the world associated with this transaction
	 */
	public final Optional<UUID> wordID() {
		return this.worldID;
	}

	/**
	 * Get the fail reason, if applicable, for this transaction.
	 *
	 * @return an Optional containing the fail reason for this transaction, if it failed, empty if it was successful.
	 */
	public final Optional<String> failReason() {
		return this.failReason;
	}

	/**
	 * Get whether or not this transaction was a successful one.
	 *
	 * @return true if the transaction was a success, false if it was cancelled.
	 */
	public final boolean isSuccess() {
		return failReason.isEmpty();
	}

	/**
	 * Complete the Broker's end of the sale and call the TransactionEvent.<br>
	 * <br>
	 * Call when the exchange of the object and the funds is complete on the caller's end.
	 * @return true if the Broker's completion and the TransactionEvent ran, false if it had already been run, or if the transaction was not a success
	 */
	public final boolean complete() {
		if (!isSuccess() || onComplete == null) return false;
		onComplete.run();
		onComplete = null;
		BrokerAPI.current().eventService().createTransactionEvent(info, this);
		return true;
	}

	/**
	 * A builder for the record of a transaction attempt.<br>
	 * Changes resulting from the success of the representing transaction should only take place within the {@code Runnable} submitted via {@link #buildSuccess(Runnable)}
	 *
	 * @param <T> The object type that this record's transaction handles
	 */
	public static final class TransactionRecordBuilder<T> {

		private final BrokerInfo info;
		private final boolean sale;
		private final T object;
		private final Optional<UUID> playerID, worldID;
		private int volume = 1;
		private BigDecimal value = BigDecimal.ZERO;

		private TransactionRecordBuilder(BrokerInfo info, boolean sale, T object, Optional<UUID> playerID, Optional<UUID> worldID) {
			this.info = info;
			this.sale = sale;
			this.object = object;
			this.playerID = playerID;
			this.worldID = worldID;
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
		 * Get the object that was transacted.
		 *
		 * @return the object that was transacted
		 */
		public final T object() {
			return this.object;
		}

		/**
		 * Get the optional UUID of the player involved in this transaction.
		 *
		 * @return An optional UUID of the player involved in this transaction
		 */
		public final Optional<UUID> playerID() {
			return this.playerID;
		}

		/**
		 * Get the optional UUID of the world associated with this transaction.
		 *
		 * @return An optional UUID of the world associated with this transaction
		 */
		public final Optional<UUID> worldID() {
			return this.worldID;
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
		 * Get the monetary value of this transaction.
		 *
		 * @return the current recorded amount of money moved as a result of this transaction
		 */
		public final BigDecimal getValue() {
			return this.value;
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
		 * Record the monetary value of this transaction.<br>
		 * Values must always be positive.
		 *
		 * @param value the value of this transaction
		 * @return this TransactionBuilder
		 */
		public final TransactionRecordBuilder<T> setValue(BigDecimal value) {
			this.value = value.abs();
			return this;
		}

		/**
		 * Attempt to build a successful TransactionReport for this transaction.<br>
		 * <br>
		 * Same as {@link #buildSuccess(Runnable)} where onComplete is null.<br>
		 * <br>
		 * <b>This will trigger the TransactionPreProcessEvent</b> and may result in the transaction being cancelled.<br>
		 * Ensure that your implementation respects the returned record by not proceeding with the transaction if a failure reason is present.
		 *
		 * @return A TransactionReport for this transaction, potentially non-successful if cancelled by 3rd party Listeners
		 */
		public final TransactionRecord<T> buildSuccess() {
			return buildSuccess(null);
		}

		/**
		 * Attempt to build a successful TransactionReport for this transaction.<br>
		 * <br>
		 * <b>This will trigger the TransactionPreProcessEvent</b> and may result in the transaction being cancelled.<br>
		 * Ensure that your implementation respects the returned record by not proceeding with the transaction if a failure reason is present.
		 *
		 * @param onComplete A Runnable to be called if the transaction isn't cancelled and the transaction is complete. Accepts null values.
		 * @return A TransactionReport for this transaction, potentially non-successful if cancelled by 3rd party Listeners
		 */
		public final TransactionRecord<T> buildSuccess(Runnable onComplete) {
			boolean cancelled = BrokerAPI.current().eventService().createTransactionPreProcessEvent(info, new PreProcessTransactionRecord(this));
			if (cancelled) return build(onComplete, Optional.of("Transaction cancelled"));
			return build(onComplete == null ? () -> {} : onComplete, Optional.empty());
		}

		/**
		 * Build a failed TransactionReport for this transaction with a specified failure reason.
		 *
		 * @param failReason The failure reason for this transaction
		 * @return A TransactionReport for a failed transaction
		 */
		public final TransactionRecord<T> buildFailure(String failReason) {
			return build(null, Optional.ofNullable(failReason));
		}

		private final TransactionRecord<T> build(Runnable onComplete, Optional<String> failReason) {
			return new TransactionRecord<>(info, sale, object, playerID, worldID, volume, value, onComplete, failReason);
		}
	}
	/**
	 * Start building a transaction record for a sale of the provided item with a player with the provided id.
	 *
	 * @param <T> The type of object being transacted
	 * @param broker The Broker facilitating this transaction
	 * @param object The object being transacted
	 * @param playerID The ID of the player participating in the transaction
	 * @param worldID The ID of the world that the transaction is taking place in
	 * @return A new TransactionRecordBuilder with the provided data
	 * @throws IllegalArgumentException if either provided arguments are null
	 */
	public static final <T> TransactionRecordBuilder<T> startSale(Broker<T> broker, T object, Optional<UUID> playerID, Optional<UUID> worldID) {
		if (broker == null || object == null || playerID == null || worldID == null) throw new IllegalArgumentException("No null arguments!");
		return new TransactionRecordBuilder<>(BrokerInfo.get(broker), true, object, playerID, worldID);
	}

	/**
	 * Start building a transaction record for a purchase of the provided item with a player with the provided id.
	 *
	 * @param <T> The type of object being transacted
	 * @param broker The Broker facilitating this transaction
	 * @param object The object being transacted
	 * @param playerID The ID of the player participating in the transaction
	 * @param worldID The ID of the world that the transaction is taking place in
	 * @return A new TransactionRecordBuilder with the provided data
	 * @throws IllegalArgumentException if either provided arguments are null
	 */
	public static final <T> TransactionRecordBuilder<T> startPurchase(Broker<T> broker, T object, Optional<UUID> playerID, Optional<UUID> worldID) {
		if (broker == null || object == null || playerID == null || worldID == null) throw new IllegalArgumentException("No null arguments!");
		return new TransactionRecordBuilder<>(BrokerInfo.get(broker), false, object, playerID, worldID);
	}

}
