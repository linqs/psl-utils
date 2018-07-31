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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.linqs.psl.model.function.ExternalFunction;
import org.linqs.psl.model.term.StringAttribute;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CosineSimilarityTest extends TextSimTest {
	/* TODO(eriq): Our cosine similarity is crazy: https://github.com/linqs/psl-utils/issues/7
	@Test
	public void testBase() {
		ExternalFunction fun = new CosineSimilarity();

		String[] a = new String[]{
			"A B C"
		};

		String[] b = new String[]{
			"A B C"
		};

		double[] expected = new double[]{
			1.0
		};

		directTest(fun, a, b, expected);
	}
	*/
}
