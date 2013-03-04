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
    public void testLiSound() throws EncoderException {
    	assertEncode("talho", "talio");
    }
    
    @Test
    public void testNiSound() throws EncoderException {
    	assertEncode("banho", "banio");
    }
    
    @Test
    public void testSSeCemS() throws EncoderException {
    	assertEncode("assa", "asa");
    	assertEncode("aço", "aso");
    }
    
    @Test
    public void testAOeANemAM() throws EncoderException {
    	assertEncode("kão", "kan");
    	assertEncode("kam", "kan");
    	assertEncode("kã", "kan");
    }
    
    @Test
    public void testOEeOISemOIN() throws EncoderException {
    	assertEncode("pões", "poins");
    	assertEncode("põis", "poins");
    	assertEncode("poes", "poins");
    	assertEncode("pois", "poins");
    }
    
    @Test
    public void testEAemEIA() throws EncoderException {
    	assertEncode("déa", "deia");
    	assertEncode("dea", "deia");
    	assertEncode("dêa", "deia");
    }
    
    @Test
    public void testCeCCemK() throws EncoderException {
    	assertEncode("teca", "teka");
    	assertEncode("tecá", "teká");
    	assertEncode("tecã", "tekan");
    	assertEncode("tecca", "teka");
    	assertEncode("teccá", "teká");
    	assertEncode("teco", "teko");
    	assertEncode("tecó", "tekó");
    	assertEncode("tecú", "tekú");
    	assertEncode("teccõ", "tekõ");
    }
    
    @Test
    public void testCemSifCBeforeEI() throws EncoderException {
    	assertEncode("tece", "tese");
    	assertEncode("teci", "tesi");
    	assertEncode("tecí", "tesí");
    	assertEncode("tecê", "tesê");
    }
    
    @Test
    public void testRRemR() throws EncoderException {
    	assertEncode("terra", "tera");
    }
    
    @Test
    public void testTTemT() throws EncoderException {
    	assertEncode("motta", "mota");
    }
    
    @Test
    public void testQUBeforeAandO() throws EncoderException {
    	assertEncode("qua", "kua");
    	assertEncode("quá", "kuá");
    	assertEncode("qüa", "kua");
    	assertEncode("qüá", "kuá");
    	assertEncode("quo", "kuo");
    	assertEncode("quó", "kuó");
    	assertEncode("qüo", "kuo");
    	assertEncode("qüó", "kuó");
    }
    
	@Test
    public void testQUBeforeU() throws EncoderException {
    	assertEncode("quu", "ku");
    	assertEncode("qüú", "kú");
    	assertEncode("qüu", "ku");
    	assertEncode("quú", "kú");
	}
	
	@Test
    public void testSWithSoundOFZ() throws EncoderException {
    	assertEncode("asa", "aza");
    	assertEncode("esaú", "ezaú");
    }
    
    private void assertEncode(String before, String after) throws EncoderException {
    	FoneticaPortuguesa encoder = new FoneticaPortuguesa();
    	String encoded = encoder.encode(before);
    	String message = before + " should encode to " + after + " but encoded to " + encoded;
    	MatcherAssert.assertThat(message, encoded.equals(after.toUpperCase()));
    }
}