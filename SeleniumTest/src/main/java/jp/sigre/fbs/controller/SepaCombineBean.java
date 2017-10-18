/**
 *
 */
package jp.sigre.fbs.controller;

/**
 * @author sigre
 *
 */
public class SepaCombineBean {

	private String code;
	//0:combine,1;separate
	private String checksepa_combine;
	private String ajustRate;

	/**
	 * @return code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @return checksepa_combine
	 */
	public String getChecksepa_combine() {
		return checksepa_combine;
	}
	/**
	 * @return ajustRate
	 */
	public String getAjustRate() {
		return ajustRate;
	}
	/**
	 * @param code セットする code
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @param checksepa_combine セットする checksepa_combine
	 */
	public void setChecksepa_combine(String checksepa_combine) {
		this.checksepa_combine = checksepa_combine;
	}
	/**
	 * @param ajustRate セットする ajustRate
	 */
	public void setAjustRate(String ajustRate) {
		this.ajustRate = ajustRate;
	};

	/* (非 Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ajustRate == null) ? 0 : ajustRate.hashCode());
		result = prime * result + ((checksepa_combine == null) ? 0 : checksepa_combine.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		if (ajustRate == null) {
			if (other.ajustRate != null)
				return false;
		} else if (!ajustRate.equals(other.ajustRate))
			return false;
		if (checksepa_combine == null) {
			if (other.checksepa_combine != null)
				return false;
		} else if (!checksepa_combine.equals(other.checksepa_combine))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
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
