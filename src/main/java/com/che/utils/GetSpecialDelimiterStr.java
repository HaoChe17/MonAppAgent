package com.che.utils;
/**
 * 把字符串中的连续的空格或tab或tab与空格的组合换成指定的字符
 * @author chehao
 * @version 2017年9月28日 下午10:10:33
 */
public class GetSpecialDelimiterStr {
	public String getSpecialStr(String targetStr,String delimiter){
		char[] ch_array=targetStr.toCharArray();
		String newStr="";
		String tmpStr="";
		//提取出非空格与tab的字符，并与指定的分隔符组成新的字符串
		for(char ar:ch_array){
			if(ar==" ".toCharArray()[0] || ar=="	".toCharArray()[0]){
				if(!tmpStr.equals("")){
					newStr=newStr+tmpStr+delimiter;
					tmpStr="";
				}
			}else{
				tmpStr=tmpStr+ar;
			}
		}
		newStr=newStr+tmpStr;
		return newStr;
	}
}
