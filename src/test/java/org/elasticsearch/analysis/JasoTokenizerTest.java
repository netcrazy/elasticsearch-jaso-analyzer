package org.elasticsearch.analysis;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import junit.framework.TestCase;
import org.elasticsearch.common.config;

/**
 * 토크나이저 유닛테스트
 * @author 최일규
 * @since 2016-02-11
 *
 */
public class JasoTokenizerTest extends TestCase {
	
	public void testTokenizer() throws IOException {

        TokenizerOptions options = TokenizerOptions.create("test");
        //한영오타에 대한 토큰 추출여부 (hello -> ㅗㄷㅣㅣㅐ, 최일규 -> chldlfrb)
        options.setMistype(true);

        //초성검색을 위한 토큰 추출여부 (최일규 -> ㅊㅇㄱ)
        options.setChosung(true);

        List<TestCaseVO> testCase = new ArrayList<TestCaseVO>();
        testCase.add(new TestCaseVO("최일규", "ㅊㅗㅣㅇㅣㄹㄱㅠ/chldlfrb/ㅊㅇㄱ"));
        testCase.add(new TestCaseVO("소녀시대", "ㅅㅗㄴㅕㅅㅣㄷㅐ/thsutleo/ㅅㄴㅅㄷ"));
        testCase.add(new TestCaseVO("Hello", "Hello/ㅗㄷㅣㅣㅐ"));
        testCase.add(new TestCaseVO("Hello~", "Hello/ㅗㄷㅣㅣㅐ"));
        testCase.add(new TestCaseVO("무조건 해피엔딩", "ㅁㅜㅈㅗㄱㅓㄴ/ㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ/anwhrjs/govldpseld/ㅁㅈㄱ/ㅎㅍㅇㄷ"));
        testCase.add(new TestCaseVO("소원을 말해봐 genie mixversion##!", "ㅅㅗㅇㅜㅓㄴㅇㅡㄹ/ㅁㅏㄹㅎㅐㅂㅗㅏ/thdnjsdmf/akfgoqhk/genie/mixversion/ㅎㄷㅜㅑㄷ/ㅡㅑㅌㅍㄷㄱㄴㅑㅐㅜ/ㅅㅇㅇ/ㅁㅎㅂ"));
        testCase.add(new TestCaseVO("hush hush; hush hush", "hush/hush/hush/hush/ㅗㅕㄴㅗ/ㅗㅕㄴㅗ/ㅗㅕㄴㅗ/ㅗㅕㄴㅗ"));
        testCase.add(new TestCaseVO("라벨:모음곡(거울)제4곡(어릿광대의 아침노래)(서현)", "ㄹㅏㅂㅔㄹㅁㅗㅇㅡㅁㄱㅗㄱㄱㅓㅇㅜㄹㅈㅔㄱㅗㄱㅇㅓㄹㅣㅅㄱㅗㅏㅇㄷㅐㅇㅡㅣ/ㅇㅏㅊㅣㅁㄴㅗㄹㅐㅅㅓㅎㅕㄴ/fkqpfahdmarhrrjdnfwprhrdjfltrhkdeodml/dkclashfotjgus/ㄹㅂㅁㅇㄱㄱㅇㅈㄱㅇㄹㄱㄷㅇ/ㅇㅊㄴㄹㅅㅎ"));
        testCase.add(new TestCaseVO("ㅗ디ㅣㅐ", "ㅗㄷㅣㅣㅐ")); //hello
        testCase.add(new TestCaseVO("째깅", "ㅈㅈㅐㄱㅣㅇ/World/ㅈㅈㄱ")); //World
        testCase.add(new TestCaseVO("ㅣㅐ햣ㄷ초", "ㅣㅐㅎㅑㅅㄷㅊㅗ")); //logitech
        testCase.add(new TestCaseVO("퍗므ㅑㅜ", "ㅍㅑㅅㅁㅡㅑㅜ")); //vitamin
        testCase.add(new TestCaseVO("랱", "ㄹㅐㅌ/fox/ㄹ")); //fox
        testCase.add(new TestCaseVO("ㅅ딛퍄냐ㅐㅜ", "ㅅㄷㅣㄷㅍㅑㄴㅑㅐㅜ")); //television

        for(TestCaseVO vo : testCase) {

            StringReader reader = new StringReader(vo.getOrigin());

            JasoTokenizer tokenizer = new JasoTokenizer(reader, options);
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
	
	}	
}
