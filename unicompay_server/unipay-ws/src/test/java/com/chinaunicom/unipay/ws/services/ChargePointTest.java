package com.chinaunicom.unipay.ws.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.chinaunicom.unipay.ws.commons.ErrorCode;
import com.chinaunicom.unipay.ws.controllers.*;
import com.chinaunicom.unipay.ws.persistence.ChargePoint;
import com.chinaunicom.unipay.ws.persistence.Order;
import com.chinaunicom.unipay.ws.plugins.ioc.IocInterceptor;
import com.chinaunicom.unipay.ws.utils.MD5;
import com.chinaunicom.unipay.ws.utils.RandomUtil;
import com.chinaunicom.unipay.ws.utils.Tools;
import com.chinaunicom.unipay.ws.utils.VerifyUtil;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.jfinal.aop.Before;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.Assert.assertTrue;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * User: Frank
 * Date: 2014/11/28
 * Time: 15:29
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/com/chinaunicom/unipay/ws/services/impl/ctx.xml")
public class ChargePointTest {
    private XmlMapper mapper = new XmlMapper();
    @Resource IWeiXinService wxs;


    //@BeforeClass
    public static void init() {

        ResourceBundle resdb = ResourceBundle.getBundle("db");

        C3p0Plugin cp = new C3p0Plugin(resdb.getString("oracle.url"),
                resdb.getString("oracle.username"),
                resdb.getString("oracle.password"),
                resdb.getString("oracle.driver"));
        ActiveRecordPlugin rp = new ActiveRecordPlugin(cp);
        rp.setShowSql(true);
        rp.setDialect(new OracleDialect());
        rp.setContainerFactory(new CaseInsensitiveContainerFactory());

        rp.addMapping("appc_charging_point", "pointindex", ChargePoint.class);
        rp.addMapping("mcnt_content", "cntindex", ChargePoint.Product.class);
        cp.start();
        rp.start();
    }

    @Test
    public void testGetByConsumecode() throws Exception {
//        ChargePoint point = ChargePoint.dao.getByConsumecode("9011282030120130131090207746000001");
//        assertTrue(point.getPointid().equals("9011282030120130131090207746000001"));
        IWeiXinService.IWeiXin wx = new IWeiXinService.IWeiXin();
        wx.setTotal_fee(100);
        wx.setProduct_id("234234");
        wx.setBody("飞毛腿");
        wx.setOut_trade_no(Tools.getUUID());

        IWeiXinService.IWeiXinResponse wxr = wxs.getQrcode(wx);


    }

