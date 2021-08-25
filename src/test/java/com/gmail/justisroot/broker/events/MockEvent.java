/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker.events;

public final class MockEvent {

	public boolean registrationFired, unregistrationFired, purchaseFired, saleFired, preprocessPurchaseFired, preprocessSaleFired, cancelled;

	public MockEvent() {
		BrokerEventService service = BrokerEventService.current();
		service.setRegistrationHandler(info -> registrationFired = true);
		service.setUnregistrationHandler(info -> unregistrationFired = true);
		service.setPurchaseHandler((info, record) -> purchaseFired = true);
		service.setSaleHandler((info, record) -> saleFired = true);
		service.setPurchasePreProcessHandler((info, record) -> {
			preprocessPurchaseFired = true;
			return cancelled;
		});
		service.setSalePreProcessHandler((info, record) -> {
			preprocessSaleFired = true;
			return cancelled;
		});
	}

}
