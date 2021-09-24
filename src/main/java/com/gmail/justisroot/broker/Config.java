/*
 * BrokerAPI Copyright 2020 Justis Root
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 */
package com.gmail.justisroot.broker;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.gmail.justisroot.hmff.HMFF;
import com.gmail.justisroot.hmff.Section;

final class Config {

	private final HMFF file;

	private final Map<String, Integer> brokers = new HashMap<>();
	private final Set<String> disabled = new HashSet<>();
	private final Set<String> generous = new HashSet<>();

	private static final String PPATH = "priorities";
	private static final String GPATH = "pass-generously";

	Config(File configFolder) {
		file = new HMFF(new File(configFolder.getPath() + File.separator + "config.hmff"));
		if (!file.getFile().exists() || file.getFile().length() < 1) applyDefaults();
		reload();
	}

	private final void applyDefaults() {
		String[] header = {
				"##############",
				"# - Broker - # config.hmff",
				"##############",
				"- hmff syntax for Sublime Text: https://gist.github.com/justisr/6be4be9b7cd1bef1547c7ada26cdbc4d",
				"",
				"Settings for unregistered Brokers are ignored.",
				"",
				"All broker implementations may set their own priorty between -128 and 127. To alter the priority of a broker, specify the priority and identifier here.", "Format:", "plugin_id: #",
				"Enter a non-numeric value to disable use of the broker (e.g: Disabled)"
		};
		file.getOrCreateSection(PPATH).comments().set(header);
		file.set("200", PPATH, "example-high-priority-broker");
		file.set("-200", PPATH,  "example-low-priority-broker");
		file.getOrCreateSection(GPATH).comments().set("",
			"All brokers pass on the object to be transacted to the next highest priority broker, whenever the implementation can't handle that object or that player or world. (e.g a spawner plugin not handling non-spawner items)",
			"However, brokers configured to pass generously will also pass on objects that they can handle, but which the handling of will result in the failure of the transaction. (e.g permissions, location, online availability, etc)");
		file.set("example-broker example-broker2 example-broker3", GPATH);
		file.save();
	}

	final void delete() {
		file.getFile().delete();
	}

	final void reload() {
		file.reload();
		brokers.clear();
		generous.clear();
		disabled.clear();
		Section priorities = file.getOrCreateSection(PPATH);
		for (String key : priorities.children().keySet()) {
			String broker = key;
			try {
				Integer priority = priorities.getInteger(broker).get();
				brokers.put(broker, priority);
			} catch (NumberFormatException e) {
				disabled.add(broker);
				continue;
			}
		}
		for (String broker : file.getOrSetString("example-broker example-broker2 example-broker3", GPATH).split("\\s+")) {
			generous.add(broker);
		}
		file.save();
	}

	final void save() {
		file.save();
	}

	final void setPriority(Broker<?> broker, Integer priorty) {
		file.set(priorty, PPATH, broker.getId());
		brokers.put(broker.getId(), priorty);
	}

	/**
	 * Ensures that there is a configuration entry for this Broker, creates one if necessary.
	 *
	 * @param broker The Broker to ensure the configuration entry for
	 */
	final void ensureEntry(Broker<?> broker) {
		file.getOrSetString(String.valueOf(broker.getPriority()), PPATH, broker.getId());
		save();
	}

	/**
	 * Returns true if and only if the Broker is configured to pass objects generously
	 *
	 * @param broker The Broker to check for being configured to pass generously
	 * @return true if the Broker is configured to pass generously, false otherwise
	 */
	final boolean isGenerous(Broker<?> broker) {
		return generous.contains(broker.getId());
	}

	/**
	 * Returns true if and only if the Broker has been disabled
	 *
	 * @param broker The Broker to check if disabled
	 * @return true if the Broker is disabled, false otherwise
	 */
	final boolean isDisabled(Broker<?> broker) {
		return disabled.contains(broker.getId());
	}

	/**
	 * Returns true if and only if the Broker has a custom priority.
	 *
	 * @param broker The Broker to check for a custom priority
	 * @return true if the Broker has a configured priority, false otherwise
	 */
	final boolean hasCustomPriority(Broker<?> broker) {
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
		return brokers.containsKey(broker.getId()) ? brokers.get(broker.getId()) : broker.getPriority();
	}
}
