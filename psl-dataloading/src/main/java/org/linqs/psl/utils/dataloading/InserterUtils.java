/*
 * This file is part of the PSL software.
 * Copyright 2011-2015 University of Maryland
 * Copyright 2013-2018 The Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.linqs.psl.utils.dataloading;

import org.linqs.psl.database.DataStore;
import org.linqs.psl.database.Partition;
import org.linqs.psl.database.loading.Inserter;
import org.linqs.psl.model.predicate.Predicate;
import org.linqs.psl.model.predicate.PredicateFactory;
import org.linqs.psl.model.predicate.StandardPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Utility methods for common data-loading tasks.
 * All data will be sent to the inserter as a string.
 * It is the responsibility of the inserter to handle conversions from strings.
 */
public class InserterUtils {
	public static final String DEFAULT_DELIMITER = "\t";
	public static final String COMMENT_PREFIX = "//";

	private static final Logger log = LoggerFactory.getLogger(InserterUtils.class);

	public static void loadDelimitedData(Inserter inserter, String path) {
		loadDelimitedData(inserter, path, DEFAULT_DELIMITER);
	}

	public static void loadDelimitedData(Inserter inserter, String path, String delimiter) {
		List<List<Object>> data = loadDelimitedData(path, delimiter);
		inserter.insertAll(data);
	}

	public static void loadDelimitedDataTruth(Inserter inserter, String path) {
		loadDelimitedDataTruth(inserter, path, DEFAULT_DELIMITER);
	}

	public static void loadDelimitedDataTruth(final Inserter inserter, String path, String delimiter) {
		List<List<Object>> data = loadDelimitedData(path, delimiter);
		List<Double> values = new ArrayList<Double>(data.size());

		for (int i = 0; i < data.size(); i++) {
			List<Object> row = data.get(i);

			double truth;
			try {
				truth = Double.parseDouble((String)row.get(row.size() - 1));
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Could not read truth value for row " + (i + 1) + ": " + row.get(row.size() - 1), ex);
			}

			if (truth < 0.0 || truth > 1.0) {
				throw new IllegalArgumentException("Illegal truth value encountered on row " + (i + 1) + ": " + truth);
			}

			// Remove the truth value from the list by taking a sublist (should not cause any additional allocation).
			data.set(i, row.subList(0, row.size() - 1));
         values.add(truth);
		}

		inserter.insertAllValues(values, data);
	}

	/**
	 * Parse a file and get the parts of each row.
	 * Each object returned will be a string, but we are returning Objects so the inserter can take it in directly.
	 */
	private static List<List<Object>> loadDelimitedData(String path, String delimiter) {
		List<List<Object>> rows = new ArrayList<List<Object>>();

		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line = null;
			int lineNumber = 0;

			while ((line = reader.readLine()) != null) {
				lineNumber++;

				line = line.trim();
				if (line.isEmpty() || line.startsWith(COMMENT_PREFIX)) {
					continue;
				}

				Object[] data = (Object[])line.split(delimiter);
				rows.add(Arrays.asList(data));
			}
		} catch (IOException ex) {
			throw new RuntimeException("Unable to parse delimited file.", ex);
		}

		return rows;
	}
}
