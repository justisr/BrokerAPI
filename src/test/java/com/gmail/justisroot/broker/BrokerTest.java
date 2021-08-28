/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class BrokerTest {

	private static File folder;

	static {
		try {
			folder = new File(new File(BrokerTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + File.separator);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private final Config config = new Config(folder);
	private final BrokerAPI api = new BrokerAPI(config);

	private final IntegerBroker intBroker = new IntegerBroker();
	private final NaNBroker nanBroker = new NaNBroker();
	private final AlphanumericBroker anBroker = new AlphanumericBroker();
	private final BigDecimalBroker bigDBroker = new BigDecimalBroker();

	@BeforeAll
	@DisplayName("Register MockBrokers")
	void registerBrokers() {
		api.register(intBroker);
		api.register(nanBroker);
	}

	@AfterAll
	@DisplayName("Clean up")
	void removeConfig() {
		config.delete();
	}

	@Test
	@Order(0)
	@DisplayName("Verify NaNs go to NaNBroker")
	void nanTest() {
		Optional<PurchaseMediator<String>> optional = api.forPurchase(UUID.randomUUID(), UUID.randomUUID(), "NotAN");
		assertTrue(optional.isPresent(), "A Broker failed to be provided");
		String id = optional.get().getBrokerInfo().id();
		assertTrue(id.equals(NaNBroker.ID), id + " was returned for a NaN value when NaNBroker was expected");
	}

	@Test
	@Order(1)
	@DisplayName("Verify string Integers go to IntegerBroker")
	void integerTest() {
		Optional<PurchaseMediator<String>> optional = api.forPurchase(UUID.randomUUID(), UUID.randomUUID(), "1");
		assertTrue(optional.isPresent(), "A Broker failed to be provided");
		String id = optional.get().getBrokerInfo().id();
		assertTrue(id.equals(IntegerBroker.ID),  id + " was returned for an Integer value when IntegerBroker was expected");
	}

	@Test
	@Order(2)
	@DisplayName("Verify string Doubles do not return a Broker")
	void doubleTest() {
		Optional<PurchaseMediator<String>> optional = api.forPurchase(UUID.randomUUID(), UUID.randomUUID(), "0.1");
		assertTrue(optional.isEmpty(), () -> optional.get().getBrokerInfo().id() + " was returned for a Double value when no return was expected");
	}

	@Test
	@Order(3)
	@DisplayName("Verify non-string BigDecimals do not return a Broker")
	void nonStringTest() {
		Optional<PurchaseMediator<BigDecimal>> optional = api.forPurchase(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ZERO);
		assertTrue(optional.isEmpty(), () -> optional.get().getBrokerInfo().id() + " was returned for a non-string when no return was expected");
	}

	@Test
	@Order(4)
	@DisplayName("Register BigDecimal Broker and ensure that it is returned for BigDecimals")
	void bigDecimalTest() {
		api.register(bigDBroker);
		Optional<PurchaseMediator<BigDecimal>> optional = api.forPurchase(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ZERO);
		assertTrue(optional.isPresent(), "A Broker failed to be provided");
		String id = optional.get().getBrokerInfo().id();
		assertTrue(id.equals(BigDecimalBroker.ID), id + " was returned for a BigDecimal value when BigDecimalBroker was expected");
	}

	@Test
	@Order(5)
	@DisplayName("Register AlphanumericBroker with a lower priority and verify that NaNBroker continues to be received for Alhpanumeric strings")
	void lowerPriorityTest() {
		api.register(anBroker);
		Optional<PurchaseMediator<String>> optional = api.forPurchase(UUID.randomUUID(), UUID.randomUUID(), "NotAN");
		assertTrue(optional.isPresent(), "A Broker failed to be provided");
		String id = optional.get().getBrokerInfo().id();
		assertTrue(id.equals(NaNBroker.ID), id + " was returned for a NaN value when NaNBroker was expected");
	}

	@Test
	@Order(6)
	@DisplayName("Unregister AlphanumericBroker")
	void unregistertest() {
		api.unregister(AlphanumericBroker.ID);
		assertTrue(!api.isRegistered(AlphanumericBroker.ID), "AlphanumericBroker is still registered when it should have been unregistered");
	}

	@Test
	@Order(7)
	@DisplayName("Register AlphanumericBroker with a higher priority and verify that it is received for Alhpanumeric strings")
	void higherPriorityTest() {
		config.setPriority(anBroker, 10);
		api.register(anBroker);
		Optional<PurchaseMediator<String>> optional = api.forPurchase(UUID.randomUUID(), UUID.randomUUID(), "NotAN");
		assertTrue(optional.isPresent(), "A Broker failed to be provided");
		String id = optional.get().getBrokerInfo().id();
		assertTrue(id.equals(AlphanumericBroker.ID), id + " was returned for a NaN value when AlphanumericBroker was expected");
		api.unregister(AlphanumericBroker.ID);
	}

}
