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

import com.gmail.justisroot.broker.events.MockEvent;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class BrokerEventTest {

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
	private final MockEvent event = new MockEvent();

	@BeforeAll
	@DisplayName("Register event handlers")
	void registerEventHandlers() {

	}

	@AfterAll
	@DisplayName("Resetting Configuration")
	void resetConfig() {
		config.delete();
	}

	@Test
	@Order(0)
	@DisplayName("Test Broker registration event")
	void registrationEventTest() {
		event.registrationFired = false;
		api.register(intBroker);
		assertTrue(event.registrationFired, "IntBroker registration was complete, but was not flagged as fired");
		event.registrationFired = false;
		api.register(nanBroker);
		assertTrue(event.registrationFired, "NaNBroker registration was complete, but was not flagged as fired");
	}

	@Test
	@Order(1)
	@DisplayName("Test Broker unregistration event")
	void unregistrationEventTest() {
		api.register(anBroker);
		event.unregistrationFired = false;
		api.unregister(anBroker);
		assertTrue(event.unregistrationFired, "AlphanumericBroker was unregistired, but was not flagged as fired");
	}

	@Test
	@Order(2)
	@DisplayName("Test Broker purchase events")
	void purchaseEventsTest() {
		event.purchaseFired = false;
		event.preprocessPurchaseFired = false;
		Optional<PurchaseMediator<String>> bp = api.forPurchase(UUID.randomUUID(), UUID.randomUUID(), "1");
		assertTrue(bp.isPresent(), "No Broker was returned for string \"1\"");
		assertTrue(bp.get().getBrokerInfo().id().equals(IntegerBroker.ID), "IntegerBroker was expected but was not returned. " + bp.get().getBrokerInfo().id() + " returned instead");
		event.cancelled = true;
		bp.get().buy().complete();
		assertTrue(event.preprocessPurchaseFired, "The purchase was started, but the preprocess event didn't get marked as fired");
		assertTrue(!event.purchaseFired, "The purchase should have been cancelled, but the event was still marked as fired");
		event.cancelled = false;
		bp.get().buy().complete();
		assertTrue(event.purchaseFired, "The purchase was fired without being cancelled, but the transaction event was not marked as fired");
	}

	@Test
	@Order(3)
	@DisplayName("Test Broker sale events")
	void saleEventsTest() {
		event.saleFired = false;
		event.preprocessSaleFired = false;
		Optional<SaleMediator<String>> bp = api.forSale(UUID.randomUUID(), UUID.randomUUID(), "1");
		assertTrue(bp.isPresent(), "No Broker was returned for string \"1\"");
		assertTrue(bp.get().getBrokerInfo().id().equals(IntegerBroker.ID), "IntegerBroker was expected but was not returned. " + bp.get().getBrokerInfo().id() + " returned instead");
		event.cancelled = true;
		bp.get().sell().complete();
		assertTrue(event.preprocessSaleFired, "The sale was started, but the preprocess event didn't get marked as fired");
		assertTrue(!event.saleFired, "The sale should have been cancelled, but the event was still marked as fired");
		event.cancelled = false;
		bp.get().sell().complete();
		assertTrue(event.saleFired, "The sale was fired without being cancelled, but the transaction event was not marked as fired");
	}


}
