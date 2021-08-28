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

import com.gmail.justisroot.broker.Broker;
import com.gmail.justisroot.broker.BrokerInfo;
import com.gmail.justisroot.broker.events.BrokerEventService;

/**
 * The transaction record to return for purchases. Should always be incomplete when returned, to be completed by the caller.<br>
 * <br>
 * Records are created using the builder pattern.<br>
 * To start building a record, use {@link #start(Broker, Object, Optional, Optional)}.
 *
 * The record is only complete once the transaction initiator has run {@link #complete()}.
 *
 * @param <T> The type of object used in the transaction that this record represents.
 */
public class PurchaseRecord<T> extends TransactionRecord<T> implements Purchase<T> {

	private PurchaseRecord(PurchaseRecordBuilder<T> builder, Runnable onComplete, Optional<String> failReason) {
		super(builder, onComplete, failReason);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean complete() {
		if (super.complete()) BrokerEventService.current().createPurchaseEvent(info, this);
		else return false;
		return true;
	}

	/**
	 * Builder for a {@link PurchaseRecord}.<br>
	 * <br>
	 * Changes resulting from the success of the representing transaction should only take place within the {@code Runnable} submitted via {@link #buildSuccess(Runnable)}.
	 *
	 * @param <T> The type of object used in the transaction that this record represents.
	 */
	public static final class PurchaseRecordBuilder<T> extends TransactionRecordBuilder<T> implements Purchase<T> {

		private PurchaseRecordBuilder(BrokerInfo info, T object, Optional<UUID> playerID, Optional<UUID> worldID) {
			super(info, object, playerID, worldID);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PurchaseRecordBuilder<T> setVolume(int volume) {
			return (PurchaseRecordBuilder<T>) super.setVolume(volume);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PurchaseRecordBuilder<T> setValue(BigDecimal value) {
			return (PurchaseRecordBuilder<T>) super.setValue(value);
		}

		/**
		 * Attempt to build a successful {@link TransactionRecord} for this transaction.<br>
		 * <br>
		 * Same as {@link #buildSuccess(Runnable)} where onComplete is null.<br>
		 * <br>
		 * <b>This will trigger the TransactionPreProcessEvent</b> and may result in the transaction being cancelled.<br>
		 * Ensure that your implementation respects the returned record by not proceeding with the transaction if a failure reason is present.
		 *
		 * @return A {@link PurchaseRecord} for this transaction, potentially non-successful if cancelled by 3rd party Listeners
		 */
		@Override
		public PurchaseRecord<T> buildSuccess() {
			return buildSuccess(null);
		}

		/**
		 * Attempt to build a successful {@link PurchaseRecord} for this transaction.<br>
		 * <br>
		 * <b>This will trigger the PurchasePreProcessEvent</b> and may result in the transaction being cancelled.<br>
		 * Ensure that your implementation respects the returned record by not proceeding with the transaction if a failure reason is present.
		 *
		 * @param onComplete A Runnable to be called if the transaction isn't cancelled and the transaction is complete. Accepts null values.
		 * @return A {@link PurchaseRecord} for this transaction, potentially non-successful if cancelled by 3rd party Listeners
		 */
		@Override
		public PurchaseRecord<T> buildSuccess(Runnable onComplete) {
			boolean cancelled = BrokerEventService.current().createPurchasePreProcessEvent(info, new PreProcessPurchaseRecord(this));
			if (cancelled) return build(onComplete, Optional.of("Purchase cancelled"));
			return build(onComplete == null ? () -> {} : onComplete, Optional.empty());
		}

		/**
		 * Build a failed {@link PurchaseRecord} for this transaction with a specified failure reason.
		 *
		 * @param failReason The failure reason for this transaction
		 * @return A {@link PurchaseRecord} for a failed transaction
		 */
		@Override
		public PurchaseRecord<T> buildFailure(String failReason) {
			return build(null, Optional.ofNullable(failReason));
		}

		private PurchaseRecord<T> build(Runnable onComplete, Optional<String> failReason) {
			return new PurchaseRecord<>(this, onComplete, failReason);
		}

	}

	/**
	 * Start building a record for a purchase of the provided object with a player with the provided id.
	 *
	 * @param <T> The type of object being transacted
	 * @param broker The Broker facilitating this transaction
	 * @param object The object being transacted
	 * @param playerID The ID of the player participating in the transaction
	 * @param worldID The ID of the world that the transaction is taking place in
	 * @return A new TransactionRecordBuilder with the provided data
	 * @throws IllegalArgumentException if either provided arguments are null
	 */
	public static final <T> PurchaseRecordBuilder<T> start(Broker<T> broker, T object, Optional<UUID> playerID, Optional<UUID> worldID) {
		if (broker == null || object == null || playerID == null || worldID == null) throw new IllegalArgumentException("No null arguments!");
		return new PurchaseRecordBuilder<>(BrokerInfo.get(broker), object, playerID, worldID);
	}

}
