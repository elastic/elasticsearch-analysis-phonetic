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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
	
	private static final String AOU = "AOUÁÓÚÂÔÛÀÒÙÃÕŨ";
	private static final String EI = "EIÉÍÈÌÊÊẼẼ";
	private static final String STRONG_CONSONANTS = "BCDFGJKLMNPQRSTVXZWY";
	
	private static final String REGEX_AOU = "([" + AOU + "]{1})";
	private static final String REGEX_U = "([UÚÛÙŨ]{1})";
	private static final String REGEX_EI = "([" + EI + "]{1})";
	private static final String REGEX_VOCALS = "([" + AOU + EI + "]{1})";
	private static final String REGEX_CONSONANTS = "([" + STRONG_CONSONANTS + "]{1})";
	
	private static final Map<String, String[]> SOUNDS_BEFORE_CONSONANTS = new HashMap<String, String[]>() {{
		put("AN$1",  new String[]{ "AM" + REGEX_CONSONANTS });
		put("AN",    new String[]{ "AM$" });
		put("U$1",  new String[]{ "L" + REGEX_CONSONANTS });
		put("U",    new String[]{ "L$" });
	}};

	private static final Map<String, String[]> SOUNDS_BEFORE_AOU = new HashMap<String, String[]>() {{
		put("K$1",  new String[]{ "CC" + REGEX_AOU, "C" + REGEX_AOU });
		put("KU$1", new String[]{ "Q[UÜ]" + REGEX_AOU });
		put("G1$1", new String[]{ "G" + REGEX_AOU });
	}};

	private static final Map<String, String[]> SOUNDS_BEFORE_U = new HashMap<String, String[]>() {{
		put("K$1",  new String[]{ "Q[UÜ]" + REGEX_U });
	}};
	
	private static final Map<String, String[]> SOUNDS_BEFORE_EI = new HashMap<String, String[]>() {{
		put("K$1",  new String[]{ "QU" + REGEX_EI });
		put("KU$1", new String[]{ "QÜ" + REGEX_EI });
		put("S$1",  new String[]{ "C" + REGEX_EI });
		put("J$1",  new String[]{ "G" + REGEX_EI });
		put("G1$1", new String[]{ "GU" + REGEX_EI });
	}};

	private static final Map<String, String[]> SOUNDS_BETWEEN_VOCALS = new HashMap<String, String[]>() {{
		put("$1Z$2",  new String[]{ REGEX_VOCALS + "S" + REGEX_VOCALS });
	}};
	
	private static final Map<String, String[]> OTHER_SOUNDS = new HashMap<String, String[]>() {{
		put("X",    new String[]{ "SH", "CH" });
		put("F",    new String[]{ "PH" });
		put("OINS", new String[]{ "ÕES", "ÕIS", "OIS", "OES" });
		put("S",    new String[]{ "SS", "Ç" });
		put("LI",   new String[]{ "LH" });
		put("NI",   new String[]{ "NH" });
		put("AN",   new String[]{ "ÃO", "Ã" });
		put("EIA",  new String[]{ "ÉA", "EA", "ÊA" });
		put("R",  	new String[]{ "RR" });
		put("T",  	new String[]{ "TT" });
		put("G1U",  new String[]{ "GÜ" });
	}};
	
	private static final Map<String, String[]> MUTE_SOUNDS = new HashMap<String, String[]>() {{
		put("",     new String[]{ "H" });
	}};
	
	@Override
	public Object encode(Object str) throws EncoderException {
		return encode((String) str);
	}

	@Override
	public String encode(String str) throws EncoderException {
		if (str == null)
			return null;
		String replaced = str.toUpperCase();
		replaced = replaceSoundsRegex(replaced, SOUNDS_BETWEEN_VOCALS);
		replaced = replaceSoundsRegex(replaced, SOUNDS_BEFORE_U);
		replaced = replaceSoundsRegex(replaced, SOUNDS_BEFORE_EI);
		replaced = replaceSoundsRegex(replaced, SOUNDS_BEFORE_AOU);
		replaced = replaceSoundsRegex(replaced, SOUNDS_BEFORE_CONSONANTS);
		replaced = replaceSoundsRegex(replaced, OTHER_SOUNDS);
		return replaceSoundsRegex(replaced, MUTE_SOUNDS);
	}
	
	private String replaceSoundsRegex(String str, Map<String, String[]> sounds) {
		String replaced = str;
		for(Entry<String, String[]> e : sounds.entrySet()) {
			replaced = replaceSoundRegex(replaced, e);
		}
		return replaced;
	}
	
	private String replaceSoundRegex(String str, Entry<String, String[]> e) {
		String replaced = str;
		for(String expression : e.getValue()) {
			replaced = replaced.replaceAll(expression, e.getKey());
		}
		return replaced;
	}
}