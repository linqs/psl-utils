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
package org.linqs.psl.utils.dataloading.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadDelimitedData {
	public static final String defaultDelimiter = "\t";

	private static final Logger log = LoggerFactory.getLogger(LoadDelimitedData.class);
	private static final String commentPrefix = "//";

	public static<O> List<O> loadTabData(String fileName, DelimitedObjectConstructor<O> loader) {
		return loadTabData(fileName,loader,defaultDelimiter);
	}

	public static<O> List<O> loadTabData(String fileName, DelimitedObjectConstructor<O> loader, String delim) {
		List<O> result = new ArrayList<O>();
		try {
			BufferedReader in  = new BufferedReader(new FileReader(fileName));
			String line = "";
			int lineNr = 0;
			while ((line = in.readLine())!= null){
				lineNr++;
				line = line.trim();
				if (line.isEmpty()) continue;
				if (line.startsWith(commentPrefix)) continue;

				String[] data = line.split(delim);
				if (loader.length()>0 && data.length != loader.length()) {
					log.warn("Could not parse line #{} (contents='{}') from file: "+fileName,lineNr,line);
					continue;
				}
				O obj = loader.create(data);
				if (obj!=null) result.add(obj);

			}
			in.close();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return result;
	}
}
