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

import com.gmail.justisroot.broker.record.PurchaseRecord;
import com.gmail.justisroot.broker.record.SaleRecord;

/**
 * @author Justis R
 *
 */
public class BigDecimalBroker implements Broker<BigDecimal> {

	public static final String ID = "BigDecimalBroker";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public Class<BigDecimal> getType() {
		return BigDecimal.class;
	}

	@Override
	public String getProvider() {
		return "BrokerAPI";
	}

	@Override
	public boolean handlesPurchases(Optional<UUID> playerID, Optional<UUID> worldID, BigDecimal object) {
		if (object != null) return true;
		return false;
	}

	@Override
	public boolean handlesSales(Optional<UUID> playerID, Optional<UUID> worldID, BigDecimal object) {
		if (object != null) return true;
		return false;
	}

	@Override
	public boolean canBeBought(Optional<UUID> playerID, Optional<UUID> worldID, BigDecimal object) {
		return handlesPurchases(playerID, worldID, object);
	}

	@Override
	public boolean canBeSold(Optional<UUID> playerID, Optional<UUID> worldID, BigDecimal object) {
		return handlesSales(playerID, worldID, object);
	}

	@Override
	public Optional<BigDecimal> getBuyPrice(Optional<UUID> playerID, Optional<UUID> worldID, BigDecimal object, int amount) {
		return Optional.of(BigDecimal.valueOf(100));
	}

	@Override
	public Optional<BigDecimal> getSellPrice(Optional<UUID> playerID, Optional<UUID> worldID, BigDecimal object, int amount) {
		return Optional.of(BigDecimal.valueOf(100));
	}

	@Override
	public PurchaseRecord<BigDecimal> buy(Optional<UUID> playerID, Optional<UUID> worldID, BigDecimal object, int amount) {
		return PurchaseRecord.start(this, object, playerID, worldID).setValue(BigDecimal.valueOf(amount*getBuyPrice(playerID, worldID, object, amount).get().intValue())).setVolume(amount).buildSuccess();
	}

	@Override
	public SaleRecord<BigDecimal> sell(Optional<UUID> playerID, Optional<UUID> worldID, BigDecimal object, int amount) {
		return SaleRecord.start(this, object, playerID, worldID).setValue(BigDecimal.valueOf(amount*getSellPrice(playerID, worldID, object, amount).get().intValue())).setVolume(amount).buildSuccess();
	}

	@Override
	public String getDisplayName(Optional<UUID> playerID, Optional<UUID> worldID, BigDecimal object) {
		return object.toPlainString();
	}

}
