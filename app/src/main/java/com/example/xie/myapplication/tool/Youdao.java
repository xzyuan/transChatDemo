package com.example.xie.myapplication.tool;

import com.squareup.okhttp.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;


////import org.apache.http.NameValuePair;


public class Youdao {

    public static void main(String[] args) {
        System.out.println("1");
    }

    public String translateFrom(String s) throws Exception {
        String appKey ="08ae2ee280177b3b";
        String query = s;
        String salt = String.valueOf(System.currentTimeMillis());
        String from = "zh-CHS";
        String to = "EN";
        String sign = md5(appKey + query + salt+ "JcPZte6kWwrSJDzhIO5Atw3Kek2arBdm");
        Map params = new HashMap();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("sign", sign);
        params.put("salt", salt);
        params.put("appKey", appKey);
        return eJson(requestForHttp("http://openapi.youdao.com/api", params));

    }

    public String translateTo(String s) throws Exception {
        String appKey ="08ae2ee280177b3b";
        String query = s;
        String salt = String.valueOf(System.currentTimeMillis());
        String from = "EN";
        String to = "zh-CHS";
        String sign = md5(appKey + query + salt+ "JcPZte6kWwrSJDzhIO5Atw3Kek2arBdm");
        Map params = new HashMap();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("sign", sign);
        params.put("salt", salt);
        params.put("appKey", appKey);
        return eJson(requestForHttp("http://openapi.youdao.com/api", params));

    }

    public static String requestForHttp(String url, Map requestParams) throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        String result = null;
//        System.out.println(new JSONObject(requestParams).toString());
        List params = new ArrayList();
        Iterator it = requestParams.entrySet().iterator();
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        while (it.hasNext()) {
            Entry en = (Entry) it.next();
            String key = (String) en.getKey();
            String value = (String) en.getValue();
            if (value != null) {
                formEncodingBuilder.add(key, value);
            }
        }
        RequestBody formBody =formEncodingBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            result = response.body().string();
        }
        else {
            throw new IOException("Unexpected code " + response);
        }
        Thread.sleep(1000);

        return result;
    }

    /**
     * 生成32位MD5摘要
     * @param string
     * @return
     */
    public static String md5(String string) {
        if(string == null){
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};

        try{
            byte[] btInput = string.getBytes("utf-8");
            /** 获得MD5摘要算法的 MessageDigest 对象 */
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            /** 使用指定的字节更新摘要 */
            mdInst.update(btInput);
            /** 获得密文 */
            byte[] md = mdInst.digest();
            /** 把密文转换成十六进制的字符串形式 */
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }catch(NoSuchAlgorithmException | UnsupportedEncodingException e){
            return null;
        }
    }

    /**
     * 根据api地址和参数生成请求URL
     * @param url
     * @param params
     * @return
     */
    public static String getUrlWithQueryString(String url, Map params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (Object key : params.keySet()) {
            String value = (String) params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }
    /**
     * 进行URL编码
     * @param input
     * @return
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }

    private String eJson(String json) {
        String s = null;
        String p[];
        try {
            JSONObject jsonObject = new JSONObject(json);
            s = jsonObject.getString("translation");
        } catch (Exception e) {
            e.printStackTrace();
        }
        p = s.split("\"");
        return p[1];
    }
}