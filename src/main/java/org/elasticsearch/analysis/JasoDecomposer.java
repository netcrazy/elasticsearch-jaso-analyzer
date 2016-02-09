package org.elasticsearch.analysis;

import org.elasticsearch.common.config;

/**
 * 자동완성용 자소분해
 * @author	최일규
 * @since	2013-09-15
 */
public class JasoDecomposer 
{
	//string 형으로 정의해야 하는 이유는 qt 와 같이 이중 받침때문
	static String[] chosungEng = { "r", "R", "s", "e", "E", "f", "a", "q", "Q", "t", "T", "d", "w", "W", "c", "z", "x", "v", "g" };

	//중성(21자) ㅏ ㅐ ㅑ ㅒ ㅓ ㅔ ㅕ ㅖ ㅗ ㅘ(9) ㅙ(10) ㅚ(11) ㅛ ㅜ ㅝ(14) ㅞ(15) ㅟ(16) ㅠ ㅡ ㅢ(19) ㅣ
	static String[] jungsungEng = { "k", "o", "i", "O", "j", "p", "u", "P", "h", "hk", "ho", "hl", "y", "n", "nj", "np", "nl", "b", "m", "ml", "l" };

	//종성(28자) <없음> ㄱ ㄲ ㄳ(3) ㄴ ㄵ(5) ㄶ(6) ㄷ ㄹ ㄺ(9) ㄻ(10) ㄼ(11) ㄽ(12) ㄾ(13) ㄿ(14) ㅀ(15) ㅁ ㅂ ㅄ(18) ㅅ ㅆ ㅇ ㅈ ㅊ ㅋ ㅌ ㅍ ㅎ
	static String[] jongsungEng = { " ", "r", "R", "rt", "s", "sw", "sg", "e", "f", "fr", "fa", "fq", "ft", "fx", "fv", "fg", "a", "q", "qt", "t", "T", "d", "w", "c", "z", "x", "v", "g" };

