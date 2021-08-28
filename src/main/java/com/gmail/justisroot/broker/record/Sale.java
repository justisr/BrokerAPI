/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker.record;

/**
 * This interface defines the information present for every sale.
 *
 * @param <T> The type of object being transacted.
 */
public interface Sale<T> extends Transaction<T> {

	/**
	 * Was this transaction a listing?<br>
	 * <br>
	 * A listing is a type of sale in which funds are not transferred, because the item is merely "put up" for sale.<br>
	 * Examples of a listing type sale include an item being added to an auction or a shop.<br>
	 * Because the item is not yet sold and funds are not transferred, the {@link #value()} of a listing sale will always be 0.
	 *
	 * @return true if it was a listing type sale, false otherwise
	 */
	boolean isListing();

}
