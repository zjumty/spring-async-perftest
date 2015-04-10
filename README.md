# spring-async-perftest

测试一下Servlet 3.1 的 Async Servlet 能对系能提升起到多少作用.

## 测试场景:

既然是使用异步Servlet, 那么后台处理必定是用线程池模式才有意义.

这次准备奢侈一把, 用公司的Intel(R) Xeon(R) CPU E5-2643 v2 @ 3.50GHz 6*4 的性能测试机试一试.

应用服务器jetty, 内嵌在Spring-Boot里. 

准备测试一下几种matrix

方式 |HTTP线程池 | 工作线程池 | 工作线程等待时间
---|---|---|---
异步 |16 | 16 | 1s
异步 |16 | 16 | 0.1s
同步 | 16 | 16 | 1s
同步 | 16 | 16 | 0.1s
同步 | 100 | 16 | 1s
同步 | 200 | 16 | 1s

并发量500, 1000, 2000, ....

----

Update: 0609

安装上面的场景测试下来性能基本上没有差别.

场景设定可能是有问题的.

用wrk测试

单场景
```
./wrk -c 5000 -t 16 -d 60 --timeout=120 http://192.168.200.23:9000/foo/nodelay
```

单场景结果
200 Threads
```
Running 1m test @ http://192.168.200.23:9000/foo/nodelay
  16 threads and 5000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   104.23ms   31.37ms   2.00s    93.20%
    Req/Sec     2.98k   276.67     6.04k    87.11%
  2844140 requests in 1.00m, 732.35MB read
Requests/sec:  47324.93
Transfer/sec:     12.19MB
```

16 Threads
```
Running 1m test @ http://192.168.200.23:9000/foo/nodelay
  16 threads and 5000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    91.36ms   76.54ms   3.97s    98.46%
    Req/Sec     3.49k   473.18    11.44k    86.49%
  3341535 requests in 1.00m, 860.42MB read
Requests/sec:  55628.92
Transfer/sec:     14.32MB
```

线程少时吞吐量更好.

异步场景

```
./wrk -c 5000 -t 16 -d 60 --timeout=120 http://192.168.200.23:9000/foo/async-100ms
```

200 Threads
```
Running 1m test @ http://192.168.200.23:9000/foo/async-100ms
  16 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.05s   502.13ms   4.64s    93.47%
    Req/Sec    20.05     21.61   121.00     87.01%
  9373 requests in 1.00m, 2.41MB read
Requests/sec:    155.99
Transfer/sec:     41.13KB
```

16 Threads
```
Running 1m test @ http://192.168.200.23:9000/foo/async-100ms
  16 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.05s   448.49ms   3.29s    94.05%
    Req/Sec    25.65     31.66   161.00     84.48%
  9408 requests in 1.00m, 2.42MB read
Requests/sec:    156.62
Transfer/sec:     41.30KB
```

吞吐量上没啥变化, 200线程时CPU使用高一点, 应该是在切换上下文上消耗的.


混合场景
```
./wrk -c 1000 -t 8 -d 30 --timeout=120 -s scripts/multiplepaths.lua http://192.168.200.23:9000/

```
两种请求平均分配的时, 和上面的没啥差别.