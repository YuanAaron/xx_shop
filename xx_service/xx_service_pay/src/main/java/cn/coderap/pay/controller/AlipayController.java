package cn.coderap.pay.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.Result;
import cn.coderap.order.pojo.Order;
import cn.coderap.pay.feign.OrderFeign;
import cn.coderap.pay.util.MatrixToImageWriter;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/alipay")
public class AlipayController {
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private OrderFeign orderFeign;

    /**
     * 请求二维码（为了保证接口的幂等性，前端调用该接口前先进性支付状态的校验，
     * 看是否处于待支付状态；当然后端在请求二维码链接前，也会先判断支付状态）
     *
     * 统一收单线下交易预创建
     * @param orderId  订单ID(out_trade_no)
     */
    @RequestMapping("/qrCode")
    public Result preCreate(@RequestParam String orderId) throws Exception {
        //1.获得订单对象，判断支付状态
        Order order = orderFeign.findById(orderId).getData();
        if (order == null) {
            return new Result(false, StatusCode.ERROR, "订单" + orderId + "不存在");
        }
        if ("1".equals(order.getPayStatus())) {
            return new Result(false, StatusCode.ERROR, "订单" + orderId + "已支付");
        }

        //2.创建AlipayTradePrecreateRequest对象
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        //设置notifyUrl

        //3.创建预处理业务模型
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        //设置商户订单号
        model.setOutTradeNo(orderId);
        //设置支付金额
        model.setTotalAmount(order.getTotalMoney().toString());
        //商品的标题/交易标题/订单标题/订单关键字等。
        model.setSubject("XX商城-订单支付");
        //将model放入到请求中
        request.setBizModel(model);

        //4.发送请求获取二维码链接
        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        if (response.isSuccess() && "10000".equals(response.getCode())) {
            //5.将二维码链接生成收款二维码
            //获得二维码链接
            String qrCode = response.getQrCode();
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bt = writer.encode(qrCode, BarcodeFormat.QR_CODE, 300, 300); //绘制二维码
            //生成二维码,将二维码写到输出流,返回到页面
//            MatrixToImageWriter.writeToStream(bt, "jpg", httpResponse.getOutputStream());
            //将二维码写入到磁盘
            File file = new File("/Users/oshacker/IdeaProjects/xx_shop/xx_service/xx_service_pay/src/main/resources/qrcodes", orderId + ".jpg");
            MatrixToImageWriter.writeToFile(bt, "jpg", file);
        }
        return new Result(true, StatusCode.OK, "交易预创建成功");
    }

    /**
     * 手动查询用户的支付结果
     *
     * 统一收单线下交易查询
     * @return
     */
    @GetMapping("/queryStatus")
    public String query(@RequestParam String out_trade_no) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(out_trade_no);
        request.setBizModel(model);

        AlipayTradeQueryResponse response = alipayClient.execute(request);
        /**
         * 判断订单状态
         * 响应正常->交易状态
         * 响应异常->错误原因
         * 其他问题->返回响应body
         */
        String result = response.getBody();
        if (response.isSuccess() && "10000".equals(response.getCode())) {
            result = response.getTradeStatus();
        } else {
            String subCode = response.getSubCode();
            if ("ACQ.SYSTEM_ERROR".equals(subCode)) {
                result = "系统错误,重新发起请求";
            }
            if ("ACQ.INVALID_PARAMETER".equals(subCode)) {
                result = "参数无效,检查请求参数，修改后重新发起请求";
            }
            if ("ACQ.TRADE_NOT_EXIST".equals(subCode)) {
                result = "查询的交易不存在,检查传入的交易号是否正确，修改后重新发起请求";
            }
        }
        return result;
    }
}
