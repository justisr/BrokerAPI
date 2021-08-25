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

interface Transaction<T> {

	/**
	 * Was this transaction a sale?
	 *
	 * @return true if it was a sale, false if it was a purchase.
	 */
	default boolean isSale() {
		return this instanceof Sale;
	}

	/**
	 * Was this transaction a purchase?
	 *
	 * @return true if it was a purchase, false if it was a sale
	 */
	default boolean isPurchase() {
		return this instanceof Purchase;
	}

	/**
	 * Get the object that was transacted.
	 *
	 * @return the object that was transacted
	 */
	T object();

	/**
	 * Get the optional UUID of the player involved in this transaction.
	 *
	 * @return An optional UUID of the player involved in this transaction
	 */
	Optional<UUID> playerID();

	/**
	 * Get the optional UUID of the world involved in this transaction.
	 *
	 * @return An optional UUID of the world involved in this transaction
	 */
	Optional<UUID> worldID();

	/**
	 * Get the volume (amount) of the item which was transacted.
	 *
	 * @return the volume (amount) of the item transacted
	 */
	int volume();

	/**
	 * Get the current recorded amount of money moved either to or from the player's balance as a result of this transaction.
	 *
	 * @return the current recorded amount of money moved as a result of this transaction
	 */
	BigDecimal value();

}
