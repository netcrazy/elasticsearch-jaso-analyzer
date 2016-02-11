package org.elasticsearch.analysis;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import junit.framework.TestCase;
import org.elasticsearch.common.config;

/**
 * 토크나이저 유닛테스트
 * @author ikchoi
 *
 */
public class JasoTokenizerTest extends TestCase {
	
	public void testTokenizer() throws IOException {	
		
		String orgion	= "최일규 Hello";
		String compare 	= String.format("%s%s%s", "ㅊㅗㅣㅇㅣㄹㄱㅠ", config.SPLIT_CHAR, "Hello");
		
		StringReader reader = new StringReader(orgion);
		JasoTokenizer tokenizer = new JasoTokenizer(reader);	
		CharTermAttribute termAtt = tokenizer.addAttribute(CharTermAttribute.class);
		
		tokenizer.reset();
	
		StringBuffer sb = new StringBuffer();
	
		while(tokenizer.incrementToken()) {
			if(sb.length()>0) sb.append(config.SPLIT_CHAR);
			sb.append(termAtt.toString());
		}
	
		TestCase.assertEquals(compare, sb.toString());
		tokenizer.close();
		
		System.out.println(sb.toString());		
	
	}	
}
