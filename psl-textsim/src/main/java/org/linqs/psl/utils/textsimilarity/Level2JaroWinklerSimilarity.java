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

import com.wcohen.ss.BasicStringWrapper;
import com.wcohen.ss.Level2JaroWinkler;

/**
 * Wraps the Level 2 Jaro-Winkler string similarity from the Second String library.
 * Level 2 means that tokens are broken up before comparison.
 * If the similarity is below a threshold (default=0.5) it returns 0.
 */
public class Level2JaroWinklerSimilarity implements ExternalFunction {
	// similarity threshold (default=0.5)
	private double simThresh;

	// constructors
	public Level2JaroWinklerSimilarity() {
		this.simThresh = 0.5;
	}
	public Level2JaroWinklerSimilarity(double simThresh) {
		this.simThresh = simThresh;
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
		BasicStringWrapper aWrapped = new BasicStringWrapper(a);
		BasicStringWrapper bWrapped = new BasicStringWrapper(b);

		Level2JaroWinkler l2jaroW = new Level2JaroWinkler();
		double sim = l2jaroW.score(aWrapped, bWrapped);

		if (sim < simThresh)
			return 0.0;
		else
			return sim;
	 }
}
