package org.elasticsearch.analysis;
import java.io.IOException;
import java.io.StringReader;

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
		
		String[] origin	= {
                  "최일규"
                , "소녀시대"
                , "Hello"
                , "Hello~"
                , "무조건 해피엔딩"
                , "소원을 말해봐 genie mixversion##!"
                , "hush hush; hush hush"
                , "라벨:모음곡(거울)제4곡(어릿광대의 아침노래)(서현)"
                , "ㅗ디ㅣㅐ" //hello
                , "째깅" //world
                , "ㅣㅐ햣ㄷ초" //logitech
                , "퍗므ㅑㅜ" //vitamin
                , "랱" //fox
                , "ㄾ"
                , "ㅅ딛퍄냐ㅐㅜ" //television
                , "ㄲㅊㅊㅁㄱ" //Rccar

        };

        String[] compare = {
                  "ㅊㅗㅣㅇㅣㄹㄱㅠ/chldlfrb/ㅊㅇㄱ"
                , "ㅅㅗㄴㅕㅅㅣㄷㅐ/thsutleo/ㅅㄴㅅㄷ"
                , "Hello/ㅗㄷㅣㅣㅐ"
                , "Hello/ㅗㄷㅣㅣㅐ"
                , "ㅁㅜㅈㅗㄱㅓㄴ/ㅎㅐㅍㅣㅇㅔㄴㄷㅣㅇ/anwhrjs/govldpseld/ㅁㅈㄱ/ㅎㅍㅇㄷ"
                , "ㅅㅗㅇㅜㅓㄴㅇㅡㄹ/ㅁㅏㄹㅎㅐㅂㅗㅏ/thdnjsdmf/akfgoqhk/genie/mixversion/ㅎㄷㅜㅑㄷ/ㅡㅑㅌㅍㄷㄱㄴㅑㅐㅜ/ㅅㅇㅇ/ㅁㅎㅂ"
                , "hush/hush/hush/hush/ㅗㅕㄴㅗ/ㅗㅕㄴㅗ/ㅗㅕㄴㅗ/ㅗㅕㄴㅗ"
                , "ㄹㅏㅂㅔㄹㅁㅗㅇㅡㅁㄱㅗㄱㄱㅓㅇㅜㄹㅈㅔㄱㅗㄱㅇㅓㄹㅣㅅㄱㅗㅏㅇㄷㅐㅇㅡㅣ/ㅇㅏㅊㅣㅁㄴㅗㄹㅐㅅㅓㅎㅕㄴ/fkqpfahdmarhrrjdnfwprhrdjfltrhkdeodml/dkclashfotjgus/ㄹㅂㅁㅇㄱㄱㅇㅈㄱㅇㄹㄱㄷㅇ/ㅇㅊㄴㄹㅅㅎ"
                , "ㅗㄷㅣㅣㅐ"
                , "ㅈㅈㅐㄱㅣㅇ/World/ㅈㅈㄱ"
                , "ㅣㅐㅎㅑㅅㄷㅊㅗ"
                , "ㅍㅑㅅㅁㅡㅑㅜ"
                , "ㄹㅐㅌ/fox/ㄹ"
                , "ㄹㅌ"
                , "ㅅㄷㅣㄷㅍㅑㄴㅑㅐㅜ"
                , "ㄱㄱㅊㅊㅁㄱ"
        };

        for(int i=0; i< origin.length; i++) {

            StringReader reader = new StringReader(origin[i]);

            JasoTokenizer tokenizer = new JasoTokenizer(reader, options);
            CharTermAttribute termAtt = tokenizer.addAttribute(CharTermAttribute.class);

            tokenizer.reset();

            StringBuffer sb = new StringBuffer();

            while (tokenizer.incrementToken()) {
                if (sb.length() > 0) sb.append('/');
                sb.append(termAtt.toString());
            }

            TestCase.assertEquals(compare[i], sb.toString());
            tokenizer.close();

            System.out.println(String.format("%s => %s", origin[i], sb.toString()));
        }
	
	}	
}
