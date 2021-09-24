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
 * The transaction record to return for transactions. Should always be incomplete when returned, to be completed by the caller.<br>
 * The record is only complete once the transaction caller has run {@link #complete()}
 *
 * @param <T> The object type that this record's transaction handles
 */
public abstract class TransactionRecord<T> implements Transaction<T> {

	private Runnable onComplete;
	private Optional<String> failReason;

	final BrokerInfo info;

	private final T object;
	private final Optional<UUID> playerID, worldID;
	private final int volume;
	private final BigDecimal value;

	TransactionRecord(TransactionRecordBuilder<T> builder, Runnable onComplete) {
		this(builder);
		this.onComplete = onComplete;
		this.failReason = Optional.empty();
	}

	TransactionRecord(TransactionRecordBuilder<T> builder, Optional<String> failReason) {
		this(builder);
		this.failReason = failReason;
	}

	private TransactionRecord(TransactionRecordBuilder<T> builder) {
		this.info = builder.info;
		this.object = builder.object;
		this.volume = builder.volume;
		this.value = builder.value;
		this.playerID = builder.playerID;
		this.worldID = builder.playerID;
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
	 * Completes the Broker's end of the transaction and initiates the appropriate transaction event.<br>
	 * <br>
	 * To be run by the caller when the transaction has been completed. (i.e funds transferred, items moved, etc)
	 *
	 * @return true if the Broker's completion and the transaction event ran, false if it had already been run, or if the transaction was not a success
	 */
	public boolean complete() {
		if (!isSuccess() || onComplete == null) return false;
		onComplete.run();
		onComplete = null;
		return true;
	}

}
