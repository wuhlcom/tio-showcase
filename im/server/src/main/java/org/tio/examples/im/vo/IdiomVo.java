package org.tio.examples.im.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 * 2017年6月8日 上午9:57:45
 */
public class IdiomVo {
	private static Logger log = LoggerFactory.getLogger(IdiomVo.class);

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	private String idiom = null;
	private String first = null;//首字母
	private String pinyin = null;

	private String paraphrase = null;//释义

	/**
	 *
	 * @author tanyaowu
	 */
	public IdiomVo() {
	}

	/**
	 * @param idiom
	 * @param first
	 * @param pinyin
	 * @param paraphrase
	 * @author tanyaowu
	 */
	public IdiomVo(String idiom, String first, String pinyin, String paraphrase) {
		super();
		this.idiom = idiom;
		this.first = first;
		this.pinyin = pinyin;
		this.paraphrase = paraphrase;
	}

	/**
	 * @return the first
	 */
	public String getFirst() {
		return first;
	}

	/**
	 * @return the idiom
	 */
	public String getIdiom() {
		return idiom;
	}

	/**
	 * @return the paraphrase
	 */
	public String getParaphrase() {
		return paraphrase;
	}

	/**
	 * @return the pinyin
	 */
	public String getPinyin() {
		return pinyin;
	}

	/**
	 * @param first the first to set
	 */
	public void setFirst(String first) {
		this.first = first;
	}

	/**
	 * @param idiom the idiom to set
	 */
	public void setIdiom(String idiom) {
		this.idiom = idiom;
	}

	/**
	 * @param paraphrase the paraphrase to set
	 */
	public void setParaphrase(String paraphrase) {
		this.paraphrase = paraphrase;
	}

	/**
	 * @param pinyin the pinyin to set
	 */
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
}
