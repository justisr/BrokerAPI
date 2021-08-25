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

import com.gmail.justisroot.broker.record.SaleRecord;

public final class SaleMediator<T> extends BrokerMediator<T> {

	SaleMediator(Broker<T> broker, UUID playerID, UUID worldID, T object) {
		super(broker, playerID, worldID, object);
	}

	/**
	 * Can the provided player sell the provided Object in the provided world?
	 *
	 * @return true if the player can sell the object in this world, false @Override if otherwise
	 */
	public final boolean canBeSold() {
		return broker.canBeSold(playerID, worldID, object);
	}

	/**
	 * Get the price the provided player will receive for selling one of the provided Object in the provided world.<br>
	 * Has the same functionality as {@link #getSellPrice(int)} where 'amount' is 1. <br>
	 * <br>
	 * <b>Always use {@link #getSellPrice(int)} when attempting to determine the price of multiple items.</b><br>
	 * Not all Broker implementations will have static prices. Thus, simply multiplying the price of one item by x to get the price of x items may produce incorrect results.<br>
	 * <br>
	 * <b>Never proceed with a sale without having run {@link #sell()}</b><br>
	 * Implementing Brokers may need to be kept informed of when a transaction has taken place in order to maintain themselves and {@link #sell()} is there for that purpose<br>
	 * {@link #sell()} also triggers the transaction events, including the pre-process event, which allows for 3rd party modifications to the {@link TransactionRecord}'s volume and value.<br>
	 * Exchange no more or less than what the {@link TransactionRecord} specifies.
	 *
	 * @return An optional BigDecimal representation of the price the player will be paid as a result of selling, empty if no price is associated with this object
	 */
	public final Optional<BigDecimal> getSellPrice() {
		return getSellPrice(1);
	}

	/**
	 * Get the price the provided player will receive for selling a specified amount of the provided Object in the provided world.<br>
	 * <br>
	 * <b>Always specify the amount of the item you're wishing to receive the total price of</b> <br>
	 * Not all Broker implementations will have static prices. Thus, simply multiplying the price of one item by x to get the price of x items may produce incorrect results.<br>
	 * <br>
	 * <b>Never proceed with a sale without having run {@link #sell()}</b><br>
	 * Implementing Brokers may need to be kept informed of when a transaction has taken place in order to maintain themselves and {@link #sell()} is there for that purpose<br>
	 * {@link #sell()} also triggers the transaction events, including the pre-process event, which allows for 3rd party modifications to the {@link TransactionRecord}'s volume and value.<br>
	 * Exchange no more or less than what the {@link TransactionRecord} specifies.
	 *
	 * @param volume The amount of the object to get the sum sell price of
	 * @return An optional BigDecimal representation of the price the player will be paid as a result of selling, empty if no price is associated with this object
	 */
	public final Optional<BigDecimal> getSellPrice(int volume) {
		return broker.getSellPrice(playerID, worldID, object, volume);
	}

	/**
	 * Have the provided player sell one of the provided Object within the provided world.<br>
	 * <br>
	 * Triggers the transaction pre-process event. Future versions may allow for 3rd party modifications.<br>
	 * Exchange no more or less than what the returned {@link SaleRecord} specifies should be exchanged.<br>
	 * <br>
	 * <b>Do not proceed with the transaction if this record does not denote a success</b><br>
	 * <br>
	 * Call {@link SaleRecord#complete()} once the transaction has been completed. (i.e funds transferred, items moved, etc)<br>
	 * <br>
	 * Has the same functionality as {@link #sell(int)} where 'amount' is 1.
	 *
	 * @return A SaleRecord representing the details of the transaction, including the transaction's success or failure
	 */
	public final SaleRecord<T> sell() {
		return sell(1);
	}

	/**
	 * Have the provided player sell a specified amount of the provided Object in the provided world.<br>
	 * <br>
	 * Triggers the transaction pre-process event. Future versions may allow for 3rd party modifications.<br>
	 * Exchange no more or less than what the returned {@link SaleRecord} specifies should be exchanged.<br>
	 * <br>
	 * <b>Do not proceed with the transaction if this record does not denote a success</b><br>
	 * <br>
	 * Call {@link SaleRecord#complete()} once the transaction has been completed. (i.e funds transferred, items moved, etc).<br>
	 * Do not {@code complete()} the {@link SaleRecord} if the player lacks the object or is otherwise unable to receive payment.<br>
	 *
	 * @param volume The amount of the object to sell
	 * @return A SaleRecord representing the details of the transaction, including the transaction's success or failure
	 */
	public final SaleRecord<T> sell(int volume) {
		return broker.sell(playerID, worldID, object, volume);
	}

}
