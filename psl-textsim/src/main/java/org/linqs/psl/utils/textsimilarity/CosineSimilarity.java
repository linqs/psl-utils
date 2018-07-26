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
package org.linqs.psl.utils.textsimilarity;

import org.linqs.psl.database.ReadableDatabase;
import org.linqs.psl.model.function.ExternalFunction;
import org.linqs.psl.model.term.Constant;
import org.linqs.psl.model.term.ConstantType;
import org.linqs.psl.model.term.StringAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.list.tint.IntArrayList;
import cern.colt.map.tdouble.AbstractIntDoubleMap;
import cern.colt.map.tdouble.OpenIntDoubleHashMap;

public class CosineSimilarity implements ExternalFunction {
	private static final double EPSILON = 1e-5;
	private static final double DEFAULT_SIM_THRESHOLD = 0.4;

	private static final Logger log = LoggerFactory.getLogger(CosineSimilarity.class);

	private final double similarityThreshold;

	public CosineSimilarity() {
		this(DEFAULT_SIM_THRESHOLD);
	}

	public CosineSimilarity(double threshold) {
		similarityThreshold = threshold;
	}

	@Override
	public int getArity() {
		return 2;
	}

	@Override
	public ConstantType[] getArgumentTypes() {
		return new ConstantType[] { ConstantType.String, ConstantType.String };
	}

	@Override
	public double getValue(ReadableDatabase db, Constant... args) {
		String a = ((StringAttribute) args[0]).getValue();
		String b = ((StringAttribute) args[1]).getValue();

		WordVector vec1 = getVector(a);
		WordVector vec2 = getVector(b);

		double result = cosineSimilarity(vec1, vec2);
		if (result > similarityThreshold) {
			return result;
		}

		return 0.0;
	}

	public String toString() {
		return String.format("CosineSimilarity(%f)", similarityThreshold);
	}

	private static WordVector getVector(String text) {
		// Cleanup whitespace.
		text = text.replaceAll("\\s+", " ").trim();

		WordVector vec = new WordVector();
		if (text.isEmpty()) {
			return vec;
		}

		String[] entries = text.split(" ");
		assert(entries.length > 0);

		for (int i = 0; i < entries.length; i++) {
			String[] entry = entries[i].split(":");
			assert entry.length==2 : entries[i] + " | " + text + ">";
			vec.addWord(Integer.parseInt(entry[0]),Double.parseDouble(entry[1]));
		}

		return vec;
	}

	private static double vecLength(WordVector vec) {
		double[] vals = vec.values().elements();
		double total = 0.0;
		for (int i=0;i<vec.size();i++) {
			total += vals[i]*vals[i];
		}
		return Math.sqrt(total);
	}

	private static double multiplyVec(WordVector vecA, WordVector vecB) {
		AbstractIntDoubleMap vec1=vecA,vec2=vecB;
		if (vecB.size()<vecA.size()) {
			vec1 = vecB; vec2 = vecA;
		}
		double total = 0.0;
		int[] keys = vec1.keys().elements();
		for (int i=0;i<vec1.size();i++) {
			if (vec2.containsKey(keys[i])) {
				total += vec1.get(keys[i])*vec2.get(keys[i]);
			}
		}
		return total;
	}

	public static double cosineSimilarity(WordVector vec1, WordVector vec2) {
		double len1 = vecLength(vec1);
		double len2 = vecLength(vec2);
		double result = 0.0;
		if (len1>0.0 && len2>0.0) result = multiplyVec(vec1,vec2)/(len1*len2);
		assert result>=(0.0-EPSILON) && result<=(1.0+EPSILON);
		return result;
	}

	public static class WordVector extends OpenIntDoubleHashMap {

		private static final long serialVersionUID = 2045184972598485102L;

		public int getNumWords() {
			return this.size();
		}

		public int getMaxWordIndex() {
			IntArrayList key = this.keys();
			int[] vals = key.elements();
			int max = -1;
			for (int i=0;i<key.size();i++) {
				if (vals[i]>=max) max = vals[i]+1;
			}
			return max;
		}

		public void addWord(int index, double val) {
			put(index,val);
		}

	}
}
