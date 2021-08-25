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

public class SaleRecord<T> extends TransactionRecord<T> implements Sale<T> {

	private final boolean listing;

	private SaleRecord(BrokerInfo info, T object, Optional<UUID> playerID, Optional<UUID> worldID, int volume, BigDecimal value, boolean listing, Runnable onComplete, Optional<String> failReason) {
		super(info, object, playerID, worldID, volume, value, onComplete, failReason);
		this.listing = listing;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isListing() {
		return this.listing;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean complete() {
		if (super.complete()) BrokerEventService.current().createSaleEvent(info, this);
		else return false;
		return true;
	}

	public static final class SaleRecordBuilder<T> extends TransactionRecordBuilder<T> implements Sale<T> {

		private boolean listing;

		SaleRecordBuilder(BrokerInfo info, T object, Optional<UUID> playerID, Optional<UUID> worldID) {
			super(info, object, playerID, worldID);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isListing() {
			return this.listing;
		}

		/**
		 * Set whether or not this sale is merely a listing.<br>
		 * <br>
		 * A listing is a type of sale in which funds are not transferred, because the item is merely "put up" for sale.<br>
		 * Examples of a listing type sale include an item being added to an auction or a shop.<br>
		 * Because the item is not yet sold and funds are not transferred, the {@link #value()} of a listing sale will always be 0.
		 *
		 * @param listing true if the item is being put up for sale, false if it is being sold
		 * @return
		 */
		public SaleRecordBuilder<T> setListing(boolean listing) {
			this.listing = listing;
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BigDecimal value() {
			if (this.listing) return BigDecimal.ZERO;
			return super.value();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SaleRecordBuilder<T> setVolume(int volume) {
			return (SaleRecordBuilder<T>) super.setVolume(volume);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SaleRecordBuilder<T> setValue(BigDecimal value) {
			return (SaleRecordBuilder<T>) super.setValue(value);
		}

		/**
		 * Attempt to build a successful SaleRecord for this transaction.<br>
		 * <br>
		 * Same as {@link #buildSuccess(Runnable)} where onComplete is null.<br>
		 * <br>
		 * <b>This will trigger the TransactionPreProcessEvent</b> and may result in the transaction being cancelled.<br>
		 * Ensure that your implementation respects the returned record by not proceeding with the transaction if a failure reason is present.
		 *
		 * @return A SaleRecord for this transaction, potentially non-successful if cancelled by 3rd party Listeners
		 */
		@Override
		public SaleRecord<T> buildSuccess() {
			return buildSuccess(null);
		}

		/**
		 * Attempt to build a successful SaleRecord for this transaction.<br>
		 * <br>
		 * <b>This will trigger the SalePreProcessEvent</b> and may result in the transaction being cancelled.<br>
		 * Ensure that your implementation respects the returned record by not proceeding with the transaction if a failure reason is present.
		 *
		 * @param onComplete A Runnable to be called if the transaction isn't cancelled and the transaction is complete. Accepts null values.
		 * @return A SaleRecord for this transaction, potentially non-successful if cancelled by 3rd party Listeners
		 */
		@Override
		public SaleRecord<T> buildSuccess(Runnable onComplete) {
			boolean cancelled = BrokerEventService.current().createSalePreProcessEvent(info, new PreProcessSaleRecord(this));
			if (cancelled) return build(onComplete, Optional.of("Sale cancelled"));
			return build(onComplete == null ? () -> {} : onComplete, Optional.empty());
		}

		/**
		 * Build a failed SaleRecord for this transaction with a specified failure reason.
		 *
		 * @param failReason The failure reason for this transaction
		 * @return A SaleRecord for a failed transaction
		 */
		@Override
		public SaleRecord<T> buildFailure(String failReason) {
			return build(null, Optional.ofNullable(failReason));
		}

		private SaleRecord<T> build(Runnable onComplete, Optional<String> failReason) {
			return new SaleRecord<>(info, object, playerID, worldID, volume, value, listing, onComplete, failReason);
		}

	}

	/**
	 * Start building a record for a sale of the provided object with a player with the provided id.
	 *
	 * @param <T> The type of object being transacted
	 * @param broker The Broker facilitating this transaction
	 * @param object The object being transacted
	 * @param playerID The ID of the player participating in the transaction
	 * @param worldID The ID of the world that the transaction is taking place in
	 * @return A new TransactionRecordBuilder with the provided data
	 * @throws IllegalArgumentException if either provided arguments are null
	 */
	public static final <T> SaleRecordBuilder<T> start(Broker<T> broker, T object, Optional<UUID> playerID, Optional<UUID> worldID) {
		if (broker == null || object == null || playerID == null || worldID == null) throw new IllegalArgumentException("No null arguments!");
		return new SaleRecordBuilder<>(BrokerInfo.get(broker), object, playerID, worldID);
	}

	/**
	 * Start building a record for a listing type sale of the provided object with a player with the provided id.<br>
	 * <br>
	 * A listing is a type of sale in which funds are not transferred, because the item is merely "put up" for sale.<br>
	 * Examples of a listing type sale include an item being added to an auction or a shop.<br>
	 * Because the item is not yet sold and funds are not transferred, the {@link #value()} of a listing sale will always be 0.
	 *
	 * @param <T> The type of object being transacted
	 * @param broker The Broker facilitating this transaction
	 * @param object The object being transacted
	 * @param playerID The ID of the player participating in the transaction
	 * @param worldID The ID of the world that the transaction is taking place in
	 * @return A new TransactionRecordBuilder with the provided data
	 * @throws IllegalArgumentException if either provided arguments are null
	 */
	public static final <T> SaleRecordBuilder<T> startListing(Broker<T> broker, T object, Optional<UUID> playerID, Optional<UUID> worldID) {
		return start(broker, object, playerID, worldID).setListing(true);
	}

}
