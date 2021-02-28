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

public interface Broker<T> {

	/**
	 * Get the identification String of this Broker implementation.<br>
	 * Identification String must be unique and may or may not directly correspond to the name of the providing software.<br>
	 * The providing software may include multiple active Broker implementations, so long as they are all uniquely named.<br>
	 * <br>
	 * <b>Identification String must not be empty or contain spaces.<br>
	 *
	 * @return the unique identification String of this Broker implementation
	 */
	public String getId();

	/**
	 * Get the identification String of the Broker implementation provider.<br>
	 * This should correspond directly to the name of your plugin, and should be the same across all of your plugin's Broker implementations, if there are multiple.
	 *
	 * @return the identification String of the Broker implementation provider
	 */
	public String getProvider();

	/**
	 * Get the self-assigned priority of this Broker implementation.<br>
	 * This priority may be overwritten by end user configuration settings.<br>
	 * <br>
	 * Higher priority Broker implementations get asked to handle the transacted Object first.<br>
	 * If it is rejected, the Object will instead be passed down to lower priority Broker implementations.<br>
	 * Thus, more specific or restrictive Broker implementations should be a higher priority than general catch-all implementations.<br>
	 * <br>
	 * An example of a good high priority Broker would be a Minecraft spawner plugin exclusively transacting spawner ItemStacks.<br>
	 * If the ItemStack Object it receives is not a spawner, it can can and should reject handling the ItemStack. At which point the Object would be passed to the next priority Broker, potentially an item shop.<br>
	 * If the general item shop had been a higher priority and it handled spawner items by forbidding their transaction, the player would not be able to transact spawners despite there being a Broker implementation
	 * installed for that purpose, because the general shop would have taken priority over everything.
	 *
	 * @return the self-assigned priority of this Broker
	 */
	public default byte getPriority() {
		return 0;
	}

	/**
	 * Get the type of Object this Broker is for transacting.<br>
	 * e.g ItemStack, Permission, Command, etc.
	 *
	 * @return the Class of the Object this Broker is for transacting
	 */
	public Class<T> getType();

	/**
	 * Returns whether or not this Broker implementation is intended to handle the provided object for the provided player in the provided world.<br>
	 * Returning false will allow the Object to be passed down to the Broker implementation next in line for handling Objects of this type.<br>
	 * <br>
	 * An example of correct implementation would be a Minecraft spawner plugin that exclusively handles spawner Itemstacks returning false for non-spawner ItemStacks.<br>
	 * If the spawner plugin incorrectly returned true here, but then proceeded to forbid the transaction of any non-spawner items, players would be unable to transact any non-spawner items even if another Broker
	 * implementation of a lower priority was installed for that purpose.
	 *
	 * @param playerID The UUID for the player that will be transacting the Object
	 * @param worldID The UUID of the world that the transaction will take place in
	 * @param object the Object to be transacted
	 * @return true if this Broker implementation wishes to handle this object
	 */
	public boolean handles(UUID playerID, UUID worldID, T object);

	/**
	 * Can the provided player purchase the provided Object in the provided world?
	 *
	 * @param playerID The UUID for the player attempting to purchase the object
	 * @param worldID The UUID of the world that the transaction will take place in
	 * @param object The Object that the player is attempting to purchase
	 * @return true if the player can purchase the object in this world, false if otherwise
	 */
	public boolean canBeBought(UUID playerID, UUID worldID, T object);

	/**
	 * Can the provided player sell the provided Object in the provided world?
	 *
	 * @param playerID The UUID for the player attempting to sell the object
	 * @param worldID The UUID of the world that the transaction will take place in
	 * @param object The Object that the player is attempting to sell
	 * @return true if the player can sell the object in this world, false @Override if otherwise
	 */
	public boolean canBeSold(UUID playerID, UUID worldID, T object);

	/**
	 * Get the price the provided player will need to pay to obtain the provided amount of the provided Object in the provided world.
	 *
	 * @param playerID The UUID for the player attempting to buy the object
	 * @param worldID The UUID of the world that the transaction will take place in
	 * @param object The Object that the player is attempting to purchase
	 * @param amount The amount of Objects that the player is attempting to purchase
	 * @return An optional BigDecimal representation of the price the player will need to pay in order to buy these objects, empty if no price is associated with this object, e.g forbidden
	 */
	public Optional<BigDecimal> getBuyPrice(UUID playerID, UUID worldID, T object, int amount);

	/**
	 * Get the price the provided player will receive for selling a specified amount of the provided Object in the provided world.
	 *
	 * @param playerID The UUID for the player attempting to sell the object
	 * @param worldID The UUID of the world that the transaction will take place in
	 * @param object The Object that the player is attempting to sell
	 * @param amount The amount of Objects that the player is attempting to sell
	 * @return An optional BigDecimal representation of the price the player will be paid as a result of selling, empty if no price is associated with this object, e.g forbidden
	 */
	public Optional<BigDecimal> getSellPrice(UUID playerID, UUID worldID, T object, int amount);

	/**
	 * Have the provided player buy a specified amount of the provided Object in the provided world.<br>
	 * The implementing plugin handles all aspects of the transaction including redistribution of player funds and distribution of the transacted object.
	 *
	 * @param playerID The UUID for the player whose market will be used for the purchase of the Object
	 * @param worldID The UUID of the world in which the transaction is taking place
	 * @param object The Object being transacted
	 * @param amount The amount of stacks being purchased
	 */
	public TransactionRecord<T> buy(UUID playerID, UUID worldID, T object, int amount);

	/**
	 * Have the provided player sell a specified amount of the provided Object in the provided world.<br>
	 * The implementing plugin handles all aspects of the transaction including redistribution of player funds and distribution of the transacted Object.
	 *
	 * @param playerID The UUID for the player whose market will be used for the sale of the Object
	 * @param worldID The UUID of the world in which the transaction is taking place
	 * @param object The Object being transacted
	 * @param amount The amount of stacks being sold
	 */
	public TransactionRecord<T> sell(UUID playerID, UUID worldID, T object, int amount);

	/**
	 * Get a player friendly display name for the provided Object.<br>
	 * <br>
	 * Where applicable, display names should be singular, title case, contain no ChatColors be and appropriate for use in a context such as an inventory GUI or sending the message:<br>
	 * "{display-name} | Now only $5!"<br>
	 * e.g "Diamond", "Portal Gun", "Permission to Fly", "Gold Kit"
	 *
	 * @param playerID The UUID for the player who will be displayed this name
	 * @param worldID The UUID of the world in which the player will be displayed this name
	 * @param object The Object to get a display name for
	 * @return a player-friendly display name for the provided Object
	 */
	public String getDisplayName(UUID playerID, UUID worldID, T object);

}
