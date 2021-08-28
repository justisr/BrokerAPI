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

import com.gmail.justisroot.broker.record.PurchaseRecord;

/**
 * The medium through which callers interact with their returned {@link Broker}.<br>
 * <br>
 * This class ensures that the {@link Broker} returned by {@link BrokerAPI} is not exposed, so that only the provided IDs and object instance may be used to transact through it.
 * This is important, because the {@link Broker} implementation and end user configuration determine which implementation should be used for which objects.
 *
 * @param <T> The type of object being transacted
 */
public final class PurchaseMediator<T> extends BrokerMediator<T> {

	PurchaseMediator(Broker<T> broker, UUID playerID, UUID worldID, T object) {
		super(broker, playerID, worldID, object);
	}

	/**
	 * Can the provided player purchase the provided Object in the provided world?
	 *
	 * @return true if the player can purchase the object in this world, false if otherwise
	 */
	public final boolean canBeBought() {
		return broker.canBeBought(playerID, worldID, object);
	}

	/**
	 * Get the price the provided player will need to pay to obtain one of the provided Object in the provided world.<br>
	 * Has the same functionality as {@link #getBuyPrice(int)} where 'amount' is 1.<br>
	 * <br>
	 * <b>Always use {@link #getBuyPrice(int)} when attempting to determine the cost of multiple items.</b><br>
	 * Not all Broker implementations will have static prices. Thus, simply multiplying the price of one item by x to get the price of x items may produce incorrect results.<br>
	 * <br>
	 * <b>Never proceed with a purchase without having run {@link #buy()} which will return a PurchaseRecord with the details you must use for your transaction.</b><br>
	 * Implementing Brokers may need to be kept informed of when a transaction has taken place in order to maintain themselves and {@link #buy()} is there for that purpose<br>
	 * {@link #buy()} also triggers the transaction events, including the pre-process event, which allows for 3rd party modifications to the {@link PurchaseRecord}'s volume and value.<br>
	 * Exchange no more or less than what the {@link PurchaseRecord} specifies.
	 *
	 * @return An optional BigDecimal representation of the price the player will need to pay in order to buy the object, empty if no price is associated with this object
	 */
	public final Optional<BigDecimal> getBuyPrice() {
		return getBuyPrice(1);
	}

	/**
	 * Get the price the provided player will need to pay to obtain the provided amount of the provided Object in the provided world.<br>
	 * <br>
	 * <b>Always specify the amount of the item you're wishing to receive the total price of</b> <br>
	 * Not all Broker implementations will have static prices. Thus, simply multiplying the price of one item by x to get the price of x items may produce incorrect results.<br>
	 * <br>
	 * <b>Never proceed with a purchase without having run {@link #buy()}</b><br>
	 * Implementing Brokers may need to be kept informed of when a transaction has taken place in order to maintain themselves and {@link #buy()} is there for that purpose<br>
	 * {@link #buy()} also triggers the transaction events, including the pre-process event, which allows for 3rd party modifications to the {@link PurchaseRecord}'s volume and value.<br>
	 * Exchange no more or less than what the {@link PurchaseRecord} specifies.
	 *
	 * @param volume The amount of the object to get the sum buy price of
	 * @return An optional BigDecimal representation of the price the player will need to pay in order to buy these objects, empty if no price is associated with this object
	 */
	public final Optional<BigDecimal> getBuyPrice(int volume) {
		return broker.getBuyPrice(playerID, worldID, object, volume);
	}

	/**
	 * Have the provided player buy one of the provided Object in the provided world.<br>
	 * <br>
	 * Triggers the transaction pre-process event. Future versions may allow for 3rd party modifications.<br>
	 * Exchange no more or less than what the returned {@link PurchaseRecord} specifies should be exchanged.<br>
	 * <br>
	 * <b>Do not proceed with the transaction if this record does not denote a success</b><br>
	 * <br>
	 * Call {@link PurchaseRecord#complete()} once the transaction has been completed. (i.e funds transferred, items moved, etc)<br>
	 * <br>
	 * Has the same functionality as {@link #buy(int)} where 'amount' is 1.
	 *
	 * @return A PurchaseRecord representing the details of the transaction, including the transaction's success or failure
	 */
	public final PurchaseRecord<T> buy() {
		return buy(1);
	}

	/**
	 * Have the provided player buy a specified amount of the provided Object in the provided world.<br>
	 * <br>
	 * Triggers the transaction pre-process event. Future versions may allow for 3rd party modifications..<br>
	 * Exchange no more or less than what the returned {@link PurchaseRecord} specifies should be exchanged.<br>
	 * <br>
	 * <b>Do not proceed with the transaction if this record does not denote a success</b><br>
	 * <br>
	 * Call {@link PurchaseRecord#complete()} once the transaction has been completed. (i.e funds transferred, items moved, etc)
	 *
	 * @param volume The amount of the object to buy
	 * @return A PurchaseRecord representing the details of the transaction, including the transaction's success or failure
	 */
	public final PurchaseRecord<T> buy(int volume) {
		return broker.buy(playerID, worldID, object, volume);
	}


}