    @Test
    public void testGetProduct() throws Exception {

//        ChargePoint point = ChargePoint.dao.getByConsumecode("9011282030120130131090207746000001");
//        assertTrue(point.getProduct().getCntname().equals("孵化器应用s"));
       String xml = "<xml>\n" +
               "   <return_code><![CDATA[SUCCESS]]></return_code>\n" +
               "   <return_msg><![CDATA[OK]]></return_msg>\n" +
               "   <appid><![CDATA[wx2421b1c4370ec43b]]></appid>\n" +
               "   <mch_id><![CDATA[10000100]]></mch_id>\n" +
               "   <nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>\n" +
               "   <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>\n" +
               "   <result_code><![CDATA[SUCCESS]]></result_code>\n" +
               "   <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>\n" +
               "   <trade_type><![CDATA[JSAPI]]></trade_type>\n" +
               "</xml>";
        IWeiXinService.IWeiXinResponse wxr = mapper.readValue(xml, IWeiXinService.IWeiXinResponse.class);
        assertTrue(wxr.getAppid().equals("wx2421b1c4370ec43b"));
    }
    @Test
    public void testTest() throws Exception{
        String xml = "<alipay>\n" +
                "<is_success>T</is_success>\n" +
                "<request>\n" +
                "<param name=\"sign\">02f3da7b48a31a98757acb62cbb24724</param>\n" +
                "<param name=\"timestamp\">2012-12-24 11:20:06</param>\n" +
                "<param name=\"_input_charset\">UTF-8</param>\n" +
                "<param name=\"biz_type\">10</param>\n" +
                "<param name=\"biz_data\">\n" +
                "{\"need_address\":\"F\",\"trade_type\":\"1\",\"return_url\":\"http://10.253.88.17:3000/api/kabao.do?method=merchantorder\",\"notify_url\":\"http://api.test.alipay.net/atinterface/receive_notify.htm\",\"goods_info\":{\"id\":\"10001\",\"name\":\"商品名称\",\"price\":\"100.0\",\"inventory\":\"\",\"sku_title\":\"抬头\",\"sku\":[{\"sku_id\":\"123456\",\"sku_name\":\"红色\",\"sku_price\":\"1.5\",\"sku_inventory\":\"4\"}],\"expiry_date\":\"2012-10-12 21:21:12|2012-22-19 22:24:12\",\"desc\":\"商品描述（最多100字）\"},\"ext_info\":{\"single_limit\":\"2\",\"user_limit\":\"3\",\"logo_name\":\"logo\"},\"memo\":\"memo\"}</param>\n" +
                "<param name=\"sign_type\">MD5</param>\n" +
                "<param name=\"service\">alipay.mobile.qrcode.manage</param>\n" +
                "<param name=\"method\">add</param>\n" +
                "<param name=\"partner\">2088102104932950</param>\n" +
                "</request>\n" +
                "<response>\n" +
                "<alipay>\n" +
                "<qrcode>https://qr.alipay.com/9446219319446735</qrcode>\n" +
                "<qrcode_img_url>https://qr.alipay.com/paipai/show.htm? code=9446219319446735</qrcode_img_url>\n" +
                "<result_code>SUCCESS</result_code>\n" +
                "</alipay>" +
                "</response>\n" +
                "<sign>8342649bb0b3b818c9bed5952503b3df</sign>\n" +
                "<sign_type>MD5</sign_type>\n" +
                "</alipay>";

//        AliResponse ar = mapper.readValue(xml, AliResponse.class);
//        Map ali = new HashMap();
//        AliPay aliPay = new AliPay();
//       for(AliPay ap : ar.getRequest().getParam()){
//           System.out.println(ap.getName() + "::::::::::" + ap.getValue());
//       }
       // System.out.print( MD5.MD5Encode("9022623518820141218145955682000b1c6040a380e0d12uuid{\"consumer_seq\":\"a737fffa10ed46e7b156e27e9c13d21c\",\"goods_name\":\"200元宝\",\"goods_num\":\"1\",\"total_point\":1000,\"user_id\":\"2872493499619402251\"}2c4d6de947ffa2d9899ab4575bca7dd0"));
       IPointService.PointResponse pr = new IPointService.PointResponse();
        JSONObject pointConsume = JSONObject.parseObject("{\"sys_error\":{\"err_cd\":\"1997\",\"err_msg\":\"服务 27 is not authorized\"}}");

        JSONObject consume = pointConsume.getJSONObject("point_consume");
        String consumeid = null;
        if(consume != null) {
            consumeid = consume.getString("consume_id");
        }else {
            consume = pointConsume.getJSONObject("sys_error");
        }
        String errcd = consume.getString("err_cd");
        String errmsg = consume.getString("err_msg");
        if( StringUtils.isEmpty(consumeid)){
            pr.setCode(errcd);
            pr.setMsg(errmsg);
        }else {
            pr.setConsumeid(Integer.parseInt(consumeid));
        }
        VerifyUtil.logprint("返回数据：", pr);
        try {
            throw new PointException.Data(ErrorCode.getCode(ErrorCode.POINT_CODE_,pr.getCode()),"213412451235235");
        } catch (PointException.Data e) {
            String a = e.getUuid();
            int b = e.getResult();
        }



    }

    public static class AliPay{

