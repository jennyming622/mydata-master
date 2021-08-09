package test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class testByteArrayToString {

	public static void main(String[] args) throws UnsupportedEncodingException, CharacterCodingException {
		String pid = "H120983053";
		byte[] pidArray= pid.getBytes("UTF-8");
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(pidArray.length);
		bb.put(pidArray).flip();
		CharBuffer cb = cs.decode(bb);
		char[]  pidCharArray = cb.array();
		System.out.println(new StringBuilder().append(pidCharArray).toString());
		/**
		 * test
		 */
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();
		ByteBuffer srcBuffer = ByteBuffer.wrap(pidArray);
		CharBuffer resBuffer = decoder.decode(srcBuffer);
		StringBuilder yourStringBuilder = new StringBuilder(resBuffer);
		System.out.println(yourStringBuilder);
		System.out.println(yourStringBuilder.length());
	}

}
