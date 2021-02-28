/*
 *
 * BrokerAPI Copyright 2020 Justis Root
 *
 * This program is distributed under the terms of the GNU Lesser General Public License
 *
 */
package com.gmail.justisroot.broker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bstats.charts.CustomChart;
import org.bstats.charts.DrilldownPie;

/**
 * @author Justis R
 *
 */
abstract class BrokerMetrics {

	private BrokerMetrics() { }

	private static final Set<CustomChart> CHARTS = new HashSet<>();

	static {
		CHARTS.add(new DrilldownPie("broker_implementations", () -> {
			Map<String, Map<String, Integer>> data = new HashMap<>();
			for (Entry<Class<?>, SimilarBrokers<?>> entry : BrokerAPI.instance().brokers().entrySet()) {
				Map<String, Integer> meta = new HashMap<>();
				Iterator<?> iterator = entry.getValue().iterator();
				while (iterator.hasNext()) meta.put(((PrioritizedBroker<?, ?>) iterator.next()).get().getProvider(), 1);
				data.put(entry.getKey().getSimpleName(), meta);
			}
			return data;
		}));
	}

	static final Set<CustomChart> getCharts() {
		return CHARTS;
	}

}
