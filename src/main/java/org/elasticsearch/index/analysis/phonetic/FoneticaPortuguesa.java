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

	private static final Map<String, String[]> SOUNDS_BEFORE_U = new HashMap<String, String[]>() {{
		put("K$1",  new String[]{ "Q[UÜ]" + REGEX_U });
	}};

	private static final Map<String, String[]> SOUNDS_BETWEEN_VOCALS = new HashMap<String, String[]>() {{
		put("$1Z$2",  new String[]{ REGEX_VOCALS + "S" + REGEX_VOCALS });
	}};
	
	private static final Map<String, String[]> OTHER_SOUNDS = new HashMap<String, String[]>() {{
		put("LI",   new String[]{ "LH" });
		put("NI",   new String[]{ "NH" });
		put("AN",   new String[]{ "ÃO", "Ã" });
		put("EIA",  new String[]{ "ÉA", "EA", "ÊA" });
		put("R",  	new String[]{ "RR" });
		put("T",  	new String[]{ "TT" });
		put("G1U",  new String[]{ "GÜ" });
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
		replaced = replaceSoundsRegex(replaced, SOUNDS_BEFORE_CONSONANTS);
		replaced = replaceSoundsRegex(replaced, OTHER_SOUNDS);
		return new EncoderLetterByLetter(replaced).encode();
	}
	
	private class EncoderLetterByLetter {
		
		//private static final String CONSONANTS = "BCDFGHJKLMNPQRSTVXZW";
		private static final String VOCALS_O = "OÓÔÒÕ";
		private static final String VOCALS_AOU = "AUÁÚÂÛÀÙÃŨ" + VOCALS_O;
		private static final String VOCALS_EI = "EIÉÍÈÌÊÊẼẼ";
		
		private int index;
		private String str;
		private StringBuilder buffer;
		private int length;

		public EncoderLetterByLetter(String str) {
			this.index = 0;
			this.str = str;
			this.length = str.length();
			this.buffer = new StringBuilder();
		}
		
		public String encode() {
			while(this.index < this.length) {
				this.allLetters();
				this.index++;
			}
			return this.buffer.toString();
		}
		
		private void allLetters() {
			char current = this.str.charAt(this.index);
			if(!treatChar(current)) {
				this.buffer.append(current);
			}
		}
		
		private boolean treatChar(char current) {
			switch(current) {
				case 'C': return consonantC(current);
				case 'Ç': return consonantCedilha(current);
				case 'G': return consonantG(current);
				case 'H': return consonantH(current);
				case 'P': return consonantP(current);
				case 'Q': return consonantQ(current);
				case 'S': return consonantS(current);
			}
			if(isO(current)) {
				return vocalO(current);
			}
			return false;
		}
		
		private boolean consonantC(char current) {
			char next = this.next();
			if( isAOU(next) ) {
				this.buffer.append('K');
				return true;
			}
			if( isEI(next) ) {
				this.buffer.append('S');
				return true;
			}
			if( next == 'C' ) {
				if( isAOU(this.next(2)) ) {
					this.buffer.append('K');
					this.index++;
					return true;
				}
			} else if( next == 'H' ) {
				this.buffer.append('X');
				this.index++;
				return true;
			}
			return false;
		}
		
		private boolean consonantCedilha(char current) {
			this.buffer.append('S');
			return true;
		}
		
		private boolean consonantG(char current) {
			char next = this.next();
			if( next == 'U' ) {
				if( isEI(this.next(2)) ) {
					this.buffer.append('G').append('1');
					this.index++;
					return true;
				}
			}
			if(isAOU(next)) {
				this.buffer.append('G').append('1');
				return true;
			}
			if(isEI(next)) {
				this.buffer.append('J');
				return true;
			}
			return false;
		}
		
		private boolean consonantH(char current) {
			return true;
		}
		
		private boolean consonantP(char current) {
			char next = this.next();
			if( next == 'H' ) {
				this.buffer.append('F');
				this.index++;
				return true;
			}
			return false;
		}
		
		private boolean consonantQ(char current) {
			char next = this.next();
			if(next == 'U' || next == 'Ü') {
				char afterNext = this.next(2);
				if( isEI(afterNext) ) {
					if( next == 'U' ) {
						this.buffer.append('K');
						this.index += 1;
						return true;
					}
					if( next == 'Ü') {
						this.buffer.append('K').append('U');
						this.index += 1;
						return true;
					}
				} else if( isAOU(afterNext) ) {
					this.buffer.append('K').append('U');
					this.index += 1;
					return true;
				}
			}
			return false;
		}
		
		private boolean consonantS(char current) {
			char next = this.next();
			if( next == 'H' ) {
				this.buffer.append('X');
				this.index++;
				return true;
			}
			if( next == 'S' ) {
				this.buffer.append('S');
				this.index++;
				return true;
			}
			return false;
		}
		
		private boolean vocalO(char current) {
			char afterNext = this.next(2);
			if( afterNext == 'S' ) {
				char next = this.next();
				if( isEI(next) ) {
					this.buffer.append('O').append('I').append('N').append('S');
					this.index += 2;
					return true;
				}
			}
			return false;
		}
		
		private char next() {
			return this.next(1);
		}
		
		private char next(int salto) {
			return this.index + salto < this.length ? this.str.charAt(this.index + salto) : '_';
		}
		
		private boolean isAOU(char c) {
			return VOCALS_AOU.indexOf(c)!=-1;
		}
		
		private boolean isO(char c) {
			return VOCALS_O.indexOf(c)!=-1;
		}
		
		private boolean isEI(char c) {
			return VOCALS_EI.indexOf(c)!=-1;
		}
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