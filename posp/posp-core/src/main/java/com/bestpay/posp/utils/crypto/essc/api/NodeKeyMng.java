package com.bestpay.posp.utils.crypto.essc.api;

public class NodeKeyMng {
	String gunionIDOfEsscAPI;
	public NodeKeyMng(String gunionIDOfEsscAPI) {
		this.gunionIDOfEsscAPI = gunionIDOfEsscAPI;
	}
	
	public String UnionGetNodeKeyName(String node, String type) throws Exception{
		String[] desKeyType = {"zpk","zak","zmk","tmk","tpk","tak","pvk","cvk","zek","wwk","bdk","edk",""};
		if(node.length() == 0 || type.length() == 0|| type.length() > 3)
			throw new Exception("in NodeKeyMng:: UnionGetNodeKeyName parameter error!");
		String typeLower = type.toLowerCase();
		for(int i =0; i< desKeyType.length;i++){
			if(desKeyType[i].equals(typeLower)){
				return gunionIDOfEsscAPI+"."+node+"."+typeLower;
			}
		}
		throw new Exception("in UnionGetNodeKeyName:: can find type in desKeyType!");
	}
	
	public String UnionGetNodePKName(String node) throws Exception{
		if(node.length()==0)
			throw new Exception("in UnionGetNodePKName:: parameter error!");
		return gunionIDOfEsscAPI+"."+node+".pk";
	}
}
