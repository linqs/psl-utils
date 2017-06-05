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

/*
 * Companion class for QuickPredictionComparator.
 * Author: nkini@ucsc.edu
*/
public class QuickPredictionStatistics implements PredictionStatistics {
	
	public enum BinaryClass {
		NEGATIVE,
		POSITIVE
	}
	
	private final int tp;
	private final int fp;
	private final int fn;
	private final int tn;
	private final double threshold;
	private final double continuousMetricScore; 
	
	public QuickPredictionStatistics(int tp, int fp, int tn, int fn,
			double threshold, double continuousMetricScore) {
		this.tp = tp;
		this.fp = fp;
		this.tn = tn;
		this.fn = fn;
		this.threshold = threshold;
		this.continuousMetricScore = continuousMetricScore;
	}
	
	public double getThreshold() {
		return threshold;
	}
	
	public double getContinuousMetricScore() {
		return continuousMetricScore;
	}
	
	public double getPrecision(BinaryClass c) {
		if (c == BinaryClass.NEGATIVE) {
			double n = tn + fn;
			if (n == 0.0) {
				return 1.0;
			}
			return tn / n;
		}
		else {
			double p = tp + fp;
			if (p == 0.0) {
				return 1.0;
			}
			return tp / p;
		}
	}
	
	public double getRecall(BinaryClass c) {
		if (c == BinaryClass.NEGATIVE) {
			double n = tn + fp;
			if (n == 0.0) {
				return 1.0;
			}
			return tn / n;
		}
		else {
			double p = tp + fn;
			if (p == 0.0) {
				return 1.0;
			}
			return tp / p;
		}
	}
	
	public double getF1(BinaryClass c) {
		double prec = getPrecision(c);
		double rec = getRecall(c);
		double sum = prec + rec;
		if (sum == 0.0) { 
			return 0.0;
		}
		return 2 * (prec * rec) / sum;
	}
	
	public double getAccuracy() {
		double numAtoms = getNumAtoms();
		if (numAtoms == 0.0) {
			return 0.0;
		}
		return (tp + tn) / numAtoms;
	}

	@Override
	public double getError() {
		return fp + fn;
	}

	@Override
	public int getNumAtoms() {
		return tp + fp + tn + fn;
	}
	
}

