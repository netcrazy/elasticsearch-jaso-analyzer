package org.elasticsearch.plugin;

import org.elasticsearch.analysis.JasoTokenizerFactory;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.Plugin;

/**
 * Elasticsearch 2.3.x Plugin
 * @author 	최일규
 * @since	2016-02-03
 *
 */
public class JasoAnalysisPlugin extends Plugin  {
	@Override
	public String name() {
		return "jaso-analyzer";
	}

	@Override
	public String description() {
		return "Jaso analysis support";
	}


	/**
	 * 모듈 등록
	 * Analyzer 셋팅시 사용될 토크나이저명 "jaso_tokenizer"
	 * @param module
	 */
	public void onModule(AnalysisModule module) {
		module.addTokenizer("jaso_tokenizer", JasoTokenizerFactory.class);
	}


}