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
 *
 * @author 최일규
 * @since 2016-02-11
 */
public class JasoTokenizerTest extends TestCase {

    public void testTokenizer() throws IOException {

        long start = System.currentTimeMillis();
        TokenizerOptions options = TokenizerOptions.create("testTokenizer");

        //한영오타에 대한 토큰 추출여부 (hello -> ㅗㄷㅣㅣㅐ, 최일규 -> chldlfrb)
        options.setMistype(false);

        //초성검색을 위한 토큰 추출여부 (최일규 -> ㅊㅇㄱ)
        options.setChosung(false);

        List<TestCaseVO> testCase = new ArrayList<TestCaseVO>();

        if (options.isMistype() == true && options.isChosung() == true) {

            testCase.add(new TestCaseVO("최일규", "ㅊㅗㅣㅇㅣㄹㄱㅠ/chldlfrb/ㅊㅇㄱ"));
            testCase.add(new TestCaseVO("소녀시대", "ㅅㅗㄴㅕㅅㅣㄷㅐ/thsutleo/ㅅㄴㅅㄷ"));
            testCase.add(new TestCaseVO("Hello", "hello/ㅗㄷㅣㅣㅐ"));
            testCase.add(new TestCaseVO("Hello~", "hello~/ㅗㄷㅣㅣㅐ~"));
            testCase.add(new TestCaseVO("무조건 해피엔딩", "ㅁㅜㅈㅗㄱㅓㄴㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ/ㅁㅜㅈㅗㄱㅓㄴ/ㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ/anwhrjsgovldpseld/anwhrjs/govldpseld/ㅁㅈㄱㅎㅍㅇㄷ"));
            testCase.add(new TestCaseVO("아디다스 운동화", "ㅇㅏㄷㅣㄷㅏㅅㅡㅇㅜㄴㄷㅗㅇㅎㅗㅏ/ㅇㅏㄷㅣㄷㅏㅅㅡ/ㅇㅜㄴㄷㅗㅇㅎㅗㅏ/dkelektmdnsehdghk/dkelektm/dnsehdghk/ㅇㄷㄷㅅㅇㄷㅎ"));
            testCase.add(new TestCaseVO("투데이특가", "ㅌㅜㄷㅔㅇㅣㅌㅡㄱㄱㅏ/xnepdlxmrrk/ㅌㄷㅇㅌㄱ"));

        } else if (options.isMistype() == true && options.isChosung() == false) {

            testCase.add(new TestCaseVO("최일규", "ㅊㅗㅣㅇㅣㄹㄱㅠ/chldlfrb"));
            testCase.add(new TestCaseVO("소녀시대", "ㅅㅗㄴㅕㅅㅣㄷㅐ/thsutleo"));
            testCase.add(new TestCaseVO("Hello", "hello/ㅗㄷㅣㅣㅐ"));
            testCase.add(new TestCaseVO("Hello~", "hello~/ㅗㄷㅣㅣㅐ~"));
            testCase.add(new TestCaseVO("무조건 해피엔딩", "ㅁㅜㅈㅗㄱㅓㄴㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ/ㅁㅜㅈㅗㄱㅓㄴ/ㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ/anwhrjsgovldpseld/anwhrjs/govldpseld"));
            testCase.add(new TestCaseVO("아디다스 운동화", "ㅇㅏㄷㅣㄷㅏㅅㅡㅇㅜㄴㄷㅗㅇㅎㅗㅏ/ㅇㅏㄷㅣㄷㅏㅅㅡ/ㅇㅜㄴㄷㅗㅇㅎㅗㅏ/dkelektmdnsehdghk/dkelektm/dnsehdghk"));
            testCase.add(new TestCaseVO("투데이특가", "ㅌㅜㄷㅔㅇㅣㅌㅡㄱㄱㅏ/xnepdlxmrrk"));

        } else if (options.isMistype() == false && options.isChosung() == true) {

            testCase.add(new TestCaseVO("최일규", "ㅊㅗㅣㅇㅣㄹㄱㅠ/ㅊㅇㄱ"));
            testCase.add(new TestCaseVO("소녀시대", "ㅅㅗㄴㅕㅅㅣㄷㅐ/ㅅㄴㅅㄷ"));
            testCase.add(new TestCaseVO("Hello", "hello"));
            testCase.add(new TestCaseVO("Hello~", "hello~"));
            testCase.add(new TestCaseVO("무조건 해피엔딩", "ㅁㅜㅈㅗㄱㅓㄴㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ/ㅁㅜㅈㅗㄱㅓㄴ/ㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ/ㅁㅈㄱㅎㅍㅇㄷ"));
            testCase.add(new TestCaseVO("아디다스 운동화", "ㅇㅏㄷㅣㄷㅏㅅㅡㅇㅜㄴㄷㅗㅇㅎㅗㅏ/ㅇㅏㄷㅣㄷㅏㅅㅡ/ㅇㅜㄴㄷㅗㅇㅎㅗㅏ/ㅇㄷㄷㅅㅇㄷㅎ"));
            testCase.add(new TestCaseVO("투데이특가", "ㅌㅜㄷㅔㅇㅣㅌㅡㄱㄱㅏ/ㅌㄷㅇㅌㄱ"));

        } else if (options.isMistype() == false && options.isChosung() == false) {

            testCase.add(new TestCaseVO("최일규", "ㅊㅗㅣㅇㅣㄹㄱㅠ"));
            testCase.add(new TestCaseVO("소녀시대", "ㅅㅗㄴㅕㅅㅣㄷㅐ"));
            testCase.add(new TestCaseVO("Hello", "hello"));
            testCase.add(new TestCaseVO("Hello~", "hello~"));
            testCase.add(new TestCaseVO("무조건 해피엔딩", "ㅁㅜㅈㅗㄱㅓㄴㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ/ㅁㅜㅈㅗㄱㅓㄴ/ㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ"));
            testCase.add(new TestCaseVO("아디다스 운동화", "ㅇㅏㄷㅣㄷㅏㅅㅡㅇㅜㄴㄷㅗㅇㅎㅗㅏ/ㅇㅏㄷㅣㄷㅏㅅㅡ/ㅇㅜㄴㄷㅗㅇㅎㅗㅏ"));
            testCase.add(new TestCaseVO("투데이특가", "ㅌㅜㄷㅔㅇㅣㅌㅡㄱㄱㅏ"));
        }

        for (TestCaseVO vo : testCase) {

            StringReader reader = new StringReader(vo.getOrigin());

            Tokenizer tokenizer = new JasoTokenizer(options);
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