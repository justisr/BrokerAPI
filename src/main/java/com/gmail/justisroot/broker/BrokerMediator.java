/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Justis R
 *
 */
public final class BrokerMediator<T> {

	private final Broker<T> broker;
	private final UUID playerID, worldID;
	private final T object;

	BrokerMediator(Broker<T> broker, UUID playerID, UUID worldID, T object) {
		this.broker = broker;
		this.playerID = playerID;
		this.worldID = worldID;
		this.object = object;
	}

	/**
	 * Get the name of the provider of this Broker's implementation.
	 *
	 * @return the name of this Broker's provider
	 */
	public final String provider() {
		return broker.getProvider();
	}

	/**
	 * Get the UUID of the player making the transaction.
	 *
	 * @return the UUID of the player making the transaction
	 */
	public final UUID getPlayerID() {
		return playerID;
	}

	/**
	 * Get the UUID of the world that this transaction is taking place in.
	 *
	 * @return the UUID of the world being transacted in
	 */
	public final UUID getWorldID() {
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
	 * Can the provided player purchase the provided Object in the provided world?
	 *
	 * @return true if the player can purchase the object in this world, false if otherwise
	 */
	public final boolean canBeBought() {
		return broker.canBeBought(playerID, worldID, object);
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
	 * Get the price the provided player will need to pay to obtain one of the provided Object in the provided world.<br>
	 * Has the same functionality as {@link #getBuyPrice(int)} where 'amount' is 1.<br>
	 * <br>
	 * <b>Always use {@link #getBuyPrice(int)} when attempting to determine the cost of multiple items.</b><br>
	 * Not all Broker implementations will have static prices. Thus, simply multiplying the price of one item by x to get the price of x items may produce incorrect results.
	 *
	 * @return An optional BigDecimal representation of the price the player will need to pay in order to buy the object, empty if no price is associated with this object
	 */
	public final Optional<BigDecimal> getBuyPrice() {
		return getBuyPrice(1);
	}

	/**
	 * Get the price the provided player will receive for selling one of the provided Object in the provided world.<br>
	 * Has the same functionality as {@link #getSellPrice(int)} where 'amount' is 1. <br>
	 * <br>
	 * <b>Always use {@link #getSelPrice(int)} when attempting to determine the price of multiple items.</b><br>
	 * Not all Broker implementations will have static prices. Thus, simply multiplying the price of one item by x to get the price of x items may produce incorrect results.
	 *
	 * @return An optional BigDecimal representation of the price the player will be paid as a result of selling, empty if no price is associated with this object
	 */
	public final Optional<BigDecimal> getSellPrice() {
		return getSellPrice(1);
	}

	/**
	 * Get the price the provided player will need to pay to obtain the provided amount of the provided Object in the provided world.<br>
	 * <br>
	 * <b>Always specify the amount of the item you're wishing to receive the total price of</b> <br>
	 * Not all Broker implementations will have static prices. Thus, simply multiplying the price of one item by x to get the price of x items may produce incorrect results.
	 *
	 * @return An optional BigDecimal representation of the price the player will need to pay in order to buy these objects, empty if no price is associated with this object
	 */
	public final Optional<BigDecimal> getBuyPrice(int amount) {
		return broker.getSellPrice(playerID, worldID, object, amount);
	}

	/**
	 * Get the price the provided player will receive for selling a specified amount of the provided Object in the provided world.<br>
	 * <br>
	 * <b>Always specify the amount of the item you're wishing to receive the total price of</b> <br>
	 * Not all Broker implementations will have static prices. Thus, simply multiplying the price of one item by x to get the price of x items may produce incorrect results.
	 *
	 * @return An optional BigDecimal representation of the price the player will be paid as a result of selling, empty if no price is associated with this object
	 */
	public final Optional<BigDecimal> getSellPrice(int amount) {
		return broker.getSellPrice(playerID, worldID, object, amount);
	}

	/**
	 * Have the provided player buy one of the provided Object in the provided world.<br>
	 * The implementing plugin handles all aspects of the transaction including redistribution of player funds and distribution of the transacted Object.<br>
	 * Has the same functionality as {@link #buy(int)} where 'amount' is 1.
	 */
	public final TransactionRecord<T> buy() {
		return buy(1);
	}

	/**
	 * Have the provided player buy a specified amount of the provided Object in the provided world.<br>
	 * The implementing plugin handles all aspects of the transaction including redistribution of player funds and distribution of the transacted object.
	 */
	public final TransactionRecord<T> buy(int amount) {
		TransactionRecord<T> record = broker.buy(playerID, worldID, object, amount);
		if (record.failReason().isEmpty())
			BrokerAPI.instance().eventService().createTransactionEvent(broker, record);
		return record;
	}

	/**
	 * Have the provided player sell one of the provided Object within the provided world.<br>
	 * The implementing plugin handles all aspects of the transaction including redistribution of player funds and distribution of the transacted Object.<br>
	 * Has the same functionality as {@link #sell(int)} where 'amount' is 1.
	 */
	public final TransactionRecord<T> sell() {
		return sell(1);
	}

	/**
	 * Have the provided player sell a specified amount of the provided Object in the provided world.<br>
	 * The implementing plugin handles all aspects of the transaction including redistribution of player funds and distribution of the transacted Object.
	 *
	 * @param amount The amount of stacks being sold
	 */
	public final TransactionRecord<T> sell(int amount) {
		TransactionRecord<T> record =  broker.sell(playerID, worldID, object, amount);
		if (record.failReason().isEmpty())
			BrokerAPI.instance().eventService().createTransactionEvent(broker, record);
		return record;
	}

	/**
	 * Get a player friendly display name for the provided Object.<br>
	 * <br>
	 * Where applicable, display names should be singular, title case, contain no ChatColors be and appropriate for use in a context such as an inventory GUI or sending the message:<br>
	 * "{display-name} | Now only $5!"<br>
	 * e.g "Diamond", "Portal Gun", "Permission to Fly", "Gold Kit"
	 *
	 * @return a player-friendly display name for the provided Object
	 */
	public final String getDisplayName() {
		return broker.getDisplayName(playerID, worldID, object);
	}

}
