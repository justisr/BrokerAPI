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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Justis R
 *
 */
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

	private boolean registrationFired, unregistrationFired, transactionFired, preprocessTransactionFired, cancelled;


	@BeforeAll
	@DisplayName("Register event handlers")
	void registerEventHandlers() {
		api.eventService().setRegistrationHandler(info -> registrationFired = true);
		api.eventService().setUnregistrationHandler(info -> unregistrationFired = true);
		api.eventService().setTransactionHandler((info, record) -> transactionFired = true);
		api.eventService().setPreProcessTransactionHandler((info, record) -> {
			preprocessTransactionFired = true;
			return cancelled;
		});
	}

	@BeforeAll
	@DisplayName("Resetting Configuration")
	void resetConfig() {
		config.clear();
		config.reload();
	}

	@Test
	@Order(0)
	@DisplayName("Test Broker registration event")
	void registrationEventTest() {
		registrationFired = false;
		api.register(intBroker);
		assertTrue(registrationFired, "IntBroker registration was complete, but was not flagged as fired");
		registrationFired = false;
		api.register(nanBroker);
		assertTrue(registrationFired, "NaNBroker registration was complete, but was not flagged as fired");
	}

	@Test
	@Order(1)
	@DisplayName("Test Broker unregistration event")
	void unregistrationEventTest() {
		api.register(anBroker);
		unregistrationFired = false;
		api.unregister(anBroker);
		assertTrue(unregistrationFired, "AlphanumericBroker was unregistired, but was not flagged as fired");
	}

	@Test
	@Order(2)
	@DisplayName("Test Broker transaction events")
	void transactionEventsTest() {
		transactionFired = false;
		preprocessTransactionFired = false;
		Optional<PurchaseMediator<String>> bp = api.forPurchase(UUID.randomUUID(), UUID.randomUUID(), "1");
		assertTrue(bp.isPresent(), "No Broker was returned for string \"1\"");
		assertTrue(bp.get().getBrokerInfo().id().equals(IntegerBroker.ID), "IntegerBroker was expected but was not returned. " + bp.get().getBrokerInfo().id() + " returned instead");
		cancelled = true;
		bp.get().buy();
		assertTrue(preprocessTransactionFired, "The transaction was started, but the preprocess event didn't get marked as fired");
		assertTrue(!transactionFired, "The transaction should have been cancelled, but the event was still marked as fired");
		cancelled = false;
		bp.get().buy();
		assertTrue(transactionFired, "The transaction was fired without being cancelled, but the transaction event was not marked as fired");
	}

}
