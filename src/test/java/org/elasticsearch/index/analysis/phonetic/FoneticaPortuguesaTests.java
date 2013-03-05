package org.elasticsearch.index.analysis.phonetic;

import org.apache.commons.codec.EncoderException;
import org.hamcrest.MatcherAssert;
import org.testng.annotations.Test;

public class FoneticaPortuguesaTests {

    @Test
    public void testConsonantClusterOfHLetter() throws EncoderException {
    	assertEncode("talho", "talio");
    	assertEncode("banho", "banio");
    	assertEncode("aché", "axé");
    	assertEncode("ashé", "axé");
    	assertEncode("rapha", "rafa");
    }
    
    @Test
    public void testSSeCemS() throws EncoderException {
    	assertEncode("assa", "asa");
    	assertEncode("aço", "aso");
    }
    
    @Test
    public void testAOeAMemAN() throws EncoderException {
    	assertEncode("kão", "kan");
    	assertEncode("kã", "kan");
    	assertEncode("kam", "kan");
    	assertEncode("kãod", "kand");
    	assertEncode("kãp", "kanp");
    	assertEncode("kamt", "kant");
    }
    
    @Test
    public void testAMbeforeVocal() throws EncoderException {
    	assertEncode("ama", "ama");
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
    public void testQUBeforeEandI() throws EncoderException {
    	assertEncode("que", "ke");
    	assertEncode("qué", "ké");
    	assertEncode("qui", "ki");
    	assertEncode("quí", "kí");
    	assertEncode("qüe", "kue");
    	assertEncode("qüé", "kué");
    	assertEncode("qüi", "kui");
    	assertEncode("qüí", "kuí");
	}
	
	@Test
    public void testGbeforeEandI() throws EncoderException {
    	assertEncode("ge", "je");
    	assertEncode("gi", "ji");
    	assertEncode("gé", "jé");
    	assertEncode("gí", "jí");
	}
	
	@Test
    public void testGSound() throws EncoderException {
    	assertEncode("ga", "g1a");
    	assertEncode("go", "g1o");
    	assertEncode("gu", "g1u");
    	assertEncode("gue", "g1e");
    	assertEncode("gui", "g1i");
    	assertEncode("güe", "g1ue");
    	assertEncode("güi", "g1ui");
	}
	
	@Test
    public void testSWithSoundOFZ() throws EncoderException {
    	assertEncode("asa", "aza");
    	assertEncode("esaú", "ezaú");
    }
	
	@Test
    public void testLWithSoundOfU() throws EncoderException {
    	assertEncode("alto", "auto");
    	assertEncode("samuel", "samueu");
    }
	
	@Test
    public void testLAsConsonant() throws EncoderException {
		assertEncode("ela", "ela");
		assertEncode("ele", "ele");
		assertEncode("lá", "lá");
	}
	
	@Test
    public void testHMute() throws EncoderException {
		assertEncode("home", "ome");
		assertEncode("ha", "a");
		assertEncode("hó", "ó");
		assertEncode("húmido", "úmido");
		assertEncode("óhtimo", "ótimo");
	}
    
    private void assertEncode(String before, String after) throws EncoderException {
    	FoneticaPortuguesa encoder = new FoneticaPortuguesa();
    	String encoded = encoder.encode(before);
    	String message = before + " should encode to " + after + " but encoded to " + encoded;
    	MatcherAssert.assertThat(message, encoded.equals(after.toUpperCase()));
    }
}