package cn.coderap.business.listener;

import okhttp3.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RabbitListener(queues = "ad_update_queue")
@Component
public class AdListener {

    @RabbitHandler
    public void adUpdate(String message){
        System.out.println("接收到的消息:"+message);
        String url = "http://192.168.1.128/ad_update?position=" + message;

        //通过OkHttpClient发出请求
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获得lua脚本的响应
                System.out.println("调用成功," + response.message());
            }
        });
    }
}
