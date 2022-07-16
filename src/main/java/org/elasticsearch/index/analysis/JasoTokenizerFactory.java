package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.analysis.JasoTokenizer;
import org.elasticsearch.analysis.SettingOptions;
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

    private final SettingOptions options;

    public JasoTokenizerFactory(IndexSettings indexSettings,
                                Environment environment,
                                String name,
                                Settings settings) {

        super(indexSettings, settings, name);

        this.options = SettingOptions.create(name);
        this.options.setMistype(settings.getAsBoolean("mistype", SettingOptions.MISTYPE));
        this.options.setChosung(settings.getAsBoolean("chosung", SettingOptions.CHOSUNG));
    }

    @Override
    public Tokenizer create() {
        return new JasoTokenizer(this.options);
    }
}