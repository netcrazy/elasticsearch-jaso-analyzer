package org.elasticsearch.analysis;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.analysis.util.CharacterUtils.CharacterBuffer;

/**
 * Base 자소 토크나이저 구현
 * @author	최일규
 * @since	2013-09-15
 */
public abstract class BaseTokenizer extends Tokenizer {

	protected BaseTokenizer(Reader input) {
		super(input);
		//decomposeMode=mode;
		//typoMode=typo;
		charUtils = CharacterUtils.getInstance();		
	}

	private static JasoDecomposer2 decomposer;
	private int decomposeMode = 0;
	private boolean typoMode = false;

	private int offset = 0, bufferIndex = 0, dataLen = 0, finalOffset = 0;
	private static final int MAX_WORD_LEN 	= 2048;
	private static final int IO_BUFFER_SIZE = 4096;

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);;
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

	private final CharacterUtils charUtils;
	private final CharacterBuffer ioBuffer = CharacterUtils.newCharacterBuffer(IO_BUFFER_SIZE);



	protected boolean isTokenChar(int c) {
		throw new UnsupportedOperationException("Subclasses of CharTokenizer must implement isTokenChar(int)");
	}

	protected int normalize(int c) {
		return c;
	}

	/**
	 * lucene 4.2x의 경우 데이터가 있으면 자소분리 후 true가 떨어지나, 여기서는 false로 떨어져 부득이하게 ioBuffer사이즈 상태로 조건변경 (CharacterUtils.fill)
	 * @author 	최일규
	 * @since	2014-07-11
	 */
	@Override
	public final boolean incrementToken() throws IOException {
		clearAttributes();

		int length = 0;
		int start = -1; // this variable is always initialized
		char[] buffer = termAtt.buffer();
		while (true) {
			if (bufferIndex >= dataLen) {

				offset += dataLen;
				boolean isDecompose = charUtils.fill(ioBuffer, jasoDecompose(input, decomposeMode, typoMode));

				//버퍼사이즈가 있으면 분석한다. (return false일때까지... 재귀호출)
				if(ioBuffer.getLength() == 0) {
					dataLen = 0; // so next offset += dataLen won't decrement offset
					if (length > 0) {
						break;
					} else {
						finalOffset = correctOffset(offset);
						return false;
					}
				}
				dataLen = ioBuffer.getLength();
				bufferIndex = 0;
			}
			// use CharacterUtils here to support < 3.1 UTF-16 code unit behavior if the char based methods are gone
			final int c = charUtils.codePointAt(ioBuffer.getBuffer(), bufferIndex, dataLen);
			bufferIndex += Character.charCount(c);

			// if it's a token char
			if (isTokenChar(c)) {

				// start of token
				if (length == 0) {
					assert start == -1;
					start = offset + bufferIndex - 1;

					// check if a supplementary could run out of bounds
				} else if (length >= buffer.length-1) {

					// make sure a supplementary fits in the buffer
					buffer = termAtt.resizeBuffer(2+length);
				}

				// buffer it, normalized
				length += Character.toChars(normalize(c), buffer, length);
				if (length >= MAX_WORD_LEN) {
					break;
				}
			} else if (length > 0) {
				// return 'em
				break;
			}
		}

		termAtt.setLength(length);
		assert start != -1;
		offsetAtt.setOffset(correctOffset(start), finalOffset = correctOffset(start+length));
		return true;
	}

	@Override
	public final void end() {
		// set final offset
		offsetAtt.setOffset(finalOffset, finalOffset);
	}

	/**
	 * Reader -> String -> 자소변환 -> String -> Reader
	 * @author 	ikchoi
	 * @param 	in
	 * @param 	mode
	 * @param 	typo
	 * @return
	 */
	public static Reader jasoDecompose(Reader in,int mode,boolean typo)
	{
		Writer writer = new StringWriter();
		decomposer=new JasoDecomposer2();
		char[] buffer = new char[2048];
		String temp="";

		try {
			int n;
			while((n = in.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
			temp=writer.toString();
			temp=decomposer.runJasoDecompose(temp);
			// System.out.println(temp);
			StringReader myStringReader = new StringReader(temp);
			in=myStringReader;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {}

		return in;
	}

	/**
	 * reset
	 * @param input
	 * @throws IOException
	 */
	public void reset(Reader input) throws IOException {
		super.reset();
		bufferIndex = 0;
		offset = 0;
		dataLen = 0;
		finalOffset = 0;
		ioBuffer.reset(); // make sure to reset the IO buffer!!
	}
}