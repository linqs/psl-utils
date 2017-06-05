/*
 * This file is part of the PSL software.
 * Copyright 2011-2015 University of Maryland
 * Copyright 2013-2015 The Regents of the University of California
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
package org.linqs.psl.utils.evaluation.statistics;

import java.util.Iterator;

import org.linqs.psl.database.Database;
import org.linqs.psl.database.Queries;
import org.linqs.psl.model.atom.GroundAtom;
import org.linqs.psl.model.atom.ObservedAtom;
import org.linqs.psl.model.predicate.Predicate;
import org.linqs.psl.model.term.Constant;
import org.linqs.psl.utils.evaluation.statistics.filter.AtomFilter;

import java.lang.UnsupportedOperationException;

/*
 * The goal of this class is to compute prediction statistics quickly without overhead.
 * Speed up  achieved by combining both Discrete and Continuous Comparators 
 * to reduce the number of iterations over the inferred ground atoms to one, and
 * this class does not create the Map<GroundAtom, Double> errors and 
 * Set<GroundAtom> correctAtoms data structures.
 * Author: nkini@ucsc.edu
*/
public class QuickPredictionComparator implements PredictionComparator, ResultComparator {
	
	public static final double DEFAULT_THRESHOLD = 0.5;

	private final Database result;
	private Database baseline;
	private AtomFilter resultFilter;
	private double threshold;
	private Metric metric;
	
	int tp;
	int fn;
	int tn;
	int fp;
	double continuousMetricScore;

	public QuickPredictionComparator(Database result) {
		this.result = result;
		baseline = null;
		resultFilter = AtomFilter.NoFilter;
		threshold = DEFAULT_THRESHOLD;
		metric = Metric.MSE;
	}
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	@Override
	public void setBaseline(Database db) {
		this.baseline = db;
	}
	
	@Override
	public void setResultFilter(AtomFilter af) {
		resultFilter = af;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	/**
	 * Compares the baseline with te inferred result for a given predicate
	 * DOES NOT check the baseline database for atoms. Only use this if all 
	 * possible predicted atoms are active and unfiltered
	 */
	@Override
	public QuickPredictionStatistics compare(Predicate p) {
		countResultDBStats(p);
		return new QuickPredictionStatistics(tp, fp, tn, fn, threshold, continuousMetricScore);
	}


	private double accumulate(double difference) {
		double value;
		switch (metric) {
			case MSE: 	value = difference * difference;
						break;
			case MAE: 	value = Math.abs(difference);
						break;
			default: 	value = 0.0;
						break;
		}
		return value;
	}

	public enum Metric {
		MSE, MAE;
	}


	/* TODO: This method requires errors and correctAtoms. I propose leaving it unimplemented
	 *       making this a very specific class, that offers prediction statistics and certainly
	         not data structures containing erroneous and correctly predicted ground atoms.
	*/
	public QuickPredictionStatistics compare(Predicate p, int maxBaseAtoms) {
		throw new UnsupportedOperationException("The QuickPredictionComparator does not implement this method. Perhaps try DiscretePredictionComparator or ContinuousPredictionComparator instead?");
	}

	/**
	 * Subroutine used by both compare methods for counting statistics from atoms
	 * stored in result database
	 * @param p Predicate to compare against baseline database
	 */
	private void countResultDBStats(Predicate p) {
		tp = 0;
		fn = 0;
		tn = 0;
		fp = 0;
		
		double score = 0.0;
		int total = 0;

		GroundAtom resultAtom, baselineAtom;
		Constant[] args;
		boolean actual, expected;

		Iterator<GroundAtom> iter = resultFilter.filter(Queries.getAllAtoms(result, p).iterator());
		
		while (iter.hasNext()) {

			resultAtom = iter.next();
			args = new Constant[resultAtom.getArity()];

			for (int i = 0; i < args.length; i++) {
				args[i] = (Constant) resultAtom.getArguments()[i];
			}
			baselineAtom = baseline.getAtom(resultAtom.getPredicate(), args);
			
			if (!(baselineAtom instanceof ObservedAtom)) {
				continue;
			}

			//Continuous comparison statistics
			total++;
			score += accumulate(baselineAtom.getValue() - resultAtom.getValue());

			actual = (resultAtom.getValue() >= threshold);
			expected = (baselineAtom.getValue() >= threshold);
			if ((actual && expected) || (!actual && !expected)) {
				// True negative
				if (!actual) {
					tn++;
				}
				// True positive
				else {
					tp++;
				}
			}
			// False negative
			else if (!actual) {
				fn++;
			}
			// False positive
			else {
				fp++;
			}
		}

		continuousMetricScore = score / total;

	}
	
}
