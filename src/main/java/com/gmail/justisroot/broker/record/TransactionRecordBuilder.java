/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker.record;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.gmail.justisroot.broker.BrokerInfo;

/**
 * Builder for a {@link TransactionRecord}.<br>
 * <br>
 * Changes resulting from the success of the representing transaction should only take place within the {@code Runnable} submitted via {@link #buildSuccess(Runnable)}.
 *
 * @param <T> The type of object used in the transaction that this record represents.
 */
public abstract class TransactionRecordBuilder<T> implements Transaction<T> {

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
	 * Attempt to build a successful {@link TransactionRecord} for this transaction.<br>
	 * <br>
	 * Same as {@link #buildSuccess(Runnable)} where onComplete is null.<br>
	 * <br>
	 * <b>This will trigger the TransactionPreProcessEvent</b> and may result in the transaction being cancelled.<br>
	 * Ensure that your implementation respects the returned record by not proceeding with the transaction if a failure reason is present.
	 *
	 * @return A {@link TransactionRecord} for this transaction, potentially non-successful if cancelled by 3rd party Listeners
	 */
	public abstract TransactionRecord<T> buildSuccess();

	/**
	 * Attempt to build a successful {@link TransactionRecord} for this transaction.<br>
	 * <br>
	 * <b>This will trigger the TransactionPreProcessEvent</b> and may result in the transaction being cancelled.<br>
	 * Ensure that your implementation respects the returned record by not proceeding with the transaction if a failure reason is present.
	 *
	 * @param onComplete A Runnable to be called if the transaction isn't cancelled and the transaction is complete. Accepts null values.
	 * @return A {@link TransactionRecord} for this transaction, potentially non-successful if cancelled by 3rd party Listeners
	 */
	public abstract TransactionRecord<T> buildSuccess(Runnable onComplete);

	/**
	 * Build a failed {@link TransactionRecord} for this transaction with a specified failure reason.
	 *
	 * @param failReason The failure reason for this transaction
	 * @return A {@link TransactionRecord} for a failed transaction
	 */
	public abstract TransactionRecord<T> buildFailure(String failReason);

}