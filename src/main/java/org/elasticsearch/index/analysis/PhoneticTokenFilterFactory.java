/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.index.analysis;

import java.util.Arrays;
import java.util.HashSet;
import org.apache.commons.codec.Encoder;
import org.apache.commons.codec.language.Caverphone1;
import org.apache.commons.codec.language.Caverphone2;
import org.apache.commons.codec.language.ColognePhonetic;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.RefinedSoundex;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.codec.language.bm.Languages.LanguageSet;
import org.apache.commons.codec.language.bm.NameType;
import org.apache.commons.codec.language.bm.PhoneticEngine;
import org.apache.commons.codec.language.bm.RuleType;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.phonetic.BeiderMorseFilter;
import org.apache.lucene.analysis.phonetic.DoubleMetaphoneFilter;
import org.apache.lucene.analysis.phonetic.PhoneticFilter;
import org.elasticsearch.ElasticSearchIllegalArgumentException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.phonetic.FoneticaPortuguesa;
import org.elasticsearch.index.analysis.phonetic.HaasePhonetik;
import org.elasticsearch.index.analysis.phonetic.KoelnerPhonetik;
import org.elasticsearch.index.analysis.phonetic.Nysiis;
import org.elasticsearch.index.settings.IndexSettings;

/**
 *
 */
public class PhoneticTokenFilterFactory extends AbstractTokenFilterFactory {

    private final Encoder encoder;
    private final boolean replace;
    private int maxcodelength;
    private String[] languageset;
    private NameType nametype;
    private RuleType ruletype;

    @Inject
    public PhoneticTokenFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        this.languageset = null;
        this.nametype = null;
        this.ruletype = null;
        this.maxcodelength = 0;
        this.replace = settings.getAsBoolean("replace", true);
        // weird, encoder is null at last step in SimplePhoneticAnalysisTests, so we set it to metaphone as default
        String encodername = settings.get("encoder", "metaphone"); 
        if ("metaphone".equalsIgnoreCase(encodername)) {
            this.encoder = new Metaphone();
        } else if ("soundex".equalsIgnoreCase(encodername)) {
            this.encoder = new Soundex();
        } else if ("caverphone1".equalsIgnoreCase(encodername)) {
            this.encoder = new Caverphone1();
        } else if ("caverphone2".equalsIgnoreCase(encodername)) {
            this.encoder = new Caverphone2();
        } else if ("caverphone".equalsIgnoreCase(encodername)) {
            this.encoder = new Caverphone2();
        } else if ("refined_soundex".equalsIgnoreCase(encodername) || "refinedSoundex".equalsIgnoreCase(encodername)) {
            this.encoder = new RefinedSoundex();
        } else if ("cologne".equalsIgnoreCase(encodername)) {
            this.encoder = new ColognePhonetic();
        } else if ("double_metaphone".equalsIgnoreCase(encodername) || "doubleMetaphone".equalsIgnoreCase(encodername)) {
            this.encoder = null;
            this.maxcodelength = settings.getAsInt("max_code_len", 4);
        } else if ("bm".equalsIgnoreCase(encodername) || "beider_morse".equalsIgnoreCase(encodername) || "beidermorse".equalsIgnoreCase(encodername)) {
            this.encoder = null;
            this.languageset = settings.getAsArray("languageset");
            String ruleType = settings.get("rule_type", "approx");
            if ("approx".equalsIgnoreCase(ruleType)) {
                ruletype = RuleType.APPROX;
            } else if ("exact".equalsIgnoreCase(ruleType)) {
                ruletype = RuleType.EXACT;
            } else {
                throw new ElasticSearchIllegalArgumentException("No matching rule type [" + ruleType + "] for beider morse encoder");
            }
            String nameType = settings.get("name_type", "generic");
            if ("GENERIC".equalsIgnoreCase(nameType)) {
                nametype = NameType.GENERIC;
            } else if ("ASHKENAZI".equalsIgnoreCase(nameType)) {
                nametype = NameType.ASHKENAZI;
            } else if ("SEPHARDIC".equalsIgnoreCase(nameType)) {
                nametype = NameType.SEPHARDIC;
            }
        } else if ("koelnerphonetik".equalsIgnoreCase(encodername)) {
            this.encoder = new KoelnerPhonetik();
        } else if ("haasephonetik".equalsIgnoreCase(encodername)) {
            this.encoder = new HaasePhonetik();
        } else if ("nysiis".equalsIgnoreCase(encodername)) {
            this.encoder = new Nysiis();
        } else if ("foneticaportuguesa".equalsIgnoreCase(encodername)) {
        	this.encoder = new FoneticaPortuguesa();
        } else {
            throw new ElasticSearchIllegalArgumentException("unknown encoder [" + encodername + "] for phonetic token filter");
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        if (encoder == null) {
            if (ruletype != null && nametype != null) {
                if (languageset != null) {
                    final LanguageSet languages = LanguageSet.from(new HashSet(Arrays.asList(languageset)));
                    return new BeiderMorseFilter(tokenStream, new PhoneticEngine(nametype, ruletype, true), languages);
                }
                return new BeiderMorseFilter(tokenStream, new PhoneticEngine(nametype, ruletype, true));
            }
            if (maxcodelength > 0) {
                return new DoubleMetaphoneFilter(tokenStream, maxcodelength, !replace);
            }
        } else {
            return new PhoneticFilter(tokenStream, encoder, !replace);
        }
        throw new ElasticSearchIllegalArgumentException("encoder error");
    }
}