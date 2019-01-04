/*
 * This file is part of the PSL software.
 * Copyright 2011-2015 University of Maryland
 * Copyright 2013-2019 The Regents of the University of California
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

public class SubStringSimilarity implements ExternalFunction {

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
        String s1,s2;
        if (a.length()<b.length()) {
            s1 = a; s2 = b;
        } else {
            s1 = b; s2 = a;
        }
        s1 = s1.toLowerCase(); s2 = s2.toLowerCase();
        int index = s2.indexOf(s1, 0);
        if (index<0) return 0.0;
        else {
            return s1.length()*1.0/s2.length();
        }
    }


}
