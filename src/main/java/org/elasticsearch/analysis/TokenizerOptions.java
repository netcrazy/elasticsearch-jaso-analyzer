package org.elasticsearch.analysis;

/**
 * 토크나이저 옵션
 * @author 최일규
 * @since  2016 -02 -12
 *
 */
public class TokenizerOptions {

    public final static boolean MISTYPE = false;
    public final static boolean CHOSUNG = false;

    private boolean mistype = MISTYPE;
    private boolean chosung = CHOSUNG;

    private String name = null;

    public static TokenizerOptions create(String name) {
        return new TokenizerOptions( name);
    }

    private TokenizerOptions(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isMistype() {
        return mistype;
    }
    public void setMistype( boolean mistype) {
        this. mistype = mistype;
    }
    public boolean isChosung() {
        return chosung;
    }
    public void setChosung( boolean chosung) {
        this. chosung = chosung;
    }
}