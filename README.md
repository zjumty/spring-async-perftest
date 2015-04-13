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

用wrk测试单场景


延迟100ms的处理, 用同步模式和异步模式对比.

```
./wrk -c 100 -t 8 -d 60 --timeout=120 http://192.168.200.23:9000/foo/async-100ms

Running 1m test @ http://192.168.200.23:9000/foo/async-100ms
  8 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   609.43ms   32.22ms 724.79ms   98.11%
    Req/Sec    25.65     16.62    90.00     74.23%
  9360 requests in 1.00m, 2.41MB read
Requests/sec:    155.83
Transfer/sec:     41.09KB
```

```
./wrk -c 100 -t 8 -d 60 --timeout=120 http://192.168.200.23:9000/foo/sync-100ms

Running 1m test @ http://192.168.200.23:9000/foo/sync-100ms
  8 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   605.78ms   43.94ms 706.07ms   96.43%
    Req/Sec    28.31     23.22    90.00     73.35%
  9408 requests in 1.00m, 2.42MB read
Requests/sec:    156.64
Transfer/sec:     41.30KB
```

在这种场景下, 无论你用那种模式由于延迟的存在, CPU的利用率都不高. 所有即便同步模式下有频繁的线程切换, 只是CPU的利用率稍稍搞了一点. 吞吐量是一样的.



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

## 延迟和非延迟 1:9的请求, 也就是说有少量的高延迟访问, 大量的低延迟访问

### 如果高延迟采用异步模式
```
./wrk -c 900 -t 8 -d 60 --timeout=120 http://192.168.200.23:9000/foo/nodelay
Running 1m test @ http://192.168.200.23:9000/foo/nodelay
  8 threads and 900 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    16.13ms   24.54ms 863.32ms   97.81%
    Req/Sec     8.36k   689.01    11.18k    87.42%
  3993216 requests in 1.00m, 1.00GB read
Requests/sec:  66512.06
Transfer/sec:     17.13MB

./wrk -c 100 -t 8 -d 60 --timeout=120 http://192.168.200.23:9000/foo/async-100ms
Running 1m test @ http://192.168.200.23:9000/foo/async-100ms
  8 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   610.82ms   35.10ms 816.95ms   97.39%
    Req/Sec    24.43     23.64   111.00     88.33%
  9392 requests in 1.00m, 2.42MB read
Requests/sec:    156.39
Transfer/sec:     41.23KB
```
如果高延迟采用同步模式
```
./wrk -c 900 -t 8 -d 60 --timeout=120 http://192.168.200.23:9000/foo/nodelay
Running 1m test @ http://192.168.200.23:9000/foo/nodelay
  8 threads and 900 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   213.21ms  214.30ms 863.02ms   75.67%
    Req/Sec   640.82      1.75k   11.60k    94.33%
  306108 requests in 1.00m, 78.82MB read
Requests/sec:   5098.09
Transfer/sec:      1.31MB

./wrk -c 100 -t 8 -d 60 --timeout=120 http://192.168.200.23:9000/foo/sync-100ms
Running 1m test @ http://192.168.200.23:9000/foo/sync-100ms
  8 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   610.71ms   30.23ms 831.14ms   98.95%
    Req/Sec    30.99     30.55   111.00     82.36%
  9392 requests in 1.00m, 2.42MB read
Requests/sec:    156.38
Transfer/sec:     41.23KB
```
大家看到差异了吧!

在同步模式下由于Http线程被高延迟处理霸占, 没有其他线程处理低延迟请求.
而异步模式下由于高延迟处理不霸占HTTP线程, 所以低延迟的请求基本上没有受到影响.

上面的测试是在服务器设置了16线程的情况下执行的. 如果高延迟处理占用了线程, 那我们把服务器线程设置为200再试试.

结果如下:

```
./wrk -c 900 -t 8 -d 60 --timeout=120 http://192.168.200.23:9000/foo/nodelay
Running 1m test @ http://192.168.200.23:9000/foo/nodelay
  8 threads and 900 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    25.36ms   99.18ms   3.33s    99.21%
    Req/Sec     5.90k     0.87k    8.96k    92.58%
  2819464 requests in 1.00m, 725.99MB read
Requests/sec:  46961.89
Transfer/sec:     12.09MB

./wrk -c 100 -t 8 -d 60 --timeout=120 http://192.168.200.23:9000/foo/sync-100ms
Running 1m test @ http://192.168.200.23:9000/foo/sync-100ms
  8 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   610.49ms   37.34ms 689.02ms   97.20%
    Req/Sec    25.67     21.57   101.00     72.72%
  9359 requests in 1.00m, 2.41MB read
Requests/sec:    155.80
Transfer/sec:     41.08KB
```

可以看到同步模式比之前的结果好多了, 也就是高延迟处理不会对低延迟有很大影响, 但是任然比异步模式差.

在看看200线程的异步模式的结果:
```
./wrk -c 900 -t 8 -d 60 --timeout=120 http://192.168.200.23:9000/foo/nodelay
Running 1m test @ http://192.168.200.23:9000/foo/nodelay
  8 threads and 900 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    20.05ms   18.33ms 890.99ms   92.99%
    Req/Sec     6.01k   561.72    13.69k    90.15%
  2874649 requests in 1.00m, 740.20MB read
Requests/sec:  47831.09
Transfer/sec:     12.32MB

./wrk -c 100 -t 8 -d 60 --timeout=120 http://192.168.200.23:9000/foo/async-100ms
Running 1m test @ http://192.168.200.23:9000/foo/async-100ms
  8 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   613.31ms   27.23ms 721.98ms   96.03%
    Req/Sec    24.51     18.56    90.00     70.81%
  9349 requests in 1.00m, 2.41MB read
Requests/sec:    155.68
Transfer/sec:     41.05KB
```

可以看到在200线程的异步模式只比同步模式好一点点.

所以综合上面的测试结果, 可以得到如下结论: 异步Servlet模式在单场景和同步模式下几乎没有差别. 由于开发上更复杂所以没什么可取之处. 建议直接用同步模式. 在混合场景中如果是大量低延迟处理+少量高延迟处理的情况下, 由于异步模式不占用服务器线程, 可以有效减少服务器上的线程切换的资源消耗, 并且充分利用系统资源, 是有可取之处的.

上面测试的最佳结果的配置:

jetty服务器线程池16线程, 低延迟处理生成json立刻返回, 高延迟处理延迟100毫秒. 低延迟和高延迟请求比9:1. 