        public String name;
        @JacksonXmlText
        public String value;
        @JacksonXmlProperty(isAttribute = true)
        //接口名称 alipay.mobile.qrcode.manage
        private String service;
        @JacksonXmlProperty(isAttribute = true)
        //合作者身份ID
        private String partner;
        @JacksonXmlProperty(isAttribute = true)
        //参数编码字符集
        private String _input_charset;
        @JacksonXmlProperty(isAttribute = true)
        //sign_type MD5
        private String sign_type;
        @JacksonXmlProperty(isAttribute = true)
        //签名
        private String sign;
        @JacksonXmlProperty(isAttribute = true)
        //接口调用时间
        private String timestamp;
        @JacksonXmlProperty(isAttribute = true)
        //动作 add modify stop restart
        private String method;
        @JacksonXmlProperty(isAttribute = true)
        //二维码 “https://qr.alipay.com/”开头，加上一串字符串。
        private String qrcode;
        @JacksonXmlProperty(isAttribute = true)
        //业务类型 10：商品码；9：商户码（友宝售货机码），友宝目前只支持9；11：链接码；12：链接码（预授权业务）。
        private String biz_type;
        @JacksonXmlProperty(isAttribute = true)
        //业务数据
        private BizData biz_data;

        public static class BizData{

            //交易类型 1：即时到账 2：担保交易 当本参数设置为2时，need_address必须为T。
            private String trade_type;
            //是否需要收货地址 T：需要 F：不需要
            private String need_address;
            //商品明细
            private GoodsInfo goods_info;
            //通知商户下单URL
            private String return_url;
            //通知商户支付结果url
            private String notify_url;
            //查询商品信息url
            private String query_url;
            //扩展属性
            private ExtInfo ext_info;
            //备注
            private String memo;
            //链接地址
            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getTrade_type() {
                return trade_type;
            }

            public void setTrade_type(String trade_type) {
                this.trade_type = trade_type;
            }

            public String getNeed_address() {
                return need_address;
            }

            public void setNeed_address(String need_address) {
                this.need_address = need_address;
            }

            public GoodsInfo getGoods_info() {
                return goods_info;
            }

            public void setGoods_info(GoodsInfo goods_info) {
                this.goods_info = goods_info;
            }

            public String getReturn_url() {
                return return_url;
            }

            public void setReturn_url(String return_url) {
                this.return_url = return_url;
            }

            public String getNotify_url() {
                return notify_url;
            }

            public void setNotify_url(String notify_url) {
                this.notify_url = notify_url;
            }

            public String getQuery_url() {
                return query_url;
            }

            public void setQuery_url(String query_url) {
                this.query_url = query_url;
            }

            public ExtInfo getExt_info() {
                return ext_info;
            }

            public void setExt_info(ExtInfo ext_info) {
                this.ext_info = ext_info;
            }

            public String getMemo() {
                return memo;
            }

            public void setMemo(String memo) {
                this.memo = memo;
            }
        }

        public static class GoodsInfo{

            //商品编号
            private String id;
            //商品名称
            private String name;
            //商品价格
            private String price;
            //商品总库存
            private String inventory;
            //商品属性标题
            private String sku_title;
            //商品属性
            private List<Sku> sku;
            //商品有效期
            private String expiry_date;
            //商品描述
            private String desc;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getInventory() {
                return inventory;
            }

            public void setInventory(String inventory) {
                this.inventory = inventory;
            }

            public String getSku_title() {
                return sku_title;
            }

            public void setSku_title(String sku_title) {
                this.sku_title = sku_title;
            }

            public List<Sku> getSku() {
                return sku;
            }

            public void setSku(List<Sku> sku) {
                this.sku = sku;
            }

            public String getExpiry_date() {
                return expiry_date;
            }

