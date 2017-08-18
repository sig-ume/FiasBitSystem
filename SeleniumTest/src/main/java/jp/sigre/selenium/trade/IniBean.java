package jp.sigre.selenium.trade;

import java.util.ArrayList;
import java.util.List;

public class IniBean {
	private String lS_FilePath;
	private String iD_FilePath;

	private List<String[]> methodSet = new ArrayList<>();

	private String tradeVisible;

	public String getTradeVisible() {
		return tradeVisible;
	}

	public void setTradeVisible(String tradeVisible) {
		this.tradeVisible = tradeVisible;
	}

	public String getlS_FilePath() {
		return lS_FilePath;
	}

	public void setlS_FilePath(String lS_FilePath) {
		this.lS_FilePath = lS_FilePath;
	}

	public String getiD_FilePath() {
		return iD_FilePath;
	}

	public void setiD_FilePath(String iD_FilePath) {
		this.iD_FilePath = iD_FilePath;
	}

	public List<String[]> getMethodSet() {
		return methodSet;
	}

	public void setMethodSet(List<String[]> methodSet) {
		this.methodSet = methodSet;
	}

	public void addMethodSet(String[] method) {
		this.methodSet.add(method);
	}

	@Override
	public String toString() {
		String result = "IniBean [lS_FilePath=" + lS_FilePath + ", iD_FilePath=" + iD_FilePath + ", methodSet=\n";
		for (String[] strs : methodSet) {
			result += strs[0] + ", " + strs[1] + ", " + strs[2] + "\n";
		}
		result += "TradeVisible=" + tradeVisible;
		return result;
	}

}
