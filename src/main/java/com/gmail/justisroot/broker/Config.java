/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.gmail.justisroot.broker.Section.HMFFWrapper;

final class Config {

	private final HMFFWrapper file;
	private final Map<String, Integer> brokers = new HashMap<>();

	Config(File configFolder) {
		file = new HMFFWrapper(new File(configFolder.getPath() + File.separator + "config.hmff"));
		String path = "priorities";
		if (!file.getFile().exists()) {
			file.setComments(path, "All implementations may set their own priorty between -128 and 127. To alter the priority of a plugin, specify the priority and identifier here.", "Format:", "#: plugin_id");
			file.set(path + ".200", "autoeconomy");
			file.set(path + ".100", "guishop");
			file.set(path + ".50", "economy++");
			file.set(path + ".25", "FactionsShop");
			file.set(path + ".10", "totaleconomy");
			file.set(path + ".5", "conjurate-shop");
			file.save();
		}
		Section priorities = file.hardGetSection(path);
		for (String key : priorities.getChildren().keySet()) {
			Integer priority;
			try {
				priority = Integer.parseInt(key);
			} catch (NumberFormatException e) {
				System.out.println("[ERROR] Configuration file: " + file.getFile().getPath() + " section \"" + path + "\" key \"" + key + "\" should be an integer value. Skipping...");
				continue;
			}
			String broker = priorities.getString(key);
			brokers.put(broker, priority);
		}
	}

	/**
	 * Returns true if and only if the Broker has a custom priority.
	 *
	 * @param broker The Broker to check for a custom priority
	 * @return true if the Broker has a configured priority, false otherwise
	 */
	final boolean hasCustomPriority(Broker<?> broker) {
		if (broker == null || broker.getId() == null) return false;
		if (brokers.containsKey(broker.getId())) return true;
		return false;
	}

	/**
	 * Get the priority of the provided Broker, taking custom priorities into consideration.
	 *
	 * @param broker The Broker to figure the priority for
	 * @return Integer representation of the broker's priority
	 */
	final Integer getPriority(Broker<?> broker) {
		Objects.requireNonNull(broker, "Broker must not be null");
		if (broker.getId() == null) return (int) broker.getPriority();
		return brokers.containsKey(broker.getId()) ? brokers.get(broker.getId()) : broker.getPriority();
	}
}
