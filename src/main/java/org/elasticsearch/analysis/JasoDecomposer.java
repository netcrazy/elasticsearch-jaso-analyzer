package org.elasticsearch.analysis;

/**
 * 자동완성용 자소분해 (자소분해 with WhiteSpace)
 * @author	최일규
 * @since	2016-02-10
 */
public class JasoDecomposer {

	//초성(19자) ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ
	static String[] chosungKor = { "ㄱ", "ㄱㄱ", "ㄴ", "ㄷ", "ㄷㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅂㅂ", "ㅅ", "ㅅㅅ", "ㅇ", "ㅈ", "ㅈㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ" };
	//중성(21자) ㅏ ㅐ ㅑ ㅒ ㅓ ㅔ ㅕ ㅖ ㅗ ㅘ(9) ㅙ(10) ㅚ(11) ㅛ ㅜ ㅝ(14) ㅞ(15) ㅟ(16) ㅠ ㅡ ㅢ(19) ㅣ
	static String[] jungsungKor = { "ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅗㅏ", "ㅗㅐ", "ㅗㅣ", "ㅛ", "ㅜ", "ㅜㅓ", "ㅜㅔ", "ㅜㅣ", "ㅠ", "ㅡ", "ㅡㅣ", "ㅣ" };
	//종성(28자) <없음> ㄱ ㄲ ㄳ(3) ㄴ ㄵ(5) ㄶ(6) ㄷ ㄹ ㄺ(9) ㄻ(10) ㄼ(11) ㄽ(12) ㄾ(13) ㄿ(14) ㅀ(15) ㅁ ㅂ ㅄ(18) ㅅ ㅆ ㅇ ㅈ ㅊ ㅋ ㅌ ㅍ ㅎ
	static String[] jongsungKor = { " ", "ㄱ", "ㄱㄱ", "ㄱㅅ", "ㄴ", "ㄴㅈ", "ㄴㅎ", "ㄷ", "ㄹ", "ㄹㄱ", "ㄹㅁ", "ㄹㅂ", "ㄹㅅ", "ㄹㅌ", "ㄹㅍ", "ㄹㅎ", "ㅁ", "ㅂ", "ㅂㅅ", "ㅅ", "ㅅㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ" };

    static String[] chosungEng = { "r", "R", "s", "e", "E", "f", "a", "q", "Q" , "t" , "T" , "d" , "w" , "W" , "c" , "z" , "x" , "v" , "g" };
    static String[] jungsungEng = { "k", "o", "i", "O", "j", "p", "u", "P", "h" , "hk" , "ho" , "hl" , "y" , "n" , "nj" , "np" , "nl" , "b" , "m" , "ml" , "l" };
    static String[] jongsungEng = { "", "r", "R", "rt", "s", "sw", "sg", "e" , "f" , "fr" , "fa" , "fq" , "ft" , "fx" , "fv" , "fg" , "a" , "q" , "qt", "t" , "T" , "d" , "w" , "c" , "z" , "x" , "v" , "g" };

    static String[] mistyping = { "ㅁ", "ㅠ", "ㅊ", "ㅇ", "ㄷ", "ㄹ", "ㅎ", "ㅗ" , "ㅑ" , "ㅓ" , "ㅏ" , "ㅣ" , "ㅡ" , "ㅜ" , "ㅐ" , "ㅔ" , "ㅂ" , "ㄱ" , "ㄴ", "ㅅ" , "ㅕ" , "ㅍ" , "ㅈ" , "ㅌ" , "ㅛ" , "ㅋ" };

