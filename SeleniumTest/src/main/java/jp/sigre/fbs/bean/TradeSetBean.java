package jp.sigre.fbs.bean;

import java.util.List;

import jp.sigre.fbs.selenium.trade.TradeDataBean;

public class TradeSetBean {

	private String code;
	private int volume;
	private List<TradeDataBean> beanList;
	private String isMini;

	/**
	 * @return isMini
	 */
	public String getIsMini() {
		return isMini;
	}

	/**
	 * @param isMini セットする isMini
	 */
	public void setIsMini(String isMini) {
		this.isMini = isMini;
	}

	public TradeSetBean(String code, int volume, List<TradeDataBean> beanList, String isMini) {
		super();
		this.code = code;
		this.volume = volume;
		this.beanList = beanList;
		this.isMini = isMini;
	}

	public TradeSetBean() {}

	/**
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code セットする code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return volume
	 */
	public int getVolume() {
		return volume;
	}

	/**
	 * @param volume セットする volume
	 */
	public void setVolume(int volume) {
		this.volume = volume;
	}

	/**
	 * @return beanList
	 */
	public List<TradeDataBean> getBeanList() {
		return beanList;
	}

	/**
	 * @param beanList セットする beanList
	 */
	public void setBeanList(List<TradeDataBean> beanList) {
		this.beanList = beanList;
	}

}
