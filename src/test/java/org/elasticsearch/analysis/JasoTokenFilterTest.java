package org.elasticsearch.analysis;


import static com.carrotsearch.randomizedtesting.RandomizedTest.$;
import static com.carrotsearch.randomizedtesting.RandomizedTest.$$;
import static org.apache.lucene.tests.analysis.BaseTokenStreamTestCase.assertTokenStreamContents;


import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.elasticsearch.test.ESTestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.carrotsearch.randomizedtesting.annotations.ParametersFactory;

@RunWith(com.carrotsearch.randomizedtesting.RandomizedRunner.class)
public class JasoTokenFilterTest extends ESTestCase {

    private final String source;
    private final String jasoExpectedResult;

    private static Tokenizer tokenizer;

    public JasoTokenFilterTest(String source, String jasoExpectedResult) {
        this.source = source;
        this.jasoExpectedResult = jasoExpectedResult;
    }

    @BeforeClass
    public static void init() {
        tokenizer = new KeywordTokenizer();
    }

    @Test
    public void testTokenFilter() throws IOException {

        tokenizer.setReader(new StringReader(source));
        TokenFilter splitter = new JasoTokenFilter(tokenizer, new SettingOptions(""));
        assertTokenStreamContents(splitter, new String[]{jasoExpectedResult});
    }

    @ParametersFactory
    public static Iterable<Object[]> initParameters() {
        return Arrays.asList($$(
            $(
                "신혼여행1+1",
                "ㅅㅣㄴㅎㅗㄴㅇㅕㅎㅐㅇ1+1"
            )
        ));
    }
}