            public void setExpiry_date(String expiry_date) {
                this.expiry_date = expiry_date;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public static class Sku{

                private String sku_id;
                private String sku_name;
                private String sku_price;
                private String sku_inventory;

                public String getSku_id() {
                    return sku_id;
                }

                public void setSku_id(String sku_id) {
                    this.sku_id = sku_id;
                }

                public String getSku_name() {
                    return sku_name;
                }

                public void setSku_name(String sku_name) {
                    this.sku_name = sku_name;
                }

                public String getSku_price() {
                    return sku_price;
                }

                public void setSku_price(String sku_price) {
                    this.sku_price = sku_price;
                }

                public String getSku_inventory() {
                    return sku_inventory;
                }

                public void setSku_inventory(String sku_inventory) {
                    this.sku_inventory = sku_inventory;
                }
            }
        }

        public static class ExtInfo{

            //单次购买上限
            private String single_limit;
            //单用户购买上限
            private String user_limit;
            //支付超时时间
            private String pay_timeout;
            //二维码logo名称
            private String logo_name;
            //自定义收集用户信息扩展字段
            private String ext_field;

            public String getSingle_limit() {
                return single_limit;
            }

            public void setSingle_limit(String single_limit) {
                this.single_limit = single_limit;
            }

            public String getUser_limit() {
                return user_limit;
            }

            public void setUser_limit(String user_limit) {
                this.user_limit = user_limit;
            }

            public String getPay_timeout() {
                return pay_timeout;
            }

            public void setPay_timeout(String pay_timeout) {
                this.pay_timeout = pay_timeout;
            }

            public String getLogo_name() {
                return logo_name;
            }

            public void setLogo_name(String logo_name) {
                this.logo_name = logo_name;
            }

            public String getExt_field() {
                return ext_field;
            }

            public void setExt_field(String ext_field) {
                this.ext_field = ext_field;
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getPartner() {
            return partner;
        }

        public void setPartner(String partner) {
            this.partner = partner;
        }

        public String get_input_charset() {
            return _input_charset;
        }

        public void set_input_charset(String _input_charset) {
            this._input_charset = _input_charset;
        }

        public String getSign_type() {
            return sign_type;
        }

        public void setSign_type(String sign_type) {
            this.sign_type = sign_type;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public String getBiz_type() {
            return biz_type;
        }

        public void setBiz_type(String biz_type) {
            this.biz_type = biz_type;
        }

        public BizData getBiz_data() {
            return biz_data;
        }

        public void setBiz_data(BizData biz_data) {
            this.biz_data = biz_data;
        }
    }
    public static class AliResponse {
        private String is_success;
        private String error;
        private Request request;



        private Response response;

        private String sign;

        private String sign_type;

        public String getIs_success() {
            return is_success;
        }

        public void setIs_success(String is_success) {
            this.is_success = is_success;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getSign_type() {
            return sign_type;
        }

        public void setSign_type(String sign_type) {
            this.sign_type = sign_type;
        }

        public static class Request{
            @JacksonXmlElementWrapper(useWrapping = false)
            private List<AliPay> param;

            public List<AliPay> getParam() {
                return param;
            }

            public void setParam(List<AliPay> param) {
                this.param = param;
            }
        }

        public static class Response{
            private Alipay alipay;

            public Alipay getAlipay() {
                return alipay;
            }

            public void setAlipay(Alipay alipay) {
                this.alipay = alipay;
            }

            public static class Alipay{
                private String qrcode;
                private String qrcode_img_url;
                private String result_code;
                private String error_message;

                public String getQrcode() {
                    return qrcode;
                }

                public void setQrcode(String qrcode) {
                    this.qrcode = qrcode;
                }

                public String getQrcode_img_url() {
                    return qrcode_img_url;
                }

                public void setQrcode_img_url(String qrcode_img_url) {
                    this.qrcode_img_url = qrcode_img_url;
                }

                public String getResult_code() {
                    return result_code;
                }

                public void setResult_code(String result_code) {
                    this.result_code = result_code;
                }

                public String getError_message() {
                    return error_message;
                }

                public void setError_message(String error_message) {
                    this.error_message = error_message;
                }
            }
        }
    }

}
