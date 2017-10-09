/**
 *
 */
package jp.sigre.fbs.controller;

/**
 * @author sigre
 *
 */
public class SepaCombineBean {

	private int code;
	//0:combine,1;separate
	private int checksepa_combine;
	private int ajustRate;
	/**
	 * @return code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @param code セットする code
	 */
	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * @return checksepa_combine
	 */
	public int getChecksepa_combine() {
		return checksepa_combine;
	}
	/**
	 * @param checksepa_combine セットする checksepa_combine
	 */
	public void setChecksepa_combine(int checksepa_combine) {
		this.checksepa_combine = checksepa_combine;
	}
	/**
	 * @return ajustRate
	 */
	public int getAjustRate() {
		return ajustRate;
	}
	/**
	 * @param ajustRate セットする ajustRate
	 */
	public void setAjustRate(int ajustRate) {
		this.ajustRate = ajustRate;
	}
	/* (非 Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ajustRate;
		result = prime * result + checksepa_combine;
		result = prime * result + code;
		return result;
	}
	/* (非 Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SepaCombineBean other = (SepaCombineBean) obj;
		if (ajustRate != other.ajustRate)
			return false;
		if (checksepa_combine != other.checksepa_combine)
			return false;
		if (code != other.code)
			return false;
		return true;
	}
	/* (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SepaCombineBean [code=" + code + ", checksepa_combine=" + checksepa_combine + ", ajustRate=" + ajustRate
				+ "]";
	}


}
