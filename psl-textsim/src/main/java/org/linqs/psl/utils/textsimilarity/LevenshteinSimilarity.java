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

import org.apache.commons.lang3.StringUtils;
import org.linqs.psl.database.ReadableDatabase;
import org.linqs.psl.model.function.ExternalFunction;
import org.linqs.psl.model.term.Constant;
import org.linqs.psl.model.term.ConstantType;
import org.linqs.psl.model.term.StringAttribute;

public class LevenshteinSimilarity implements ExternalFunction {

    /**
     * String for which the similarity computed by the metrics is below this
     * threshold are considered to NOT be similar and hence 0 is returned as a
     * truth value.
     */
    private static final double defaultSimilarityThreshold = 0.5;

    private final double similarityThreshold;

    public LevenshteinSimilarity() {
        this(defaultSimilarityThreshold);
    }

    public LevenshteinSimilarity(double threshold) {
        similarityThreshold = threshold;
    }

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public ConstantType[] getArgumentTypes() {
        return new ConstantType[] {ConstantType.String, ConstantType.String};
    }

    @Override
    public double getValue(ReadableDatabase db, Constant... args) {

        String a = ((StringAttribute) args[0]).getValue();
        String b = ((StringAttribute) args[1]).getValue();

        int maxLen = Math.max(a.length(), b.length());
        if (maxLen == 0)
            return 1.0;

        double ldist = StringUtils.getLevenshteinDistance(a, b);
        double sim = 1.0 - (ldist / maxLen);

        if (sim > similarityThreshold)
            return sim;

        return 0.0;
    }

    public String toString() {
        return "Levenstein String Similarity";
    }
}
