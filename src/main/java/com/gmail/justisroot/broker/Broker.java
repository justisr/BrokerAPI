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
import com.gmail.justisroot.broker.record.SaleRecord;

/**
 * {@link Broker} is the interface through which the implementing and calling party communicate.<br>
 * <br>
 * Implementations of this interface are only used once they have been registered via {@link BrokerAPI#register(Broker)}
 *
 * @param <T> The type that this {@link Broker} transacts
 */
public interface Broker<T> {

	/**
	 * Get the identification String of this Broker implementation.<br>
	 * The identification String must be unique and should indicate the providing software.<br>
	 * The providing software may include multiple active Broker implementations, so long as they are all uniquely identified.<br>
	 * <br>
	 * <b>Identification String must not be empty or contain spaces.</b><br>
	 * <br>
	 * Good identification string examples include:<br>
	 * - PlayerHeadsPlusPlusSkullBroker<br>
	 * - AwesomeShopSpawnerBroker<br>
	 * - AwesomeShopGeneralBroker<br>
	 * - PermissionsStoreBroker
	 *
	 * @return the unique identification String of this Broker implementation
	 */
	String getId();

	/**
	 * Get the identification String of the Broker implementation provider.<br>
	 * This should correspond directly to the name of your plugin, and should be the same across all of your plugin's Broker implementations, if there are multiple.
	 *
	 * @return the identification String of the Broker provider
	 */
	String getProvider();

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
	default byte getPriority() {
		return 0;
	}

	/**
	 * Get the type of Object this Broker transacts.<br>
	 * e.g ItemStack, Permission, Command, etc.
	 *
	 * @return the Class of the Object this Broker is for transacting
	 */
	Class<T> getType();

	/**
	 * Can the provided player purchase the provided Object in the provided world?
	 *
	 * @param playerID An optional UUID for the player attempting to purchase the object
	 * @param worldID An optional UUID of the world that the transaction will take place in
	 * @param object The Object that the player is attempting to purchase
	 * @return true if the player can purchase the object in this world, false if otherwise
	 */
	boolean canBeBought(Optional<UUID> playerID, Optional<UUID> worldID, T object);

	/**
	 * Can the provided player sell the provided Object in the provided world?
	 *
	 * @param playerID An optional UUID for the player attempting to sell the object
	 * @param worldID An optional UUID of the world that the transaction will take place in
	 * @param object The Object that the player is attempting to sell
	 * @return true if the player can sell the object in this world, false if otherwise
	 */
	boolean canBeSold(Optional<UUID> playerID, Optional<UUID> worldID, T object);

	/**
	 * Get the price the provided player will need to pay to obtain the provided amount of the provided Object in the provided world.
	 *
	 * @param playerID An optional UUID for the player attempting to buy the object
	 * @param worldID An optional UUID of the world that the transaction will take place in
	 * @param object The Object that the player is attempting to purchase
	 * @param amount The amount of Objects that the player is attempting to purchase
	 * @return an optional BigDecimal representation of the price the player will need to pay in order to buy these objects, empty if no price is associated with this object, e.g forbidden
	 */
	Optional<BigDecimal> getBuyPrice(Optional<UUID> playerID, Optional<UUID> worldID, T object, int amount);

	/**
	 * Get the price the provided player will receive for selling a specified amount of the provided Object in the provided world.
	 *
	 * @param playerID An optional UUID for the player attempting to sell the object
	 * @param worldID An optional UUID of the world that the transaction will take place in
	 * @param object The Object that the player is attempting to sell
	 * @param amount The amount of Objects that the player is attempting to sell
	 * @return an optional BigDecimal representation of the price the player will be paid as a result of selling, empty if no price is associated with this object, e.g forbidden
	 */
	Optional<BigDecimal> getSellPrice(Optional<UUID> playerID, Optional<UUID> worldID, T object, int amount);

