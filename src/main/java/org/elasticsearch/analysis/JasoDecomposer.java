package org.elasticsearch.analysis;

/**
 * 자동완성용 자소분해 (자소분해 with WhiteSpace)
 *
 * @author 최일규
 * @since 2016-02-10
 */
public class JasoDecomposer {

    //초성(19자) ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ
    static String[] chosungKor = {"ㄱ", "ㄱㄱ", "ㄴ", "ㄷ", "ㄷㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅂㅂ", "ㅅ", "ㅅㅅ", "ㅇ", "ㅈ", "ㅈㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"};
    //중성(21자) ㅏ ㅐ ㅑ ㅒ ㅓ ㅔ ㅕ ㅖ ㅗ ㅘ(9) ㅙ(10) ㅚ(11) ㅛ ㅜ ㅝ(14) ㅞ(15) ㅟ(16) ㅠ ㅡ ㅢ(19) ㅣ
    static String[] jungsungKor = {"ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅗㅏ", "ㅗㅐ", "ㅗㅣ", "ㅛ", "ㅜ", "ㅜㅓ", "ㅜㅔ", "ㅜㅣ", "ㅠ", "ㅡ", "ㅡㅣ", "ㅣ"};
    //종성(28자) <없음> ㄱ ㄲ ㄳ(3) ㄴ ㄵ(5) ㄶ(6) ㄷ ㄹ ㄺ(9) ㄻ(10) ㄼ(11) ㄽ(12) ㄾ(13) ㄿ(14) ㅀ(15) ㅁ ㅂ ㅄ(18) ㅅ ㅆ ㅇ ㅈ ㅊ ㅋ ㅌ ㅍ ㅎ
    static String[] jongsungKor = {" ", "ㄱ", "ㄱㄱ", "ㄱㅅ", "ㄴ", "ㄴㅈ", "ㄴㅎ", "ㄷ", "ㄹ", "ㄹㄱ", "ㄹㅁ", "ㄹㅂ", "ㄹㅅ", "ㄹㅌ", "ㄹㅍ", "ㄹㅎ", "ㅁ", "ㅂ", "ㅂㅅ", "ㅅ", "ㅅㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"};

    static String[] chosungEng = {"r", "R", "s", "e", "E", "f", "a", "q", "Q", "t", "T", "d", "w", "W", "c", "z", "x", "v", "g"};
    static String[] jungsungEng = {"k", "o", "i", "O", "j", "p", "u", "P", "h", "hk", "ho", "hl", "y", "n", "nj", "np", "nl", "b", "m", "ml", "l"};
    static String[] jongsungEng = {"", "r", "R", "rt", "s", "sw", "sg", "e", "f", "fr", "fa", "fq", "ft", "fx", "fv", "fg", "a", "q", "qt", "t", "T", "d", "w", "c", "z", "x", "v", "g"};

    static String[] mistyping = {"ㅁ", "ㅠ", "ㅊ", "ㅇ", "ㄷ", "ㄹ", "ㅎ", "ㅗ", "ㅑ", "ㅓ", "ㅏ", "ㅣ", "ㅡ", "ㅜ", "ㅐ", "ㅔ", "ㅂ", "ㄱ", "ㄴ", "ㅅ", "ㅕ", "ㅍ", "ㅈ", "ㅌ", "ㅛ", "ㅋ"};

