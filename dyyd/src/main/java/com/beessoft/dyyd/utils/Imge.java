package com.beessoft.dyyd.utils;

public class Imge {
	
	private String id;

	private String imgName;
	private String imgUrl;
	private String url;
	private String userid;
	public Imge() {
		super();
	}

	public Imge(String id, String imgName, String imgUrl,String url) {
		super();
		this.id = id;
		this.imgName = imgName;
		this.imgUrl = imgUrl;
		this.url = url;
	}

	@Override
	public String toString() {
		return "Imge [id=" + id + ", imgName=" + imgName + ", imgUrl=" + imgUrl + ",url = " + url 
				+ "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImgName() {
		return imgName;
	}
	public String geturl() {
		return url;
		}
	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public void seturl(String url) {
		this.url = url;
	}
	public void setUserid(String userid){
		this.userid = userid; 
	}
	public String getUserid(){
		return userid;
	}
}