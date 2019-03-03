package jp.sigre.fbs.selenium.trade;

import java.util.ArrayList;
import java.util.List;

public class IniBean {
	private String lS_FilePath = "";
	private String iD_FilePath = "";

	private List<String[]> methodSet = new ArrayList<>();

	private String tradeVisible;

	private String sellUnusedMethod;

	private List<Integer> skipList = new ArrayList<>();

	private boolean isAdminUser = false;

	public String getTradeVisible() {
		return tradeVisible;
	}

	public void setTradeVisible(String tradeVisible) {
		this.tradeVisible = tradeVisible;
	}

	public String getLS_FilePath() {
		return lS_FilePath;
	}

	public void setLS_FilePath(String lS_FilePath) {
		this.lS_FilePath = lS_FilePath;
	}

	public String getID_FilePath() {
		return iD_FilePath;
	}

	public void setID_FilePath(String iD_FilePath) {
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

	public String getSellUnusedMethod() {
		return sellUnusedMethod;
	}

	public void setSellUnusedMethod(String sellUnusedMethod) {
		this.sellUnusedMethod = sellUnusedMethod;
	}

	public List<Integer> getSkipList() {
		return skipList;
	}

	public void addSkipNumber(int skipNumber) {
		skipList.add(skipNumber);
	}

	@Override
	public String toString() {
		String result = "IniBean [lS_FilePath=" + lS_FilePath + ", iD_FilePath=" + iD_FilePath + ", methodSet=\n";
		for (String[] methods : methodSet) {
			result += methods[0] + ", " + methods[1] + ", " + methods[2] + "\n";
		}
		result += "TradeVisible=" + tradeVisible + "\n";
		result += "SkipNumber=";
		for (int skipNumber : skipList) {
			result += skipNumber + ", ";
		}


		return result;
	}

	public boolean getIsAdminUser() {
		return isAdminUser;
	}

	public void setIsAdminUser(boolean isAdminUser) {
		this.isAdminUser = isAdminUser;
	}

}