    public String runJasoDecompose(String originStr, TokenizerOptions options) {

        if (!originStr.isEmpty()) {

            //lowercase 처리
            originStr = originStr.toLowerCase();

            char[] termBuffer = originStr.toCharArray();
            StringBuffer korBuffer = new StringBuffer();
            StringBuffer engBuffer = new StringBuffer();
            StringBuffer chosungBuffer = new StringBuffer();
            StringBuffer mistypingBuffer = new StringBuffer();
            StringBuffer etcBuffer = new StringBuffer();
            StringBuffer returnBuffer = new StringBuffer();

            //첫글자가 한글일때만 초성분해
            boolean firstCharType = false;
            if (termBuffer.length > 0)
                firstCharType = isHangul(Character.toString(termBuffer[0]));

            //자소포함여부
            boolean jaso = isJaso(originStr);
            //한글포함여부
            boolean hangul = isHangul(originStr);
            //영문포함여부
            boolean english = isEnglish(originStr);

            int strLen = originStr.length();

            int cho;
            int jung;
            int jong;
            for (int i = 0; i < termBuffer.length; i++) {
                char ch = termBuffer[i];

                //가(AC00)~힣(D7A3) 에 속한 글자면 분해
                if (ch >= 0xAC00 && ch <= 0xD7A3 && !jaso) {
                    //Unicode 값으로 환산한다.
                    int uniValue = ch - 0xAC00;

                    jong = uniValue % 28;                   //종성
                    cho = ((uniValue - jong) / 28) / 21;    //초성
                    jung = ((uniValue - jong) / 28) % 21;   //중성

                    //한글초성
                    korBuffer.append(chosungKor[cho]);

                    //한글에 대한 초성처리 (일반적으로 색인시 초성을 담는다.)
                    if (options.isChosung() && firstCharType) {
                        //초성은 2자이상일때 포함
                        if (strLen >= 2)
                            chosungBuffer.append(chosungKor[cho]);
                    }

                    //한글문장에 대한 영문오타처리 (ㄱ -> r)
                    if (options.isMistype()) {
                        engBuffer.append(chosungEng[cho].toLowerCase());
                    }

                    //한글중성
                    korBuffer.append(jungsungKor[jung]);

                    //한글문장에 대한 영문오타처리 (ㅏ-> k)
                    if (options.isMistype()) {
                        engBuffer.append(jungsungEng[jung].toLowerCase());
                    }

                    //받침이 있으면
                    if (jong != 0) {
                        korBuffer.append(jongsungKor[jong]);

                        //한글문장에 대한 영문오타처리 (ㄲ -> R)
                        if (options.isMistype()) {
                            engBuffer.append(jongsungEng[jong].toLowerCase());
                        }
                    }
                } else {

                    if (options.isMistype()) {
                        if (!jaso) {
                            if (hangul) {
                                korBuffer.append(ch);
                            }
                            engBuffer.append(ch);
                        }
                    } else {
                        if (!jaso) {
                            if (hangul) {
                                korBuffer.append(ch);
                            } else {
                                engBuffer.append(ch);
                            }
                        }
                    }

                    //영문문장에 대한 한글오타처리 (hello -> ㅗ디ㅣㅐ)
                    if (options.isMistype() && !hangul) {
                        int index;
                        if (ch >= 0x61 && ch <= 0x7A) {
                            //소문자
                            index = (int) ch - 97;
                            mistypingBuffer.append(mistyping[index]);
                        } else if (ch >= 0x41 && ch <= 0x5A) {
                            //대문자
                            index = (int) ch - 65;
                            mistypingBuffer.append(mistyping[index]);
                        } else {
                            if (hangul || english)
                                mistypingBuffer.append(ch);
                        }
                    }
                }

                //추가적인 예외상황으로 추가 토큰처리 (ㅗ디ㅣㅐ -> ㅗㄷㅣㅣㅐ 자소분해)
                if (jaso) {

                    if (ch >= 0xAC00 && ch <= 0xD7A3) {
                        //Unicode 값으로 환산한다.
                        int uniValue = ch - 0xAC00;

                        jong = uniValue % 28;                   //종성
                        cho = ((uniValue - jong) / 28) / 21;    //초성
                        jung = ((uniValue - jong) / 28) % 21;   //중성

                        etcBuffer.append(chosungKor[cho]);
                        etcBuffer.append(jungsungKor[jung]);
                        //받침이 있으면
                        if (jong != 0) {
                            etcBuffer.append(jongsungKor[jong]);
                        }
                    } else if (isJaso(Character.toString(ch))) {
                        //복자음 강제분리
                        switch (ch) {
                            case 'ㄲ':
                                etcBuffer.append("ㄱㄱ");
                                break;
                            case 'ㄳ':
                                etcBuffer.append("ㄱㅅ");
                                break;
                            case 'ㄵ':
                                etcBuffer.append("ㄴㅈ");
                                break;
                            case 'ㄶ':
                                etcBuffer.append("ㄴㅎ");
                                break;
                            case 'ㄺ':
                                etcBuffer.append("ㄹㄱ");
                                break;
                            case 'ㄻ':
                                etcBuffer.append("ㄹㅁ");
                                break;
                            case 'ㄼ':
                                etcBuffer.append("ㄹㅂ");
                                break;
                            case 'ㄽ':
                                etcBuffer.append("ㄹㅅ");
                                break;
                            case 'ㄾ':
                                etcBuffer.append("ㄹㅌ");
                                break;
                            case 'ㄿ':
                                etcBuffer.append("ㄹㅍ");
                                break;
                            case 'ㅀ':
                                etcBuffer.append("ㄹㅎ");
                                break;
                            case 'ㅄ':
                                etcBuffer.append("ㅂㅅ");
                            case 'ㄸ':
                                etcBuffer.append("ㄷㄷ");
                                break;
                            case 'ㅃ':
                                etcBuffer.append("ㅂㅂ");
                                break;
                            case 'ㅆ':
                                etcBuffer.append("ㅅㅅ");
                                break;
                            case 'ㅉ':
                                etcBuffer.append("ㅈㅈ");
                                break;
                            default:
                                etcBuffer.append(ch);
                        }
                    } else {
                        etcBuffer.append(ch);
                    }
                }
            }

            //결과 조합

            //공백을 붙인 전체 문자열 (한글)
            if (korBuffer.indexOf(" ") != -1) {
                if (korBuffer.length() > 0) {
                    returnBuffer.append(korBuffer.toString().replaceAll(" ", ""));
                    returnBuffer.append(" ");
                }
            }

            //공백으로 분리된 문자열 (한글)
            if (korBuffer.length() > 0) {
                returnBuffer.append(korBuffer.toString());
                returnBuffer.append(" ");
            }

            //공백을 붙인 전체 문자열 (영문)
            if (engBuffer.indexOf(" ") != -1) {
                if (engBuffer.length() > 0) {
                    returnBuffer.append(engBuffer.toString().replaceAll(" ", ""));
                    returnBuffer.append(" ");
                }
            }

            //공백으로 분리된 문자열 (영문)
            if (engBuffer.length() > 0) {
                returnBuffer.append(engBuffer.toString());
                returnBuffer.append(" ");
            }

            //공백을 붙인 전체 문자열 (오타)
            if (mistypingBuffer.indexOf(" ") != -1) {
                if (mistypingBuffer.length() > 0) {
                    returnBuffer.append(mistypingBuffer.toString().replaceAll(" ", ""));
                    returnBuffer.append(" ");
                }
            }

            //공백으로 분리된 문자열 (오타)
            if (mistypingBuffer.length() > 0) {
                returnBuffer.append(mistypingBuffer.toString());
                returnBuffer.append(" ");
            }

            if (chosungBuffer.length() > 0) {
                returnBuffer.append(chosungBuffer.toString());
                returnBuffer.append(" ");
            }

            if (etcBuffer.length() > 0) {
                returnBuffer.append(etcBuffer.toString());
                returnBuffer.append(" ");
            }

            return returnBuffer.toString().trim();
        } else {
            return "";
        }
    }

    /**
     * 문자열에 한글포함 여부
     *
     * @param str
     * @return
     */
    private boolean isHangul(String str) {
        return str.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    }

    /**
     * 문자열에 영문포함 여부
     *
     * @param str
     * @return
     */
    private boolean isEnglish(String str) {
        return str.matches(".*[a-zA-Z]+.*");
    }

    /**
     * 문자열에 초성,중성 포함 여부
     *
     * @param str
     * @return
     */
    private boolean isJaso(String str) {
        return str.matches(".*[ㄱ-ㅎㅏ-ㅣ]+.*");
    }
}