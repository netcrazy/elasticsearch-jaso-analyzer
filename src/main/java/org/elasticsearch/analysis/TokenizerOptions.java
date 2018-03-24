package org.elasticsearch.analysis;

/**
 * 토크나이저 옵션
 *
 * @author 최일규
 * @since 2016-02-12
 */
public class TokenizerOptions {

    //한영오타에 대한 토큰 추출여부 (hello -> ㅗㄷㅣㅣㅐ, 최일규 -> chldlfrb)
    public final static boolean MISTYPE = false;

    //초성검색을 위한 토큰 추출여부 (최일규 -> ㅊㅇㄱ)
    public final static boolean CHOSUNG = false;

    private boolean mistype = MISTYPE;
    private boolean chosung = CHOSUNG;

    private String name = null;

    public static TokenizerOptions create(String name) {
        return new TokenizerOptions(name);
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

    public void setMistype(boolean mistype) {
        this.mistype = mistype;
    }

    public boolean isChosung() {
        return chosung;
    }

    public void setChosung(boolean chosung) {
        this.chosung = chosung;
    }
}