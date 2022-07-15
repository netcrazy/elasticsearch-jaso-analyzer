package org.elasticsearch.index.analysis;

import org.elasticsearch.analysis.JasoAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

/**
 * JasoAnalyzerProvider
 *
 * @author 최일규
 * @since 2018-03-21
 */
public class JasoAnalyzerProvider extends AbstractIndexAnalyzerProvider<JasoAnalyzer> {
    private final JasoAnalyzer analyzer;

    public JasoAnalyzerProvider(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(name, settings);
        analyzer = new JasoAnalyzer();
    }

    @Override
    public JasoAnalyzer get() {
        return analyzer;
    }
}