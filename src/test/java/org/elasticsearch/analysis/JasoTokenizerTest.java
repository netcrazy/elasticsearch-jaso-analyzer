package org.elasticsearch.analysis;
import junit.framework.TestCase;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 토크나이저 유닛테스트
 * @author 최일규
 * @since 2016-02-11
 *
 */
public class JasoTokenizerTest extends TestCase {
	
	public void testTokenizer() throws IOException {

        long start = System.currentTimeMillis();
        TokenizerOptions options = TokenizerOptions.create("test");

        //한영오타에 대한 토큰 추출여부 (hello -> ㅗㄷㅣㅣㅐ, 최일규 -> chldlfrb)
        options.setMistype(true);

        //초성검색을 위한 토큰 추출여부 (최일규 -> ㅊㅇㄱ)
        options.setChosung(true);

        List<TestCaseVO> testCase = new ArrayList<TestCaseVO>();

        if(options.isMistype() == true && options.isChosung() == true) {

            testCase.add(new TestCaseVO("최일규", "ㅊㅗㅣㅇㅣㄹㄱㅠ/chldlfrb/ㅊㅇㄱ"));
            testCase.add(new TestCaseVO("소녀시대", "ㅅㅗㄴㅕㅅㅣㄷㅐ/thsutleo/ㅅㄴㅅㄷ"));
            testCase.add(new TestCaseVO("Hello", "hello/ㅗㄷㅣㅣㅐ"));
            testCase.add(new TestCaseVO("Hello~", "hello~/ㅗㄷㅣㅣㅐ~"));
            testCase.add(new TestCaseVO("무조건 해피엔딩", "ㅁㅜㅈㅗㄱㅓㄴㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ/anwhrjsgovldpseld/ㅁㅈㄱㅎㅍㅇㄷ"));
            testCase.add(new TestCaseVO("소원을 말해봐 genie mixversion##!", "ㅅㅗㅇㅜㅓㄴㅇㅡㄹㅁㅏㄹㅎㅐㅂㅗㅏgeniemixversion##!/thdnjsdmfakfgoqhkgeniemixversion##!/ㅅㅇㅇㅁㅎㅂ"));
            testCase.add(new TestCaseVO("hush hush; hush hush", "hushhush;hushhush/ㅗㅕㄴㅗㅗㅕㄴㅗ;ㅗㅕㄴㅗㅗㅕㄴㅗ"));
            testCase.add(new TestCaseVO("라벨:모음곡(거울)제4곡(어릿광대의 아침노래)(서현)", "ㄹㅏㅂㅔㄹ:ㅁㅗㅇㅡㅁㄱㅗㄱ(ㄱㅓㅇㅜㄹ)ㅈㅔ4ㄱㅗㄱ(ㅇㅓㄹㅣㅅㄱㅗㅏㅇㄷㅐㅇㅡㅣㅇㅏㅊㅣㅁㄴㅗㄹㅐ)(ㅅㅓㅎㅕㄴ)/fkqpf:ahdmarhr(rjdnf)wp4rhr(djfltrhkdeodmldkclashfo)(tjgus)/ㄹㅂㅁㅇㄱㄱㅇㅈㄱㅇㄹㄱㄷㅇㅇㅊㄴㄹㅅㅎ"));
            testCase.add(new TestCaseVO("ㅗ디ㅣㅐ", "ㅗㄷㅣㅣㅐ")); //hello
            testCase.add(new TestCaseVO("째깅", "ㅈㅈㅐㄱㅣㅇ/world/ㅈㅈㄱ")); //World
            testCase.add(new TestCaseVO("ㅣㅐ햣ㄷ초", "ㅣㅐㅎㅑㅅㄷㅊㅗ")); //logitech
            testCase.add(new TestCaseVO("퍗므ㅑㅜ", "ㅍㅑㅅㅁㅡㅑㅜ")); //vitamin
            testCase.add(new TestCaseVO("랱", "ㄹㅐㅌ/fox")); //fox
            testCase.add(new TestCaseVO("ㅅ딛퍄냐ㅐㅜ", "ㅅㄷㅣㄷㅍㅑㄴㅑㅐㅜ")); //television
            testCase.add(new TestCaseVO("최", "ㅊㅗㅣ/chl")); //television
            testCase.add(new TestCaseVO("ㅅㄴㅅㄷ", "ㅅㄴㅅㄷ")); //소녀시대 초성검색
            testCase.add(new TestCaseVO("##%@#$%()*&^%$#@!", "##%@#$%()*&^%$#@!"));
            testCase.add(new TestCaseVO("2000 최신가요", "2000ㅊㅗㅣㅅㅣㄴㄱㅏㅇㅛ/2000chltlsrkdy"));
            testCase.add(new TestCaseVO("15&", "15&"));
            testCase.add(new TestCaseVO("최신가요 1990", "ㅊㅗㅣㅅㅣㄴㄱㅏㅇㅛ1990/chltlsrkdy1990/ㅊㅅㄱㅇ"));
            testCase.add(new TestCaseVO("최신가요&", "ㅊㅗㅣㅅㅣㄴㄱㅏㅇㅛ&/chltlsrkdy&/ㅊㅅㄱㅇ"));

        } else if(options.isMistype() == true && options.isChosung() == false) {

            testCase.add(new TestCaseVO("최일규", "ㅊㅗㅣㅇㅣㄹㄱㅠ/chldlfrb"));
            testCase.add(new TestCaseVO("소녀시대", "ㅅㅗㄴㅕㅅㅣㄷㅐ/thsutleo"));
            testCase.add(new TestCaseVO("Hello", "hello/ㅗㄷㅣㅣㅐ"));
            testCase.add(new TestCaseVO("Hello~", "hello~/ㅗㄷㅣㅣㅐ~"));
            testCase.add(new TestCaseVO("무조건 해피엔딩", "ㅁㅜㅈㅗㄱㅓㄴㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ/anwhrjsgovldpseld"));
            testCase.add(new TestCaseVO("소원을 말해봐 genie mixversion##!", "ㅅㅗㅇㅜㅓㄴㅇㅡㄹㅁㅏㄹㅎㅐㅂㅗㅏgeniemixversion##!/thdnjsdmfakfgoqhkgeniemixversion##!"));
            testCase.add(new TestCaseVO("hush hush; hush hush", "hushhush;hushhush/ㅗㅕㄴㅗㅗㅕㄴㅗ;ㅗㅕㄴㅗㅗㅕㄴㅗ"));
            testCase.add(new TestCaseVO("라벨:모음곡(거울)제4곡(어릿광대의 아침노래)(서현)", "ㄹㅏㅂㅔㄹ:ㅁㅗㅇㅡㅁㄱㅗㄱ(ㄱㅓㅇㅜㄹ)ㅈㅔ4ㄱㅗㄱ(ㅇㅓㄹㅣㅅㄱㅗㅏㅇㄷㅐㅇㅡㅣㅇㅏㅊㅣㅁㄴㅗㄹㅐ)(ㅅㅓㅎㅕㄴ)/fkqpf:ahdmarhr(rjdnf)wp4rhr(djfltrhkdeodmldkclashfo)(tjgus)"));
            testCase.add(new TestCaseVO("ㅗ디ㅣㅐ", "ㅗㄷㅣㅣㅐ")); //hello
            testCase.add(new TestCaseVO("째깅", "ㅈㅈㅐㄱㅣㅇ/world")); //World
            testCase.add(new TestCaseVO("ㅣㅐ햣ㄷ초", "ㅣㅐㅎㅑㅅㄷㅊㅗ")); //logitech
            testCase.add(new TestCaseVO("퍗므ㅑㅜ", "ㅍㅑㅅㅁㅡㅑㅜ")); //vitamin
            testCase.add(new TestCaseVO("랱", "ㄹㅐㅌ/fox")); //fox
            testCase.add(new TestCaseVO("ㅅ딛퍄냐ㅐㅜ", "ㅅㄷㅣㄷㅍㅑㄴㅑㅐㅜ")); //television
            testCase.add(new TestCaseVO("최", "ㅊㅗㅣ/chl")); //television
            testCase.add(new TestCaseVO("ㅅㄴㅅㄷ", "ㅅㄴㅅㄷ")); //소녀시대 초성검색
            testCase.add(new TestCaseVO("##%@#$%()*&^%$#@!", "##%@#$%()*&^%$#@!"));
            testCase.add(new TestCaseVO("2000 최신가요", "2000ㅊㅗㅣㅅㅣㄴㄱㅏㅇㅛ/2000chltlsrkdy"));
            testCase.add(new TestCaseVO("15&", "15&"));
            testCase.add(new TestCaseVO("최신가요 1990", "ㅊㅗㅣㅅㅣㄴㄱㅏㅇㅛ1990/chltlsrkdy1990"));
            testCase.add(new TestCaseVO("최신가요&", "ㅊㅗㅣㅅㅣㄴㄱㅏㅇㅛ&/chltlsrkdy&"));

        } else if(options.isMistype() == false && options.isChosung() == true) {

            testCase.add(new TestCaseVO("최일규", "ㅊㅗㅣㅇㅣㄹㄱㅠ/ㅊㅇㄱ"));
            testCase.add(new TestCaseVO("소녀시대", "ㅅㅗㄴㅕㅅㅣㄷㅐ/ㅅㄴㅅㄷ"));
            testCase.add(new TestCaseVO("Hello", "hello"));
            testCase.add(new TestCaseVO("Hello~", "hello~"));
            testCase.add(new TestCaseVO("무조건 해피엔딩", "ㅁㅜㅈㅗㄱㅓㄴㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ/ㅁㅈㄱㅎㅍㅇㄷ"));
            testCase.add(new TestCaseVO("소원을 말해봐 genie mixversion##!", "ㅅㅗㅇㅜㅓㄴㅇㅡㄹㅁㅏㄹㅎㅐㅂㅗㅏgeniemixversion##!/ㅅㅇㅇㅁㅎㅂ"));
            testCase.add(new TestCaseVO("hush hush; hush hush", "hushhush;hushhush"));
            testCase.add(new TestCaseVO("라벨:모음곡(거울)제4곡(어릿광대의 아침노래)(서현)", "ㄹㅏㅂㅔㄹ:ㅁㅗㅇㅡㅁㄱㅗㄱ(ㄱㅓㅇㅜㄹ)ㅈㅔ4ㄱㅗㄱ(ㅇㅓㄹㅣㅅㄱㅗㅏㅇㄷㅐㅇㅡㅣㅇㅏㅊㅣㅁㄴㅗㄹㅐ)(ㅅㅓㅎㅕㄴ)/ㄹㅂㅁㅇㄱㄱㅇㅈㄱㅇㄹㄱㄷㅇㅇㅊㄴㄹㅅㅎ"));
            testCase.add(new TestCaseVO("ㅗ디ㅣㅐ", "ㅗㄷㅣㅣㅐ")); //hello
            testCase.add(new TestCaseVO("째깅", "ㅈㅈㅐㄱㅣㅇ/ㅈㅈㄱ")); //World
            testCase.add(new TestCaseVO("ㅣㅐ햣ㄷ초", "ㅣㅐㅎㅑㅅㄷㅊㅗ")); //logitech
            testCase.add(new TestCaseVO("퍗므ㅑㅜ", "ㅍㅑㅅㅁㅡㅑㅜ")); //vitamin
            testCase.add(new TestCaseVO("랱", "ㄹㅐㅌ")); //fox
            testCase.add(new TestCaseVO("ㅅ딛퍄냐ㅐㅜ", "ㅅㄷㅣㄷㅍㅑㄴㅑㅐㅜ")); //television
            testCase.add(new TestCaseVO("최", "ㅊㅗㅣ"));
            testCase.add(new TestCaseVO("ㅅㄴㅅㄷ", "ㅅㄴㅅㄷ")); //소녀시대 초성검색
            testCase.add(new TestCaseVO("##%@#$%()*&^%$#@!", "##%@#$%()*&^%$#@!"));
            testCase.add(new TestCaseVO("2000 최신가요", "2000ㅊㅗㅣㅅㅣㄴㄱㅏㅇㅛ"));
            testCase.add(new TestCaseVO("15&", "15&"));
            testCase.add(new TestCaseVO("최신가요 1990", "ㅊㅗㅣㅅㅣㄴㄱㅏㅇㅛ1990/ㅊㅅㄱㅇ"));
            testCase.add(new TestCaseVO("최신가요&", "ㅊㅗㅣㅅㅣㄴㄱㅏㅇㅛ&/ㅊㅅㄱㅇ"));

        } else if(options.isMistype() == false && options.isChosung() == false) {

            testCase.add(new TestCaseVO("최일규", "ㅊㅗㅣㅇㅣㄹㄱㅠ"));
            testCase.add(new TestCaseVO("소녀시대", "ㅅㅗㄴㅕㅅㅣㄷㅐ"));
            testCase.add(new TestCaseVO("Hello", "hello"));
            testCase.add(new TestCaseVO("Hello~", "hello~"));
            testCase.add(new TestCaseVO("무조건 해피엔딩", "ㅁㅜㅈㅗㄱㅓㄴㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ"));
            testCase.add(new TestCaseVO("소원을 말해봐 genie mixversion##!", "ㅅㅗㅇㅜㅓㄴㅇㅡㄹㅁㅏㄹㅎㅐㅂㅗㅏgeniemixversion##!"));
            testCase.add(new TestCaseVO("hush hush; hush hush", "hushhush;hushhush"));
            testCase.add(new TestCaseVO("라벨:모음곡(거울)제4곡(어릿광대의 아침노래)(서현)", "ㄹㅏㅂㅔㄹ:ㅁㅗㅇㅡㅁㄱㅗㄱ(ㄱㅓㅇㅜㄹ)ㅈㅔ4ㄱㅗㄱ(ㅇㅓㄹㅣㅅㄱㅗㅏㅇㄷㅐㅇㅡㅣㅇㅏㅊㅣㅁㄴㅗㄹㅐ)(ㅅㅓㅎㅕㄴ)"));
            testCase.add(new TestCaseVO("ㅗ디ㅣㅐ", "ㅗㄷㅣㅣㅐ")); //hello
            testCase.add(new TestCaseVO("째깅", "ㅈㅈㅐㄱㅣㅇ")); //World
            testCase.add(new TestCaseVO("ㅣㅐ햣ㄷ초", "ㅣㅐㅎㅑㅅㄷㅊㅗ")); //logitech
            testCase.add(new TestCaseVO("퍗므ㅑㅜ", "ㅍㅑㅅㅁㅡㅑㅜ")); //vitamin
            testCase.add(new TestCaseVO("랱", "ㄹㅐㅌ")); //fox
            testCase.add(new TestCaseVO("ㅅ딛퍄냐ㅐㅜ", "ㅅㄷㅣㄷㅍㅑㄴㅑㅐㅜ")); //television
            testCase.add(new TestCaseVO("최", "ㅊㅗㅣ"));
            testCase.add(new TestCaseVO("ㅅㄴㅅㄷ", "ㅅㄴㅅㄷ")); //소녀시대 초성검색
            testCase.add(new TestCaseVO("##%@#$%()*&^%$#@!", "##%@#$%()*&^%$#@!"));
            testCase.add(new TestCaseVO("2000 최신가요", "2000ㅊㅗㅣㅅㅣㄴㄱㅏㅇㅛ"));
            testCase.add(new TestCaseVO("15&", "15&"));
            testCase.add(new TestCaseVO("최신가요 1990", "ㅊㅗㅣㅅㅣㄴㄱㅏㅇㅛ1990"));
            testCase.add(new TestCaseVO("최신가요&", "ㅊㅗㅣㅅㅣㄴㄱㅏㅇㅛ&"));

        }

        for(TestCaseVO vo : testCase) {

            StringReader reader = new StringReader(vo.getOrigin());

            Tokenizer tokenizer = new JasoTokenizer( options);
            tokenizer.setReader(reader);
            CharTermAttribute termAtt = tokenizer.addAttribute(CharTermAttribute.class);

            tokenizer.reset();

            StringBuffer sb = new StringBuffer();

            while (tokenizer.incrementToken()) {
                if (sb.length() > 0) sb.append('/');
                sb.append(termAtt.toString());
            }

            TestCase.assertEquals(vo.getCompare(), sb.toString());
            tokenizer.close();

            System.out.println(String.format("%s => %s", vo.getOrigin(), sb.toString()));
        }

        long end = System.currentTimeMillis();
        System.out.println("실행 시간 : " + (end - start) / 1000.0);
	}	
}