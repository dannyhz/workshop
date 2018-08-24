package cn.evun.sweet.common.util.encode;

public class UnicodeUtil {

	/**
	 * 将字符串转成unicode(自动补全四位)
	 * 
	 * @param str
	 *            待转字符串
	 * @return unicode字符串
	 */
	public static String encodeUnicode(String str) {
		str = (str == null ? "" : str);
		String tmp;
		StringBuffer sb = new StringBuffer(1000);
		char c;
		int i, j;
		sb.setLength(0);
		for (i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			sb.append("\\u");
			j = (c >>> 8); // 取出高8位
			tmp = Integer.toHexString(j);
			if (tmp.length() == 1)
				sb.append("0");
			sb.append(tmp);
			j = (c & 0xFF); // 取出低8位
			tmp = Integer.toHexString(j);
			if (tmp.length() == 1)
				sb.append("0");
			sb.append(tmp);

		}
		return (new String(sb));
	}
	
	/**
	 * 不自动补全
	 * @param s
	 * @return
	 */
	public static String encodeUnicode1(String s) {
		int in;
        String st = "";
        for(int i=0;i<s.length();i++){
            in = s.codePointAt(i);
            st = st+"\\u"+Integer.toHexString(in).toUpperCase();
        }
        return st;
	}

	/**
	 * 将unicode 字符串
	 * 
	 * @param str
	 *            待转字符串
	 * @return 普通字符串
	 */
	public static String decodeUnicode(String utfString) {
		StringBuilder sb = new StringBuilder();  
	    int i = -1;  
	    int pos = 0;  
	      
	    while((i=utfString.indexOf("\\u", pos)) != -1){  
	        sb.append(utfString.substring(pos, i));  
	        if(i+5 < utfString.length()){  
	            pos = i+6;  
	            sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));  
	        }  
	    }  
	      
	    return sb.toString(); 
	}

	public static void main(String[] args) {

		System.out.println(encodeUnicode1("<p>sas</p>"));

		System.out.println(encodeUnicode("<p>sas</p>"));
		System.out.println(decodeUnicode("\\u003Cp\\u003Esas\\u003C/p\\u003E"));

	}
}
