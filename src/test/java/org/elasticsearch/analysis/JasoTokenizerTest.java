package org.elasticsearch.analysis;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import junit.framework.TestCase;

/**
 * 토크나이저 유닛테스트
 * @author ikchoi
 *
 */
public class JasoTokenizerTest extends TestCase {
	
	public void testTokenizer() throws IOException {	
		
		String orgion	= "최일규 Hello";
		//String compare 	= "ㅊ/초/최/최ㅇ/쵱/최이/최일/최일ㄱ/최읽/최일규";
		String compare 	= "ㅊㅗㅣㅇㅣㄹㄱㅠ";
		
		StringReader reader = new StringReader(orgion);
		JasoTokenizer tokenizer = new JasoTokenizer(reader);	
		CharTermAttribute termAtt = tokenizer.addAttribute(CharTermAttribute.class);
		
		tokenizer.reset();
	
		StringBuffer sb = new StringBuffer();
	
		while(tokenizer.incrementToken()) {
			if(sb.length()>0) sb.append("/");
			sb.append(termAtt.toString());
		}
	
		//TestCase.assertEquals(compare, sb.toString());
		tokenizer.close();
		
		System.out.println(sb.toString());		
	
	}	
}
