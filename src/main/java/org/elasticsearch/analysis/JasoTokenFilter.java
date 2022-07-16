package org.elasticsearch.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;


public class JasoTokenFilter extends TokenFilter {

    private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAttr = addAttribute(OffsetAttribute.class);
    private final SettingOptions options;
    private final JasoDecomposer decomposer;

    public JasoTokenFilter(TokenStream input, SettingOptions options) {
        super(input);
        this.options = options;
        this.decomposer = new JasoDecomposer();

    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            CharSequenceOffset terms = decomposer.split(termAttr.buffer(), termAttr.length(), options);

            if (terms.getCharSequence().length() > 0) {
                termAttr.setEmpty().append(terms.getCharSequence());
                offsetAttr.setOffset(0, terms.getOffset());
                return true;
            }
        }
        return false;
    }
}