	//초성(19자) ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ
	static char[] chosungKor = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };

	//받침 초성 번호 정의
	static int[] batchimchosungKor = { 1, 2, 4, 7, 0, 8, 16, 17, 0, 19, 20, 21, 22, 0, 23, 24, 25, 26, 27 };

	/**
	 * 시작점..
	 * @param inputKeyword
	 * @param mode
	 * @param typo
	 * @return
	 */
	public String runJasoDecompose(String inputKeyword,int mode,boolean typo) {

		String result="";
		try {
			if (isHangul(inputKeyword)) {
				result=getTokenKor(inputKeyword,mode);
				if (typo) {
					result += getTokenEng(inputKeyword, mode);
				}
			} else {
				result=getTokenKor(inputKeyword,mode);
			}
		} catch(Exception ioe){

		}
		return result;
	}

	private int[] StringDecompositionToNum(char originChar) {

		int[] resultInt 	= new int[3];
		int completeCode 	= originChar;								//char를 코드 번호로 변경한후
		int uniValue 		= completeCode - 0xAC00; 					//Unicode 값으로 환산한다.
		resultInt[2] 		= uniValue % 28;                           	//종성
		resultInt[0] 		= ((uniValue - resultInt[2]) / 28) / 21;   	//초성
		resultInt[1] 		= ((uniValue - resultInt[2]) / 28) % 21;   	//중성                          //종성
		return resultInt;
	}

	/*
	 * 영문 오타 데이터를 넣기 위하여 한글에 대한 영문 오타 값을 리턴하기 위해 자소 분해후 영문으로 리턴한다.
	 */
	@SuppressWarnings("unused")
	private String[] StringDecompositionToEng(char originChar) {
		String[] resultStr = new String[3];

		int completeCode = originChar; 	//char를 코드 번호로 변경한후
		int uniValue = completeCode - 0xAC00;	//Unicode 값으로 환산한다.

		int jong = uniValue % 28;               	//종성
		int cho = ((uniValue - jong) / 28) / 21;	//초성
		int jung = ((uniValue - jong) / 28) % 21;   //중성

		resultStr[0] = chosungEng[cho];
		resultStr[1] = jungsungEng[jung];
		resultStr[2] = jongsungEng[jong];
		return resultStr;
	}

	/**
	 * 한글인지 여부
	 * @param originStr
	 * @return
	 */
	public boolean isHangul(String originStr) {

		int imsi = 0;
		char[] chars=originStr.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != ' ') {

				imsi =  chars[i];

				if (imsi > 44031 && imsi < 55204) {
					//한글 범위에 있다면?
					return true;
				}
			}
		}
		return false;
	}

	private int choJungCode  = 0;			// 초성 + 중성 코드
	private int jongSungCode = 0;			// 종성 코드

	/*
	 * 앞단어 위주의 Prefix를 분해함
	 */
	public String getTokenKor(String originStr,int mode) {

		//원래 단어를 char배열로 변경함
		char[] chars=originStr.toCharArray();

		StringBuilder tokenResult = new StringBuilder();    //최종 결과값을 저장함.
		StringBuilder preUmjul = new StringBuilder(); 		//전단계까지의 음절을 기억하는 변수


		if (mode == 0) {

			for (int i = 0; i < chars.length; i++) {

				doTokenKor(chars, tokenResult, preUmjul, i);
			} //End For

		} else if (mode == 1){

			for (int j = 0; j < chars.length; j++) {

				preUmjul = new StringBuilder();
				choJungCode=0;

				for (int i = j; i < chars.length; i++) {

					doTokenKor(chars, tokenResult, preUmjul, i);
				} //End For
			} // End For

		} else if (mode == 2 ) { //이름을 검색할때 성과 이름 첫자부터 자동완성

			for (int j = 0; j < chars.length; j++) {
				if (j == 2) {
					break;
				}
				preUmjul = new StringBuilder();
				choJungCode=0;

				for (int i = j; i < chars.length; i++) {

					doTokenKor(chars, tokenResult, preUmjul, i);
				} //End For
			} // End For
		}
		return tokenResult.toString();

	}

	/**
	 * 한글이면 분해한다.
	 * @param chars
	 * @param tokenResult
	 * @param preUmjul
	 * @param i
	 */
	private void doTokenKor(char[] chars, StringBuilder tokenResult,
			StringBuilder preUmjul, int i) {

		int[] jasoResult = new int[3];
		int isHangulTest = 0;
		int uniCode 	 = 0;

		if (chars[i] != ' ') {

			isHangulTest = chars[i];

			//한글의 범위
			if (isHangulTest > 44031 && isHangulTest < 55204) {
				//한자(one character)에 대한 것을 시작한다.
				//분해된 자소를 배열로 받는다.
				jasoResult = StringDecompositionToNum(chars[i]);

				//초성만
				char chosungImsi = chosungKor[jasoResult[0]];
				Character crChosungImsi=new Character(chosungImsi);	//char to string 형변환

				//처음 초성을 먼저 넣는다.
				tokenResult.append(preUmjul + crChosungImsi.toString() + config.SPLIT_CHAR);

				//저장해둔 초성+중성 코드가 있는 경우 전 글자는 초+중으로 끝났다.
				if (choJungCode != 0) {

					//이 경우 현재글자의 초성을  지난글자에 종성에 더 보태서 문자를 하나 더 넣는다.
					//batchimchosung[UmsoResult[0]];
					boolean multiBatchim = false;// 종성 받침의 ㄵ 와 같은 경우인가 아닌가/

					//종성이 있는 받침
					if (jongSungCode != 0) {
						/**
						 * 복자음 분리
						 */
						choJungCode = splitMulitBatchim(tokenResult
								, preUmjul
								, jasoResult
								, choJungCode
								, jongSungCode
								, multiBatchim);
					} else {

						//종성 받침이 없으므로 초+중 코드에 + 초성 받침을 하나 추가해서 넣는다.
						choJungCode = choJungCode + batchimchosungKor[jasoResult[0]];
						char choJungCodeImsi = (char)choJungCode;
						Character crChoJungCodeImsi = new Character(choJungCodeImsi);
						tokenResult.append(preUmjul.toString().substring(0, preUmjul.length() - 1) + crChoJungCodeImsi.toString() + config.SPLIT_CHAR);
					}
				}

				/**
				 * 복모음 분리
				 */
				splitMultiMoeum(tokenResult, preUmjul, jasoResult);


				//초성+중성 을 넣는다.
				uniCode = (0xAC00) + (jasoResult[0] * 21 * 28) + (jasoResult[1] * 28) + (0);
				Character crChoJungUniCode = new Character((char)uniCode);
				tokenResult.append(preUmjul + crChoJungUniCode.toString() + config.SPLIT_CHAR);


				//초성+중성+종성
				if (jasoResult[2] != 0) {

					uniCode = (0xAC00) + (jasoResult[0] * 21 * 28) + (jasoResult[1] * 28) + (jasoResult[2]);
					Character crChoJungJongUniCode = new Character((char)uniCode);
					tokenResult.append(preUmjul + crChoJungJongUniCode.toString() + config.SPLIT_CHAR);

					//ㄱ ㄴ ㄹ ㅂ을 종성으로 가질경우
					if (jasoResult[2] == 1 || jasoResult[2] == 4 || jasoResult[2] == 8 || jasoResult[2] == 17) {

						choJungCode = (0xAC00) + (jasoResult[0] * 21 * 28) + (jasoResult[1] * 28); //초성과 중성으로만 결합된 코드를 저장해 둔다.
						jongSungCode = jasoResult[2]; //마지막 종성코드를 저장해둔다.

					} else {

						//위 의 상황 이외에는 변수 초기화
						choJungCode = 0;
						jongSungCode = 0;
					}
				} else {

					//종성이 없을때
					choJungCode = (0xAC00) + (jasoResult[0] * 21 * 28) + (jasoResult[1] * 28); //초성과 중성으로만 결합된 코드를 저장해 둔다.
					jongSungCode=0;
				}

				//마지막 저장 유니코드를 방금전 음절을 저장해 둔다.
				Character crPreUmjul = new Character((char)uniCode);
				preUmjul.append(crPreUmjul.toString());

			} else {

				//한글 이외의 문자인 경우
				Character cr4 = new Character(chars[i]);
				tokenResult.append(preUmjul + cr4.toString().toLowerCase() + config.SPLIT_CHAR);
				preUmjul.append(cr4.toString().toLowerCase());
			}
		}
		else {

			//공백인경우
			preUmjul.append(" ");
		}
	}


	/**
	 * 복모음 분리 (만약 복모음이라면, 분리해서 넣어준다.)
	 * @author	 최일규
	 * @since	2013-09-14
	 */
	private void splitMultiMoeum(StringBuilder tokenResult
			, StringBuilder preUmjul
			, int[] jasoResult) {

		int uniCode;

		Character multiMoeum = null;

		//만약 복모음이라면, 여기서 분리해서 넣어줌.
		switch(jasoResult[1]) {

			case 9  :
			case 10 :
			case 11 :
	
				//ㅘ, ㅙ, ㅚ 일때, 중성에 해당하는 인덱스 8(ㅗ)를 강제로 추가한다.
				uniCode = (0xAC00) + (jasoResult[0] * 21 * 28) + (8 * 28) + (0);
				multiMoeum = new Character((char)uniCode);
				tokenResult.append(preUmjul + multiMoeum.toString() + config.SPLIT_CHAR);
				break;
	
			case 14 :
			case 15 :
			case 16 :
	
				//ㅝ, ㅞ, ㅟ중성에 해당하는 인덱스 13(ㅜ)를 강제로 추가한다.
				uniCode = (0xAC00) + (jasoResult[0] * 21 * 28) + (13 * 28) + (0);
				multiMoeum = new Character((char)uniCode);
				tokenResult.append(preUmjul + multiMoeum.toString() + config.SPLIT_CHAR);
				break;
	
			case 19 :
	
				//ㅢ 중성에 해당하는 인덱스 18(ㅡ)를 강제로 추가한다.
				uniCode = (0xAC00) + (jasoResult[0] * 21 * 28) + (18 * 28) + (0);
				multiMoeum = new Character((char)uniCode);
				tokenResult.append(preUmjul + multiMoeum.toString() + config.SPLIT_CHAR);
				break;

		}
	}

	/**
	 * 복자음 분리
	 * @param tokenResult
	 * @param preUmjul
	 * @param jasoResult
	 * @param choJungCode
	 * @param jongSungCode
	 * @param multiBatchim
	 * @return
	 */
	private int splitMulitBatchim( StringBuilder tokenResult
			, StringBuilder preUmjul
			, int[] jasoResult
			, int choJungCode
			, int jongSungCode
			, boolean multiBatchim) {

		switch (jongSungCode)
		{
			case 1:
				if (jasoResult[0] == 9) //ㄳ의 경우
				{
					choJungCode = choJungCode + 3;
					multiBatchim = true;
				}
				break;
			case 4:
				if (jasoResult[0] == 12) //ㄵ의 경우
				{
					choJungCode = choJungCode + 5;
					multiBatchim = true;
				}
				else if (jasoResult[0] == 18) //ㄶ의 경우
				{
					choJungCode = choJungCode + 6;
					multiBatchim = true;
				}
				break;
			case 8:
				if (jasoResult[0] == 0) //ㄺ 경우
				{
					choJungCode = choJungCode + 9;
					multiBatchim = true;
				}
				else if (jasoResult[0] == 6) //ㄻ의 경우
				{
					choJungCode = choJungCode + 10;
					multiBatchim = true;
				}
				else if (jasoResult[0] == 7) //ㄼ의 경우
				{
					choJungCode = choJungCode + 11;
					multiBatchim = true;
				}
				else if (jasoResult[0] == 9) //ㄽ의 경우
				{
					choJungCode = choJungCode + 12;
					multiBatchim = true;
				}
				else if (jasoResult[0] == 16) //ㄾ의 경우
				{
					choJungCode = choJungCode + 13;
					multiBatchim = true;
				}
				else if (jasoResult[0] == 17) //ㄾ의 경우
				{
					choJungCode = choJungCode + 14;
					multiBatchim = true;
				}
				else if (jasoResult[0] == 18) //ㄾ의 경우
				{
					choJungCode = choJungCode + 15;
					multiBatchim = true;
				}
				break;
			case 17:
				if (jasoResult[0] == 9) //ㅄ 경우
				{
					choJungCode = choJungCode + 18;
					multiBatchim = true;
				}
				break;
		}

		if (multiBatchim) {

			char addChoJungCode = (char)choJungCode;	//뒷 종성을 앞 중성과 합한 코드다.
			Character crAddChoJungCode = new Character(addChoJungCode);
			tokenResult.append(preUmjul.toString().substring(0, preUmjul.length() - 1) + crAddChoJungCode.toString() + config.SPLIT_CHAR);
		}
		return choJungCode;
	}


	/**
	 * 영문 오타 토큰을 가져 온다
	 * @param originStr
	 * @param mode
	 * @return
	 */
	public String getTokenEng(String originStr, int mode) {

		//원래 단어를 char배열로 변경함
		char[] chars=originStr.toCharArray();
		StringBuilder tokenResult = new StringBuilder();   //최종 결과값을 저장함.
		StringBuilder preEngUmjul = new StringBuilder();   //전단계까지의 음절을 기억하는 변수

		if (mode == 0) {
			for (int i = 0; i < chars.length; i++) {

				doTokenEng(chars, tokenResult, preEngUmjul, i);
			}

		} else if (mode == 1) {

			for (int j = 0; j < chars.length; j++) {

				preEngUmjul = new StringBuilder();
				for (int i = j; i < chars.length; i++) {

					doTokenEng(chars, tokenResult, preEngUmjul, i);
				} //End for
			} //End for

		} else if (mode == 2) {

			for (int j = 0; j < chars.length; j++) {

				if (j == 2) {

					break;
				}
				preEngUmjul = new StringBuilder();

				for (int i = j; i < chars.length; i++) {

					doTokenEng(chars, tokenResult, preEngUmjul, i);
				} //End for
			} //End for
		}
		return tokenResult.toString();
	}

	/**
	 * 영문으로 분해한다.
	 * @param chars
	 * @param tokenResult
	 * @param preEngUmjul
	 * @param i
	 */
	private void doTokenEng(char[] chars, StringBuilder tokenResult,
			StringBuilder preEngUmjul, int i) {

		int[] jasoResult = new int[3];
		int isHangulTest = 0;

		if (chars[i] != ' ') {

			isHangulTest = chars[i];

			//한글의 범위
			if (isHangulTest > 44031 && isHangulTest < 55204) {
				//한자에 대한 것을 시작한다.
				//분해된 음소를 배열로 받는다.
				jasoResult = StringDecompositionToNum(chars[i]);

				//초성만
				String chochar = chosungEng[jasoResult[0]];

				//접두어의 길이가 3보다 커야 입력 예 sk 가 '나'로 인지되는 것을 막음
				if (i!=0) {
					tokenResult.append(preEngUmjul + chochar + config.SPLIT_CHAR);
				}

				//영문캐릭터
				chochar = chosungEng[jasoResult[0]];
				String jungchar = jungsungEng[jasoResult[1]];

				//접두어의 길이가 3보다 커야 입력 예 sk 가 '나'로 인지되는 것을 막음
				if (i!=0) {
					tokenResult.append(preEngUmjul + chochar + jungchar + config.SPLIT_CHAR);
				}


				//초성+중성+종성
				String jongchar = "";
				if (jasoResult[2] != 0) {

					chochar = chosungEng[jasoResult[0]];
					jungchar = jungsungEng[jasoResult[1]];
					jongchar = jongsungEng[jasoResult[2]];

					//접두어의 길이가 3보다 커야 입력 예 sk 가 '나'로 인지되는 것을 막음
					if (i!=0) {
						tokenResult.append(preEngUmjul + chochar + jungchar + jongchar + config.SPLIT_CHAR);
					}
				}
				preEngUmjul.append(chochar + jungchar + jongchar);

			} else {

				//그외의 문자인 경우
				Character crImsi=new Character(chars[i]);
				tokenResult.append(preEngUmjul + crImsi.toString().toLowerCase() + config.SPLIT_CHAR);
				preEngUmjul.append(crImsi.toString().toLowerCase());
			}

		} else {

			//공백인경우
			preEngUmjul.append(" ");
		}
	}

}