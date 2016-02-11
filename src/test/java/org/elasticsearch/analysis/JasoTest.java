package org.elasticsearch.analysis;

import junit.framework.TestCase;

/**
 * 자동완성 기능 유닛테스트
 * @author 	최일규
 * @since	2016-02-03
 *
 */
public class JasoTest extends TestCase {
	
	public void testJasoDecomposer() {
		JasoDecomposer aa=new JasoDecomposer();
		String test=aa.runJasoDecompose("최일규 Hello");
		System.out.println(test);
	}	
}