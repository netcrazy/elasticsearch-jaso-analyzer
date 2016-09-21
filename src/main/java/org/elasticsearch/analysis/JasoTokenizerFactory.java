package org.elasticsearch.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

/**
 * 자소 토크나이저 팩토리 구현
 * @author	최일규
 * @since	2016-02-03
 */
public class JasoTokenizerFactory extends AbstractTokenizerFactory {

    private TokenizerOptions options;

    @Inject
    public JasoTokenizerFactory(Index index, IndexSettingsService indexSettingsService, @Assisted String name, @Assisted
            Settings settings) {


        super(index, indexSettingsService.getSettings(), name, settings);


        this.options = TokenizerOptions.create(name);
        this.options.setMistype(settings.getAsBoolean("mistype", TokenizerOptions.MISTYPE));
        this.options.setChosung(settings.getAsBoolean("chosung", TokenizerOptions.CHOSUNG));
    }

    @Override
    public Tokenizer create() {
         return new JasoTokenizer( this.options);
    }
}
