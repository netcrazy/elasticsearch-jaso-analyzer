package org.elasticsearch.analysis;

import org.elasticsearch.common.config;

/**
 * 자소 토크나이저 구현
 *
 * @author 최일규
 * @since 2018-03-21
 */
public final class JasoTokenizer extends BaseTokenizer {

    /**
     * 자소 토크나이저 생성자
     *
     * @param options 토크나이저 옵션
     */
    public JasoTokenizer(TokenizerOptions options) {
        super(options);
    }

    /**
     * Collects only characters which do not satisfy
     * {@link Character#isWhitespace(int)}.
     */
    @Override
    protected boolean isTokenChar(int c) {
        return !isSplit(c);
    }

    /**
     * White Space로 토큰분해
     *
     * @param c
     * @return
     */
    protected boolean isSplit(int c) {
        if ((char) c == config.WHITESPACE_CHAR) {
            return true;
        } else {
            return false;
        }
    }
}