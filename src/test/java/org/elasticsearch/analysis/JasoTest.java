package org.elasticsearch.analysis;

import org.elasticsearch.analysis.JasoDecomposer;

import junit.framework.TestCase;

/**
 * 자동완성 기능 유닛테스트
 * @author 	최일규
 * @since	2016-02-03
 *
 */
public class JasoTest extends TestCase {

	public void testJasoDecomposer() {
		System.out.println("-----------------------------");
		JasoDecomposer aa=new JasoDecomposer();
		String test=aa.runJasoDecompose("원할",0, false);
		System.out.println(test);
	}
	
	public void testJasoDecomposer2() {
		System.out.println("-----------------------------");
		JasoDecomposer2 aa=new JasoDecomposer2();
		String test=aa.runJasoDecompose("원할");
		System.out.println(test);
	}	
}