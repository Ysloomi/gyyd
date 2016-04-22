package com.beessoft.dyyd.bean;

public class Advise {
	private String adviseType;
	private String adviseName;
	private String adviseText;
	/**
	 * @return the adviseType
	 */
	public String getAdviseType() {
		return adviseType;
	}

	/**
	 * @param adviseType
	 *            the adviseType to set
	 */
	public void setAdviseType(String adviseType) {
		this.adviseType = adviseType;
	}

	@Override
	public String toString() {
		return "Advise [adviseType=" + adviseType + "]";
	}
}