	/**
	 * Have the provided player buy a specified amount of the provided Object in the provided world.<br>
	 * The calling software handles the exchanging of funds and the transacted object.<br>
	 * The Broker implementation handles everything else, such as record keeping, price adjustment, permission updates, UI changes, etc.
	 *
	 * @param playerID An optional UUID for the player whose market will be used for the purchase of the Object
	 * @param worldID An optional UUID of the world in which the transaction is taking place
	 * @param object The Object being transacted
	 * @param amount The amount of stacks being purchased
	 * @return a PurchaseRecord representing the details of the transaction, including the transaction's success or failure
	 */
	PurchaseRecord<T> buy(Optional<UUID> playerID, Optional<UUID> worldID, T object, int amount);

	/**
	 * Have the provided player sell a specified amount of the provided Object in the provided world.<br>
	 * The implementing plugin handles all aspects of the transaction including redistribution of player funds and distribution of the transacted Object.
	 *
	 * @param playerID An optional UUID for the player whose market will be used for the sale of the Object
	 * @param worldID An optional UUID of the world in which the transaction is taking place
	 * @param object The Object being transacted
	 * @param amount The amount of stacks being sold
	 * @return a SaleRecord representing the details of the transaction, including the transaction's success or failure
	 */
	public SaleRecord<T> sell(Optional<UUID> playerID, Optional<UUID> worldID, T object, int amount);

	/**
	 * Get a player friendly display name for the provided Object.<br>
	 * <br>
	 * Where applicable, display names should be singular, title case, contain no ChatColors be and appropriate for use in a context such as an inventory GUI or sending the message:<br>
	 * "Display Name | Now only $5!"<br>
	 * e.g "Diamond Block", "Portal Gun", "Permission to Fly", "Gold Kit"
	 *
	 * @param playerID An optional UUID for the player who will be displayed this name
	 * @param worldID An optional UUID of the world in which the player will be displayed this name
	 * @param object The Object to get a display name for
	 * @return a player-friendly display name for the provided Object
	 */
	String getDisplayName(Optional<UUID> playerID, Optional<UUID> worldID, T object);

	/**
	 * Returns whether or not this Broker implementation is intended to handle purchased of the provided object for the provided player in the provided world.<br>
	 * Returning false will allow the Object to be passed down to the Broker implementation next in line for handling Objects of this type.<br>
	 * <br>
	 * An example of correct implementation would be a Minecraft spawner plugin that exclusively handles spawner Itemstacks returning false for non-spawner ItemStacks.<br>
	 * If the spawner plugin incorrectly returned true here, but then proceeded to forbid the transaction of any non-spawner items, players would be unable to transact any non-spawner items even if another Broker
	 * implementation of a lower priority was installed for that purpose.
	 *
	 * @param playerID An optional UUID for the player that will be purchasing the Object
	 * @param worldID An optional UUID of the world that the purchase will take place in
	 * @param object The Object to be purchased
	 * @return true if this Broker implementation wishes to handle purchases of this object
	 */
	boolean handlesPurchases(Optional<UUID> playerID, Optional<UUID> worldID, T object);

	/**
	 * Returns whether or not this Broker implementation is intended to handle sales of the provided object for the provided player in the provided world.<br>
	 * Returning false will allow the Object to be passed down to the Broker implementation next in line for handling Objects of this type.<br>
	 * <br>
	 * An example of correct implementation would be a Minecraft spawner plugin that exclusively handles spawner Itemstacks returning false for non-spawner ItemStacks.<br>
	 * If the spawner plugin incorrectly returned true here, but then proceeded to forbid the transaction of any non-spawner items, players would be unable to transact any non-spawner items even if another Broker
	 * implementation of a lower priority was installed for that purpose.
	 *
	 * @param playerID An optional UUID for the player that will be selling the Object
	 * @param worldID An optional UUID of the world that the sale will take place in
	 * @param object The Object to be sold
	 * @return true if this Broker implementation wishes to handle sales of this object
	 */
	boolean handlesSales(Optional<UUID> playerID, Optional<UUID> worldID, T object);

}
