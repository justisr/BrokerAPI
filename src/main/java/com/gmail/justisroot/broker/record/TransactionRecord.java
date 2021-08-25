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
package com.gmail.justisroot.broker.record;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.gmail.justisroot.broker.BrokerInfo;

/**
 * A record of a potentially cancelled or incomplete transaction.<br>
 * The record is only complete once the transaction initiator has run {@link #complete()}
 *
 * @param <T> The object type that this record's transaction handles
 */
abstract class TransactionRecord<T> implements Transaction<T> {

	private Runnable onComplete;

	final BrokerInfo info;
	private final T object;
	private final Optional<UUID> playerID, worldID;
	private final int volume;
	private final BigDecimal value;
	private final Optional<String> failReason;

	TransactionRecord(BrokerInfo info, T object, Optional<UUID> playerID, Optional<UUID> worldID, int volume, BigDecimal value, Runnable onComplete, Optional<String> failReason) {
		this.info = info;
		this.object = object;
		this.volume = volume;
		this.value = value;
		this.playerID = playerID;
		this.worldID = playerID;
		this.onComplete = onComplete;
		this.failReason = failReason;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final T object() {
		return this.object;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int volume() {
		return this.volume;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BigDecimal value() {
		return this.value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Optional<UUID> playerID() {
		return this.playerID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Optional<UUID> worldID() {
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
	 * Completes the Broker's end of the sale and initiates the appropriate TransactionEvent.<br>
	 * <br>
	 * To be run by the caller when the transaction has been completed. (i.e funds transferred, items moved, etc)
	 *
	 * @return true if the Broker's completion and the TransactionEvent ran, false if it had already been run, or if the transaction was not a success
	 */
	public boolean complete() {
		if (!isSuccess() || onComplete == null) return false;
		onComplete.run();
		onComplete = null;
		return true;
	}

	/**
	 * A builder for the record of a transaction attempt.<br>
	 * Changes resulting from the success of the representing transaction should only take place within the {@code Runnable} submitted via {@link #buildSuccess(Runnable)}
	 *
	 * @param <T> The object type that this record's transaction handles
	 */
	public static abstract class TransactionRecordBuilder<T> implements Transaction<T> {

		final BrokerInfo info;
		final T object;
		final Optional<UUID> playerID, worldID;
		int volume = 1;
		BigDecimal value = BigDecimal.ZERO;

		TransactionRecordBuilder(BrokerInfo info, T object, Optional<UUID> playerID, Optional<UUID> worldID) {
			this.info = info;
			this.object = object;
			this.playerID = playerID;
			this.worldID = worldID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T object() {
			return this.object;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Optional<UUID> playerID() {
			return this.playerID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Optional<UUID> worldID() {
			return this.worldID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int volume() {
			return this.volume;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BigDecimal value() {
			return this.value;
		}

		/**
		 * Record the volume (amount) of the item which was transacted.
		 *
		 * @param volume the volume (amount) of the item transacted
		 * @return this
		 */
		public TransactionRecordBuilder<T> setVolume(int volume) {
			this.volume = volume;
			return this;
		}

		/**
		 * Record the monetary value of this transaction.<br>
		 * Values must always be positive.
		 *
		 * @param value the value of this transaction
		 * @return this
		 */
		public TransactionRecordBuilder<T> setValue(BigDecimal value) {
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
		public abstract TransactionRecord<T> buildSuccess();

		/**
		 * Attempt to build a successful TransactionReport for this transaction.<br>
		 * <br>
		 * <b>This will trigger the TransactionPreProcessEvent</b> and may result in the transaction being cancelled.<br>
		 * Ensure that your implementation respects the returned record by not proceeding with the transaction if a failure reason is present.
		 *
		 * @param onComplete A Runnable to be called if the transaction isn't cancelled and the transaction is complete. Accepts null values.
		 * @return A TransactionReport for this transaction, potentially non-successful if cancelled by 3rd party Listeners
		 */
		public abstract TransactionRecord<T> buildSuccess(Runnable onComplete);

		/**
		 * Build a failed TransactionReport for this transaction with a specified failure reason.
		 *
		 * @param failReason The failure reason for this transaction
		 * @return A TransactionReport for a failed transaction
		 */
		public abstract TransactionRecord<T> buildFailure(String failReason);

	}

}
