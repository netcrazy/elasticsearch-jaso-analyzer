package org.elasticsearch.analysis;

public class SettingOptions {

    //한영오타에 대한 토큰 추출여부 (hello -> ㅗㄷㅣㅣㅐ, 최일규 -> chldlfrb)
    public final static boolean MISTYPE = false;

    //초성검색을 위한 토큰 추출여부 (최일규 -> ㅊㅇㄱ)
    public final static boolean CHOSUNG = false;

    private boolean mistype = MISTYPE;
    private boolean chosung = CHOSUNG;

    private String name = null;

    public static SettingOptions create(String name) {
        return new SettingOptions(name);
    }

    protected SettingOptions(String name) {
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