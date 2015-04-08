package org.devzen.controller;

import org.devzen.domain.FooBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Controller
@RequestMapping("/foo")
public class FooController {
    @Autowired
    @Qualifier("workerPool")
    private ExecutorService workerPool;


    @RequestMapping("/nodelay")
    public @ResponseBody FooBean nodelay() throws Exception {
        return new FooBean();
    }

    @RequestMapping("/sync-1s")
    public @ResponseBody FooBean sync1s() throws Exception {
        Future<FooBean> future = workerPool.submit(() ->
        {
            Thread.sleep(1000);
            return new FooBean();
        });
        return future.get();
    }

    @RequestMapping("/sync-100ms")
    public @ResponseBody FooBean sync100ms() throws Exception {
        Future<FooBean> future = workerPool.submit(() ->
        {
            Thread.sleep(100);
            return new FooBean();
        });
        return future.get();
    }

    @RequestMapping("/async-100ms")
    public @ResponseBody DeferredResult<FooBean> async100ms() throws Exception {
        DeferredResult<FooBean> defer = new DeferredResult<>(120000);
        workerPool.submit(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            defer.setResult(new FooBean());
        });
        return defer;
    }

    @RequestMapping("/async-1s")
    public @ResponseBody DeferredResult<FooBean> async1s() throws Exception {
        DeferredResult<FooBean> defer = new DeferredResult<>(120000);
        workerPool.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            defer.setResult(new FooBean());
        });
        return defer;
    }
}
