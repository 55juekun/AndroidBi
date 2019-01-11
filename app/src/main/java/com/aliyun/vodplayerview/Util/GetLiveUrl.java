package com.aliyun.vodplayerview.Util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetLiveUrl {
    public String getUrl(String originUrl){
        String key = "hO4l2rXLhG";
        long exp=System.currentTimeMillis()/1000+480*1000;//System.currentTimeMillis();
        Pattern p=Pattern.compile("^(rtmp://)?([^/?]+)(/[^?]*)?(\\?.*)?$");
        Matcher m = p.matcher(originUrl);
        m.find(0);
        String args = m.group(3);
        String rand = "0";
        String uid = "0";
        String sstring=String.format("%s-%s-%s-%s-%s",args, exp, rand, uid, key);
        String hashvalue = MD5(sstring);
        String auth_key = String.format("%s-%s-%s-%s",exp, rand, uid, hashvalue);
        return String.format("%s?auth_key=%s" ,originUrl, auth_key);
    }

    public String MD5(String originStr){
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] cipherData = md5.digest(originStr.getBytes());
            StringBuilder builder = new StringBuilder();
            for(byte cipher : cipherData) {
                String toHexStr = Integer.toHexString(cipher & 0xff);
                builder.append(toHexStr.length() == 1 ? "0" + toHexStr : toHexStr);
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
