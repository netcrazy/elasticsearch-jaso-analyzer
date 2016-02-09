package org.elasticsearch.analysis;
import junit.framework.TestCase;

/**
 * 초성,종성,중성 분해 테스트
 * @author 	최일규
 * @since	2016-02-03
 *
 */
public class JasoTest2 extends TestCase {
	
	//초성(19자) ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ
	static String[] chosungKor = { "ㄱ", "ㄱㄱ", "ㄴ", "ㄷ", "ㄷㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅂㅂ", "ㅅ", "ㅅㅅ", "ㅇ", "ㅈ", "ㅈㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ" };
	//중성(21자) ㅏ ㅐ ㅑ ㅒ ㅓ ㅔ ㅕ ㅖ ㅗ ㅘ(9) ㅙ(10) ㅚ(11) ㅛ ㅜ ㅝ(14) ㅞ(15) ㅟ(16) ㅠ ㅡ ㅢ(19) ㅣ
	static String[] jungsungKor = { "ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅗㅏ", "ㅗㅐ", "ㅗㅣ", "ㅛ", "ㅜ", "ㅜㅓ", "ㅜㅔ", "ㅜㅣ", "ㅠ", "ㅡ", "ㅡㅣ", "ㅣ" };
	//종성(28자) <없음> ㄱ ㄲ ㄳ(3) ㄴ ㄵ(5) ㄶ(6) ㄷ ㄹ ㄺ(9) ㄻ(10) ㄼ(11) ㄽ(12) ㄾ(13) ㄿ(14) ㅀ(15) ㅁ ㅂ ㅄ(18) ㅅ ㅆ ㅇ ㅈ ㅊ ㅋ ㅌ ㅍ ㅎ
	static String[] jongsungKor = { " ", "ㄱ", "ㄱㄱ", "ㄱㅅ", "ㄴ", "ㄴㅈ", "ㄴㅎ", "ㄷ", "ㄹ", "ㄹㄱ", "ㄹㅁ", "ㄹㅂ", "ㄹㅅ", "ㄹㅌ", "ㄹㅍ", "ㄹㅎ", "ㅁ", "ㅂ", "ㅂㅅ", "ㅅ", "ㅅㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ" };

	public void testJaso() {
		String originStr	= "이광석";
		char[] termBuffer 	= originStr.toCharArray();
		StringBuffer buffer = new StringBuffer();
		int cho	 = 0;
		int jung = 0;
		int jong = 0;
		for(int i = 0; i < termBuffer.length; i++) {
			char ch = termBuffer[i];
			
			//가(AC00)~힣(D7A3) 에 속한 글자면 분해
			if(ch >= 0xAC00 && ch <= 0xD7A3) {
				
				//Unicode 값으로 환산한다.
				int uniValue = ch - 0xAC00;

				jong = uniValue % 28;               	//종성
				cho	 = ((uniValue - jong) / 28) / 21;	//초성
				jung = ((uniValue - jong) / 28) % 21;   //중성	
								
				for(int j=0; j < chosungKor[cho].length(); j++) {						
					buffer.append(chosungKor[cho].toCharArray()[j]).append("♥");	
				}
				
				for(int j=0; j < jungsungKor[jung].length(); j++) {
					buffer.append(jungsungKor[jung].toCharArray()[j]).append("♥");
				}
				
				//받침이 있으면
				if(jong != 0) {
					for(int j=0; j < jongsungKor[jong].length(); j++) {
						buffer.append(jongsungKor[jong].toCharArray()[j]).append("♥");
					}					
				}
			} else {
				//한글을 제외한 문자는 그냥 넣는다.
				buffer.append(termBuffer[i]).append("♥");
			}			
		}
		
		System.out.println(buffer);

	}
}
