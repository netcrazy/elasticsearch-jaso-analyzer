package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.analysis.JasoTokenizer;
import org.elasticsearch.analysis.TokenizerOptions;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.env.Environment;

/**
 * JasoTokenizerFactory
 *
 * @author 최일규
 * @since 2018-03-21
 */
public class JasoTokenizerFactory extends AbstractTokenizerFactory {

    private TokenizerOptions options;

    public JasoTokenizerFactory(IndexSettings indexSettings,
                                Environment environment,
                                String name,
                                Settings settings) {

        super(indexSettings, settings, name);

        this.options = TokenizerOptions.create(name);
        this.options.setMistype(settings.getAsBoolean("mistype", TokenizerOptions.MISTYPE));
        this.options.setChosung(settings.getAsBoolean("chosung", TokenizerOptions.CHOSUNG));
    }

    @Override
    public Tokenizer create() {
        return new JasoTokenizer(this.options);
    }
}