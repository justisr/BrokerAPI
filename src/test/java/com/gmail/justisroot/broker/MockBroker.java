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
public abstract class MockBroker implements Broker<String> {

	@Override
	public String getProvider() {
		return "BrokerAPI";
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

	@Override
	public boolean canBeBought(Optional<UUID> playerID, Optional<UUID> worldID, String object) {
		return handlesPurchases(playerID, worldID, object);
	}

	@Override
	public boolean canBeSold(Optional<UUID> playerID, Optional<UUID> worldID, String object) {
		return handlesSales(playerID, worldID, object);
	}

	@Override
	public Optional<BigDecimal> getBuyPrice(Optional<UUID> playerID, Optional<UUID> worldID, String object, int amount) {
		return Optional.of(BigDecimal.valueOf(100));
	}

	@Override
	public Optional<BigDecimal> getSellPrice(Optional<UUID> playerID, Optional<UUID> worldID, String object, int amount) {
		return Optional.of(BigDecimal.valueOf(100));
	}

	@Override
	public TransactionRecord<String> buy(Optional<UUID> playerID, Optional<UUID> worldID, String object, int amount) {
		return TransactionRecord.startPurchase(this, object, playerID, worldID).setValue(BigDecimal.valueOf(amount*getBuyPrice(playerID, worldID, object, amount).get().intValue())).setVolume(amount).buildSuccess(null);
	}

	@Override
	public TransactionRecord<String> sell(Optional<UUID> playerID, Optional<UUID> worldID, String object, int amount) {
		return TransactionRecord.startSale(this, object, playerID, worldID).setValue(BigDecimal.valueOf(amount*getSellPrice(playerID, worldID, object, amount).get().intValue())).setVolume(amount).buildSuccess(null);
	}

	@Override
	public String getDisplayName(Optional<UUID> playerID, Optional<UUID> worldID, String object) {
		return object.toString();
	}


}
