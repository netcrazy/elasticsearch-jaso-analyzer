package org.elasticsearch.analysis;

public class CharSequenceOffset {
    private final CharSequence charSequence;

    private final int offset;

    public CharSequenceOffset(CharSequence charSequence, int offset) {
        this.charSequence = charSequence;
        this.offset = offset;
    }

    public CharSequence getCharSequence() {
        return charSequence;
    }

    public int getOffset() {
        return offset;
    }
}
