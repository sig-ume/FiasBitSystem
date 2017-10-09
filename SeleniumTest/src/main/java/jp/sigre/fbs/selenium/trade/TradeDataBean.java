package jp.sigre.fbs.selenium.trade;

/**
 * @author sigre
 *
 */
public class TradeDataBean {

	private String code;
	private String dayTime;
	private String type;
	private String entryMethod;
	private String exitMethod;
	private String MINI_CHECK_flg;
	private String realEntryVolume;
	private String entry_money;
	private String correctedEntryVolume;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDayTime() {
		return dayTime;
	}
	public void setDayTime(String dayTime) {
		this.dayTime = dayTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getEntryMethod() {
		return entryMethod;
	}
	public void setEntryMethod(String entryMethod) {
		this.entryMethod = entryMethod;
	}
	public String getExitMethod() {
		return exitMethod;
	}
	public void setExitMethod(String exitMethod) {
		this.exitMethod = exitMethod;
	}
	public String getMINI_CHECK_flg() {
		return MINI_CHECK_flg;
	}
	public void setMINI_CHECK_flg(String mINI_CHECK_flg) {
		MINI_CHECK_flg = mINI_CHECK_flg;
	}
	public String getRealEntryVolume() {
		return realEntryVolume;
	}
	public void setRealEntryVolume(String realEntryVolume) {
		this.realEntryVolume = realEntryVolume;
	}
	public String getEntry_money() {
		return entry_money;
	}
	public void setEntry_money(String entry_money) {
		this.entry_money = entry_money;
	}
	public String getCorrectedEntryVolume() {
		return correctedEntryVolume;
	}
	public void setCorrectedEntryVolume(String correctedEntryVolume) {
		this.correctedEntryVolume = correctedEntryVolume;
	}

	public void minusRealEntryVolume(int volume) {
		this.realEntryVolume = String.valueOf(Integer.parseInt(this.realEntryVolume) - volume);
	}

	@Override
	public String toString() {
		return "TradeDataBean [code=" + code + ", dayTime=" + dayTime + ", type=" + type + ", entryMethod="
				+ entryMethod + ", exitMethod=" + exitMethod + ", MINI_CHECK_flg=" + MINI_CHECK_flg
				+ ", realEntryVolume=" + realEntryVolume + ", entry_money=" + entry_money + ", correctedEntryVolume="
				+ correctedEntryVolume + "]";
	}

	public String toCSV() {
		return code + "," + dayTime + "," + type + "," + entryMethod + "," + exitMethod + "," + MINI_CHECK_flg
				+ "," + realEntryVolume + "," + entry_money;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((entryMethod == null) ? 0 : entryMethod.hashCode());
		result = prime * result + ((exitMethod == null) ? 0 : exitMethod.hashCode());
		return result;
	}
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		TradeDataBean other = (TradeDataBean) obj;
//		if (code == null) {
//			if (other.code != null)
//				return false;
//		} else if (!code.equals(other.code))
//			return false;
//		if (entryMethod == null) {
//			if (other.entryMethod != null)
//				return false;
//		} else if (!entryMethod.equals(other.entryMethod))
//			return false;
//		if (exitMethod == null) {
//			if (other.exitMethod != null)
//				return false;
//		} else if (!exitMethod.equals(other.exitMethod))
//			return false;
//		return true;
//	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TradeDataBean other = (TradeDataBean) obj;
		if (MINI_CHECK_flg == null) {
			if (other.MINI_CHECK_flg != null)
				return false;
		} else if (!MINI_CHECK_flg.equals(other.MINI_CHECK_flg))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (correctedEntryVolume == null) {
			if (other.correctedEntryVolume != null)
				return false;
		} else if (!correctedEntryVolume.equals(other.correctedEntryVolume))
			return false;
		if (dayTime == null) {
			if (other.dayTime != null)
				return false;
		} else if (!dayTime.equals(other.dayTime))
			return false;
		if (entryMethod == null) {
			if (other.entryMethod != null)
				return false;
		} else if (!entryMethod.equals(other.entryMethod))
			return false;
		if (entry_money == null) {
			if (other.entry_money != null)
				return false;
		} else if (!entry_money.equals(other.entry_money))
			return false;
		if (exitMethod == null) {
			if (other.exitMethod != null)
				return false;
		} else if (!exitMethod.equals(other.exitMethod))
			return false;
		if (realEntryVolume == null) {
			if (other.realEntryVolume != null)
				return false;
		} else if (!realEntryVolume.equals(other.realEntryVolume))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public boolean equalsCodeMethods(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TradeDataBean other = (TradeDataBean) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (entryMethod == null) {
			if (other.entryMethod != null)
				return false;
		} else if (!entryMethod.equals(other.entryMethod))
			return false;
		if (exitMethod == null) {
			if (other.exitMethod != null)
				return false;
		} else if (!exitMethod.equals(other.exitMethod))
			return false;
		return true;
	}

	public TradeDataBean clone() {
		TradeDataBean bean = new TradeDataBean();

		bean.setCode(this.code);
		bean.setDayTime(this.dayTime);
		bean.setType(this.type);
		bean.setEntryMethod(this.entryMethod);
		bean.setExitMethod(this.exitMethod);
		bean.setMINI_CHECK_flg(this.MINI_CHECK_flg);
		bean.setRealEntryVolume(this.realEntryVolume);
		bean.setEntry_money(this.entry_money);
		bean.setCorrectedEntryVolume(this.correctedEntryVolume);

		return bean;
	}

}
