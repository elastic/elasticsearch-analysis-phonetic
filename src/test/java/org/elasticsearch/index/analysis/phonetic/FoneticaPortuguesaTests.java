package org.elasticsearch.index.analysis.phonetic;


import org.apache.commons.codec.EncoderException;
import org.hamcrest.MatcherAssert;
import org.testng.annotations.Test;

public class FoneticaPortuguesaTests {

    @Test
    public void testCHeSHToXis() throws EncoderException {
    	assertEncode("aché", "axé");
    	assertEncode("ashé", "axé");
    }

    @Test
    public void testLHToLi() throws EncoderException {
    	assertEncode("talho", "talio");
    }
    
    @Test
    public void testSSeCemS() throws EncoderException {
    	assertEncode("assa", "asa");
    	assertEncode("aço", "aso");
    }
    
    @Test
    public void testAOeANemAM() throws EncoderException {
    	assertEncode("cão", "cam");
    	assertEncode("can", "cam");
    }
    
    @Test
    public void testOEeOISemOIN() throws EncoderException {
    	assertEncode("pões", "poins");
    	assertEncode("põis", "poins");
    	assertEncode("poes", "poins");
    	assertEncode("pois", "poins");
    }
    
    private void assertEncode(String before, String after) throws EncoderException {
    	FoneticaPortuguesa encoder = new FoneticaPortuguesa();
    	String encoded = encoder.encode(before);
    	String message = before + " should encode to " + after + " but encoded to " + encoded;
    	MatcherAssert.assertThat(message, encoded.equals(after.toUpperCase()));
    }
}