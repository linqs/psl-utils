/*
 * This file is part of the PSL software.
 * Copyright 2011-2015 University of Maryland
 * Copyright 2013-2017 The Regents of the University of California
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.linqs.psl.model.function.ExternalFunction;
import org.linqs.psl.model.term.StringAttribute;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The base class for all text similarity related tests.
 * All ExternalFunction's used in these tests should ignore the first (database) parameter to getValue().
 */
public abstract class TextSimTest {
	public static final double EPSILON = 0.00001;

	/**
	 * Call the similarity method directly on all the inputs.
	 */
	public void directTest(ExternalFunction fun, String[] a, String[] b, double[] expected) {
		assertEquals(a.length, expected.length);
		assertEquals(b.length, expected.length);

		for (int i = 0; i < a.length; i++) {
			double actual = fun.getValue(null, new StringAttribute(a[i]), new StringAttribute(b[i]));
			assertEquals(expected[i], actual, EPSILON);
		}
	}
}
