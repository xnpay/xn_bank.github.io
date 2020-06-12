package com.olain.java8.默认方法zcatpaydemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

/**
 * demo
 * 示例代码, 实际使用自行修改更换
 * 扩展依赖包可自行更换修改:
 * import com.alibaba.fastjson.JSON;
 * import com.alibaba.fastjson.JSONObject;
 * import org.springframework.util.StringUtils
 *
 * @author lcl
 * @date 2020/2/16 13:41
 */
public class JavaDemo {

    //联系客服获取商户, 密钥与域名
    private static final String MERCHANT_NO = "";
    private static final String KEY = "";

    public static void main(String[] args) {
        //查询
        //queryOrder();
        //创建, 页面跳转形式
        //createOrder();
        //回调
        //callback();
    }


    /**
     * 创建
     */
    public static void createOrder() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderNo", "123456789004");
        jsonObject.put("merchantNo", MERCHANT_NO);
        jsonObject.put("amount", "100");
        jsonObject.put("payMode", "ebank");
        jsonObject.put("notifyUrl", "https://www.baidu.com/");
        jsonObject.put("returnUrl", "https://www.baidu.com/");
        jsonObject.put("ts", Instant.now().getEpochSecond());
        String signReduce = generateSignReduce(jsonObject);
        String sign = encodeMD5(signReduce + "&key=" + KEY);
        jsonObject.put("sign", sign);

        //链接跳转形式, 域名请联系客服
        //host + /#/create?amount=100&merchantNo=20200113185052721173545318&notifyUrl=baidu.com&orderNo=1582342812127&payMode=ebank&returnUrl=baidu.com&ts=1582342812&sign=126a8a129faff7d71a1e2208f5c938fb
        String s = "网关地址+/pass-sign/#/create?" + signReduce + "&sign=" + sign;
        System.out.println(s);
    }

    /**
     * 查询订单
     */
    public static void queryOrder() {
        String host = "queryHost/open/order/query";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderNo", "1581829465");
        jsonObject.put("merchantNo", MERCHANT_NO);
        jsonObject.put("ts", Instant.now().getEpochSecond());

        String signReduce = generateSignReduce(jsonObject);
        String sign = encodeMD5(signReduce + "&key=" + KEY);
        jsonObject.put("sign", sign);

        String s = jsonPost(jsonObject.toJSONString(), host);
        System.out.println(s);
    }

    /**
     * 回调
     */
    public static void callback() {
        String result = "{\"amount\":900,\"bankName\":\"广发银行\",\"bankNo\":\"62146202210026980\",\"merchantNo\":\"123456789\",\"name\":\"王五\",\"orderNo\":\"o-10086140\",\"orderStatus\":50,\"payMode\":\"ebank\",\"payNo\":\"20200213173023981153464943\",\"payStatus\":30,\"payTime\":1581586702,\"sign\":\"3aff08ebde950423acbc267e363588ec\",\"ts\":1581585888}";
        JSONObject jsonObject = JSON.parseObject(result);
        //拼接
        String reduce = generateSignReduce(jsonObject);
        String md5 = encodeMD5(reduce + "&key=" + KEY);
        System.out.println(jsonObject.getString("sign").equalsIgnoreCase(md5));
    }


    public static String generateSignReduce(Object o) {
        JSONObject parse = JSONObject.parseObject(JSONObject.toJSONString(o));
        String result = parse.keySet()
                .stream().filter(key -> !"sign".equalsIgnoreCase(key))
                .filter(key -> !StringUtils.isEmpty(parse.getString(key)))
                .sorted().map(key -> {
                    try {
                        return key + "=" + URLEncoder.encode(parse.getString(key), StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .reduce("", (a, b) -> a + "&" + b).substring(1);
        return result;
    }


    /**
     * 请求
     * 示例代码, 实际使用自行修改更换
     *
     * @param postData
     * @param postUrl
     * @return
     */
    public static String jsonPost(String postData, String postUrl) {
        try {
            URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Length", "" + postData.length());
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(postData);
            out.flush();
            out.close();

            BufferedReader br;
            //获取响应状态
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            }
            //获取响应内容体
            String line, result = "";

            while ((line = br.readLine()) != null) {
                result += line + "\n";
            }
            br.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * MD5
     * 示例代码, 实际使用自行修改更换
     *
     * @param s
     * @return
     */
    public static String encodeMD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
