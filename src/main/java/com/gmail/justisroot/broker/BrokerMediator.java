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

import java.util.Optional;
import java.util.UUID;

/**
 * @author Justis R
 *
 */
abstract class BrokerMediator<T> {

	protected final Broker<T> broker;
	protected final BrokerInfo info;
	protected final Optional<UUID> playerID, worldID;
	protected final T object;

	BrokerMediator(Broker<T> broker, UUID playerID, UUID worldID, T object) {
		this.broker = broker;
		this.info = BrokerInfo.get(broker);
		this.playerID = Optional.ofNullable(playerID);
		this.worldID = Optional.ofNullable(worldID);
		this.object = object;
	}

	/**
	 * Get the BrokerInfo for the provided Broker.
	 *
	 * @return the BrokerInfo associated with the provided Broker.
	 */
	public final BrokerInfo getBrokerInfo() {
		return info;
	}

	/**
	 * Get the optional UUID of the player making the transaction.
	 *
	 * @return An optional UUID of the player making the transaction
	 */
	public final Optional<UUID> getPlayerID() {
		return playerID;
	}

	/**
	 * Get the optional UUID of the world that this transaction is taking place in.
	 *
	 * @return An optional UUID of the world being transacted in
	 */
	public final Optional<UUID> getWorldID() {
		return worldID;
	}

	/**
	 * Get the Object that is being transacted.
	 *
	 * @return the object of this transaction
	 */
	public final T getObject() {
		return object;
	}

	/**
	 * Get a player friendly display name for the provided Object.<br>
	 * <br>
	 * Where applicable, display names should be singular, title case, contain no ChatColors be and appropriate for use in a context such as an inventory GUI or sending the message:<br>
	 * "Display Name | Now only $5!"<br>
	 * e.g "Diamond Block", "Portal Gun", "Permission to Fly", "Gold Kit"
	 *
	 * @return a player-friendly display name for the provided Object
	 */
	public final String getDisplayName() {
		return broker.getDisplayName(playerID, worldID, object);
	}

}
