package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.analysis.JasoTokenFilter;
import org.elasticsearch.analysis.SettingOptions;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;


public class JasoTockenFilterFactory extends AbstractTokenFilterFactory {

    private final SettingOptions options;
    public JasoTockenFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);

        this.options = SettingOptions.create(name);
        this.options.setMistype(settings.getAsBoolean("mistype", SettingOptions.MISTYPE));
        this.options.setChosung(settings.getAsBoolean("chosung", SettingOptions.CHOSUNG));
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new JasoTokenFilter(tokenStream, options);
    }
}
