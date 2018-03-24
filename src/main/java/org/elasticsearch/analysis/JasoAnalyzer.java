package org.elasticsearch.analysis;

import org.apache.lucene.analysis.Analyzer;

/**
 * JasoAnalyzer
 *
 * @author 최일규
 * @since 2018-03-21
 */
public class JasoAnalyzer extends Analyzer {
    public JasoAnalyzer() {
    }

    @Override
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        return new Analyzer.TokenStreamComponents(new JasoTokenizer(TokenizerOptions.create("jaso_analyzer")));
    }
}