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
	public boolean handlesPurchases(UUID playerID, UUID worldID, BigDecimal object) {
		if (object != null) return true;
		return false;
	}

	@Override
	public boolean handlesSales(UUID playerID, UUID worldID, BigDecimal object) {
		if (object != null) return true;
		return false;
	}

	@Override
	public boolean canBeBought(UUID playerID, UUID worldID, BigDecimal object) {
		return handlesPurchases(playerID, worldID, object);
	}

	@Override
	public boolean canBeSold(UUID playerID, UUID worldID, BigDecimal object) {
		return handlesSales(playerID, worldID, object);
	}

	@Override
	public Optional<BigDecimal> getBuyPrice(UUID playerID, UUID worldID, BigDecimal object, int amount) {
		return Optional.of(BigDecimal.valueOf(100));
	}

	@Override
	public Optional<BigDecimal> getSellPrice(UUID playerID, UUID worldID, BigDecimal object, int amount) {
		return Optional.of(BigDecimal.valueOf(100));
	}

	@Override
	public TransactionRecord<BigDecimal> buy(UUID playerID, UUID worldID, BigDecimal object, int amount) {
		return TransactionRecord.startPurchase(this, object, playerID, worldID).setValue(BigDecimal.valueOf(amount*getBuyPrice(playerID, worldID, object, amount).get().intValue())).setVolume(amount).buildSuccess(null);
	}

	@Override
	public TransactionRecord<BigDecimal> sell(UUID playerID, UUID worldID, BigDecimal object, int amount) {
		return TransactionRecord.startSale(this, object, playerID, worldID).setValue(BigDecimal.valueOf(amount*getSellPrice(playerID, worldID, object, amount).get().intValue())).setVolume(amount).buildSuccess(null);
	}

	@Override
	public String getDisplayName(UUID playerID, UUID worldID, BigDecimal object) {
		return object.toPlainString();
	}

}