	public String runJasoDecompose(String originStr, TokenizerOptions options) {
		
		char[] termBuffer 	= originStr.toCharArray();
		StringBuffer bufferKor = new StringBuffer();
        StringBuffer bufferEng = new StringBuffer();
        StringBuffer bufferChosung = new StringBuffer();
        StringBuffer bufferMistyping = new StringBuffer();
        StringBuffer bufferTmp = new StringBuffer();

        boolean jaso = isJaso(originStr);

		int cho;
		int jung;
		int jong;
		for(int i = 0; i < termBuffer.length; i++) {
			char ch = termBuffer[i];
			
			//가(AC00)~힣(D7A3) 에 속한 글자면 분해
			if(ch >= 0xAC00 && ch <= 0xD7A3 && !jaso) {
                //Unicode 값으로 환산한다.
                int uniValue = ch - 0xAC00;

                jong = uniValue % 28;                    //종성
                cho  = ((uniValue - jong) / 28) / 21;   //초성
                jung = ((uniValue - jong) / 28) % 21;   //중성

                //한글초성
                bufferKor.append(chosungKor[cho]);

                //한글에 대한 초성만 토큰처리 (일반적으로 색인시 분해함)
                if(options.isChosung()) {
                    bufferChosung.append(chosungKor[cho]);
                }

                //한글문장에 대한 영문오타처리 (ㄱ -> r)
                if(options.isMistype()) {
                    bufferEng.append(chosungEng[cho]);
                }

                //한글중성
                bufferKor.append(jungsungKor[jung]);

                //한글문장에 대한 영문오타처리 (ㅏ-> k)
                if(options.isMistype()) {
                    bufferEng.append(jungsungEng[jung]);
                }

                //받침이 있으면
                if(jong != 0) {
                    bufferKor.append(jongsungKor[jong]);

                    //한글문장에 대한 영문오타처리 (ㄲ -> R)
                    if(options.isMistype()) {
                        bufferEng.append(jongsungEng[jong]);
                    }
                }
			} else {
                //한글공백에 대한 토큰분리
                if(ch == ' ') {
                    bufferKor.append(ch);
                    bufferChosung.append(ch);
                }

                //영문처리
                if(ch >= 0x61 && ch <= 0x7A || ch >= 0x41 && ch <= 0x5A || ch == ' ' ) {
                    bufferEng.append(ch);
                }

                //영문문장에 대한 한글오타처리 (hello -> ㅗ디ㅣㅐ)
                if(options.isMistype()) {
                    int index;
                    if(ch >= 0x61 && ch <= 0x7A) {
                        //소문자
                        index = (int) ch-97;
                        bufferMistyping.append(mistyping[index]);
                    } else if (ch >= 0x41 && ch <= 0x5A) {
                        //대문자
                        index = (int) ch-65;
                        bufferMistyping.append(mistyping[index]);
                    } else if(ch == ' ') {
                        //공백에 대한 토큰분리
                        bufferMistyping.append(ch);
                    }
                }
			}

            //추가적인 예외상황으로 추가 토큰처리 (ㅗㄷㅣㅣㅐ 검색시)
            if(options.isChosung() && jaso) {
                if(ch >= 0xAC00 && ch <= 0xD7A3) {

                    //Unicode 값으로 환산한다.
                    int uniValue = ch - 0xAC00;

                    jong = uniValue % 28;                   //종성
                    cho  = ((uniValue - jong) / 28) / 21;   //초성
                    jung = ((uniValue - jong) / 28) % 21;   //중성

                    bufferTmp.append(chosungKor[cho]);
                    bufferTmp.append(jungsungKor[jung]);
                    //받침이 있으면
                    if(jong != 0) {
                        bufferTmp.append(jongsungKor[jong]);
                    }
                }

                if(isJaso(Character.toString(ch))) {
                    //복자음 강제분리
                    switch(ch) {
                        case 'ㄲ': bufferTmp.append("ㄱㄱ"); break;
                        case 'ㄳ': bufferTmp.append("ㄱㅅ"); break;
                        case 'ㄵ': bufferTmp.append("ㄴㅈ"); break;
                        case 'ㄶ': bufferTmp.append("ㄴㅎ"); break;
                        case 'ㄺ': bufferTmp.append("ㄹㄱ"); break;
                        case 'ㄻ': bufferTmp.append("ㄹㅁ"); break;
                        case 'ㄼ': bufferTmp.append("ㄹㅂ"); break;
                        case 'ㄽ': bufferTmp.append("ㄹㅅ"); break;
                        case 'ㄾ': bufferTmp.append("ㄹㅌ"); break;
                        case 'ㄿ': bufferTmp.append("ㄹㅍ"); break;
                        case 'ㅀ': bufferTmp.append("ㄹㅎ"); break;
                        case 'ㅄ': bufferTmp.append("ㅂㅅ"); break;
                        default: bufferTmp.append(ch);
                    }
                } else {
                    if(ch == ' ') {
                        bufferTmp.append(ch);
                    }
                }
            }
		}

        return String.format("%s %s %s %s %s"
                , bufferKor.toString()
                , bufferEng.toString()
                , bufferMistyping.toString()
                , bufferChosung.toString()
                , bufferTmp.toString()).trim();
	}

    /**
     * 문자열에 한글포함 여부
     * @param str
     * @return
     */
    private boolean isHangul(String str) {
        return str.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    }

    /**
     * 문자열에 초성,중성 포함 여부
     * @param str
     * @return
     */
    private boolean isJaso(String str) {
        return str.matches(".*[ㄱ-ㅎㅏ-ㅣ]+.*");
    }
}