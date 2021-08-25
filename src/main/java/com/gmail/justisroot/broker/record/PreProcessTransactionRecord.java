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

import com.gmail.justisroot.broker.record.TransactionRecord.TransactionRecordBuilder;

abstract class PreProcessTransactionRecord implements Transaction<Object> {

	private final TransactionRecordBuilder<?> builder;

	PreProcessTransactionRecord(TransactionRecordBuilder<?> builder) {
		this.builder = builder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object object() {
		return builder.object();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Optional<UUID> playerID() {
		return builder.playerID();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Optional<UUID> worldID() {
		return builder.worldID();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int volume() {
		return builder.volume();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BigDecimal value() {
		return builder.value();
	}

}
