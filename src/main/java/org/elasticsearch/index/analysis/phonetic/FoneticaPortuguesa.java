/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.index.analysis.phonetic;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/**
 * Fonética Portuguesa
 * 
 * Simples encoder para tratar a fonética portuguesa
 * 
 * Look at the tests to see all patterns
 * 
 */
public class FoneticaPortuguesa implements StringEncoder {

	@Override
	public Object encode(Object str) throws EncoderException {
		return encode((String) str);
	}

	@Override
	public String encode(String str) throws EncoderException {
		if (str == null)
			return null;
		return substitute(str);
	}

	private String substitute(String str) {
		return soundOfOins(soundOfS(soundOfAM(muteH(soundOfX(str.toUpperCase())))));
	}

	private String soundOfOins(String s) {
		return s.replace("ÕES", "OINS").replace("ÕIS", "OINS").replace("OIS", "OINS").replace("OES", "OINS");
	}

	private String soundOfX(String s) {
		return s.replace("CH", "X").replace("SH", "X");
	}

	private String soundOfS(String s) {
		return s.replace("SS", "S").replace("Ç", "S");
	}

	private String muteH(String s) {
		return s.replace("LH", "LI");
	}

	private String soundOfAM(String s) {
		return s.replace("AN", "AM").replace("ÃO", "AM");
	}
}