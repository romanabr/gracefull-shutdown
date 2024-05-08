# Тестирование отключения микросервиса

## Тест1 Локально shutdown: immediate
GET http://localhost:8080/test/2000
Threads: 10, Loop count: 50

### Результат:
на 10 потоках ошибки: org.apache.http.NoHttpResponseException: localhost:8080 failed to respond
время ответа: 11, 207, 1322, 1207, 419 и тд 
оставшиеся запросы: org.apache.http.conn.HttpHostConnectException: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused
время ответа: 3, 2, 7, 4 ms 

## Тест2 Локально  shutdown: gracefull
### Результат:
последние 10 потоков получают успех
после последнего успешного ответа клиент получает ошибку:
org.apache.http.conn.HttpHostConnectException: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused
 
лог:
~~~
2024-05-06T23:49:07.767+03:00  INFO 32323 --- [       Thread-0] ru.rabramov.Application                  : --- shutdown hook detected
2024-05-06T23:49:07.768+03:00  INFO 32323 --- [ionShutdownHook] o.s.b.w.e.tomcat.GracefulShutdown        : Commencing graceful shutdown. Waiting for active requests to complete
2024-05-06T23:49:07.773+03:00  INFO 32323 --- [tomcat-shutdown] o.s.b.w.e.tomcat.GracefulShutdown        : Graceful shutdown complete
2024-05-06T23:49:07.787+03:00  INFO 32323 --- [ionShutdownHook] o.s.b.w.e.tomcat.GracefulShutdown        : Commencing graceful shutdown. Waiting for active requests to complete
2024-05-06T23:49:07.835+03:00  INFO 32323 --- [nio-8080-exec-6] ru.rabramov.SampleRestController         : Health check invocation with param: 2000
2024-05-06T23:49:08.035+03:00  INFO 32323 --- [nio-8080-exec-7] ru.rabramov.SampleRestController         : Health check invocation with param: 2000
...
2024-05-06T23:49:09.638+03:00  INFO 32323 --- [nio-8080-exec-5] ru.rabramov.SampleRestController         : Health check invocation with param: 2000
2024-05-06T23:49:09.680+03:00  INFO 32323 --- [tomcat-shutdown] o.s.b.w.e.tomcat.GracefulShutdown        : Graceful shutdown complete
2024-05-06T23:49:09.684+03:00  INFO 32323 --- [ionShutdownHook] ru.rabramov.MyDisposableService          : --- MyService.destroy
~~~



## Тест3 в openshift
sidecar.istio.io/inject: 'true'
shutdown: immediate
GET http://grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru/test/2000
Threads: 10, Loop count: 50

### Результат: влияния нет

нет ошибок у клиентов
контейнер с java не получил KILLTERM,
контейнер с envoy завершился с Graceful termination period is 5s
log istio-proxy:
~~~
2024-05-06T21:00:37.779586Z     info    Agent draining Proxy
2024-05-06T21:00:37.779575Z     info    Status server has successfully terminated
2024-05-06T21:00:37.780165Z     error   accept tcp [::]:15020: use of closed network connection
2024-05-06T21:00:37.780923Z     info    Graceful termination period is 5s, starting...
[2024-05-06T21:00:35.750Z] "GET /test/2000 HTTP/1.1" 200 - via_upstream - "-" 0 7 2004 2004 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "bce23f11-6334-9858-b611-dd96006de8d9" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:39536 29.64.150.109:8080 10.6.142.8:0 - default
[2024-05-06T21:00:35.751Z] "GET /test/2000 HTTP/1.1" 200 - via_upstream - "-" 0 7 2003 2003 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "699bfad7-6d28-9792-b68c-ff6b1357bc7d" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:39462 29.64.150.109:8080 10.6.142.8:0 - default
[2024-05-06T21:00:35.894Z] "GET /test/2000 HTTP/1.1" 200 - via_upstream - "-" 0 7 2023 2023 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "91a1d9bb-4e57-9b28-a39e-49f8c6ca8419" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:39464 29.64.150.109:8080 10.6.142.8:0 - default
2024-05-06T21:00:38.268932Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
[2024-05-06T21:00:36.498Z] "GET /test/2000 HTTP/1.1" 200 - via_upstream - "-" 0 7 2005 2004 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "5f3f65ff-04ef-978c-a23f-c25b04d5e4ba" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:39490 29.64.150.109:8080 10.6.142.8:0 - default
[2024-05-06T21:00:37.174Z] "GET /test/2000 HTTP/1.1" 200 - via_upstream - "-" 0 7 2004 2003 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "ff49bf6a-ffbc-9b0c-bb32-de8237fe78ac" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:39504 29.64.150.109:8080 10.6.142.8:0 - default
[2024-05-06T21:00:37.782Z] "GET /test/2000 HTTP/1.1" 200 - via_upstream - "-" 0 7 2002 2002 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "8dc8ecbb-8f92-98f6-b84e-ea2feed6ea60" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:39536 29.64.150.109:8080 10.6.142.8:0 - default
[2024-05-06T21:00:37.781Z] "GET /test/2000 HTTP/1.1" 200 - via_upstream - "-" 0 7 2004 2003 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "46554d3f-4f6e-90bb-9638-5004df9a0b85" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:39462 29.64.150.109:8080 10.6.142.8:0 - default
[2024-05-06T21:00:37.938Z] "GET /test/2000 HTTP/1.1" 200 - via_upstream - "-" 0 7 2052 2052 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "ec5199d6-1abc-9fb7-8c61-9e793dcdfa97" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:53426 29.64.150.109:8080 10.6.142.8:0 - default
2024-05-06T21:00:40.269402Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
[2024-05-06T21:00:38.322Z] "GET /test/2000 HTTP/1.1" 200 - via_upstream - "-" 0 7 2015 2014 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "54359be5-90f3-92bc-b885-9de9419a5675" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:39464 29.64.150.109:8080 10.6.142.8:0 - default
2024-05-06T21:00:42.271764Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:00:42.781351Z     info    Graceful termination period complete, terminating remaining proxies.
2024-05-06T21:00:42.781411Z     warn    Aborted proxy instance
2024-05-06T21:00:42.781475Z     warn    Aborting proxy
2024-05-06T21:00:42.781768Z     info    Envoy aborted normally
2024-05-06T21:00:42.781783Z     info    Agent has successfully terminated
2024-05-06T21:00:42.781910Z     error   error serving tap http server: http: Server closed
2024-05-06T21:00:42.783364Z     info    ads     ADS: "@" gracefull-shutdown-66c766dbd-6jp6w.ci02211379-eiftefsemp1dm-gateway-1 terminated
2024-05-06T21:00:42.783427Z     info    ads     ADS: "@" gracefull-shutdown-66c766dbd-6jp6w.ci02211379-eiftefsemp1dm-gateway-3 terminated
2024-05-06T21:00:42.783530Z     info    ads     ADS: "@" gracefull-shutdown-66c766dbd-6jp6w.ci02211379-eiftefsemp1dm-gateway-2 terminated
2024-05-06T21:00:42.783613Z     info    sds     SDS server for workload certificates started, listening on "./var/run/secrets/workload-spiffe-uds/socket"
~~~

## Тест4 в openshift, sleep 20 sec
sidecar.istio.io/inject: 'true'
shutdown: immediate
GET http://grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru/test/20_000
Threads: 10, Loop count: 50
### Результат: 5 запросов завершилось с ошибкой
org.apache.http.NoHttpResponseException: grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru:80 failed to respond
время ответа: 5078, 5674, 4669, 4880, 5677


## Тест5 в openshift, shutdown: gracefull 
sidecar.istio.io/inject: 'true'
"proxy.istio.io/config": "{ \"terminationDrainDuration\": \"40s\" }"
terminationGracePeriodSeconds: 40
shutdown: gracefull
java получает SIGTERM
Threads: 10, Loop count: 50

### Результат

лог java:
~~~ 
2024-05-07T00:20:21.459+03:00  INFO 1 --- [nio-8080-exec-3] ru.rabramov.SampleRestController         : Health check invocation with param: 20000
2024-05-07T00:20:22.015+03:00  INFO 1 --- [nio-8080-exec-4] ru.rabramov.SampleRestController         : Health check invocation with param: 20000
2024-05-07T00:20:22.072+03:00  INFO 1 --- [       Thread-0] ru.rabramov.Application                  : --- shutdown hook detected
2024-05-07T00:20:22.075+03:00  INFO 1 --- [ionShutdownHook] o.s.b.w.e.tomcat.GracefulShutdown        : Commencing graceful shutdown. Waiting for active requests to complete
2024-05-07T00:20:22.080+03:00  INFO 1 --- [tomcat-shutdown] o.s.b.w.e.tomcat.GracefulShutdown        : Graceful shutdown complete
2024-05-07T00:20:22.093+03:00  INFO 1 --- [ionShutdownHook] o.s.b.w.e.tomcat.GracefulShutdown        : Commencing graceful shutdown. Waiting for active requests to complete
2024-05-07T00:20:22.459+03:00  INFO 1 --- [nio-8080-exec-5] ru.rabramov.SampleRestController         : Health check invocation with param: 20000
2024-05-07T00:20:22.653+03:00  INFO 1 --- [nio-8080-exec-6] ru.rabramov.SampleRestController         : Health check invocation with param: 20000
2024-05-07T00:20:40.959+03:00  INFO 1 --- [nio-8080-exec-7] ru.rabramov.SampleRestController         : Health check invocation with param: 20000
2024-05-07T00:20:41.481+03:00  INFO 1 --- [nio-8080-exec-8] ru.rabramov.SampleRestController         : Health check invocation with param: 20000
2024-05-07T00:20:41.651+03:00  INFO 1 --- [nio-8080-exec-9] ru.rabramov.SampleRestController         : Health check invocation with param: 20000
2024-05-07T00:20:42.050+03:00  INFO 1 --- [io-8080-exec-10] ru.rabramov.SampleRestController         : Health check invocation with param: 20000
2024-05-07T00:20:42.079+03:00  INFO 1 --- [tomcat-shutdown] o.s.b.w.e.tomcat.GracefulShutdown        : Graceful shutdown complete
2024-05-07T00:20:42.086+03:00  INFO 1 --- [ionShutdownHook] ru.rabramov.MyDisposableService          : --- MyService.destroy
~~~

лог istio:
~~~
2024-05-06T21:18:28.310811Z     info    Envoy proxy is ready
[2024-05-06T21:20:00.852Z] "GET /test/20000 HTTP/1.1" 200 - via_upstream - "-" 0 8 20027 20026 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "3a70c9d2-68cd-9485-91d9-d161800c2cbc" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:44298 29.64.38.195:8080 10.6.142.8:0 - default
[2024-05-06T21:20:01.012Z] "GET /test/20000 HTTP/1.1" 200 - via_upstream - "-" 0 8 20048 20048 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "23b346c4-13d9-9064-a945-07d900c58652" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:44302 29.64.38.195:8080 10.6.142.8:0 - default
[2024-05-06T21:20:01.412Z] "GET /test/20000 HTTP/1.1" 200 - via_upstream - "-" 0 8 20048 20047 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "e6577d07-fcee-9fd4-96a2-cce43a9a66e8" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:44308 29.64.38.195:8080 10.6.142.8:0 - default
2024-05-06T21:20:22.069717Z     info    Agent draining Proxy
2024-05-06T21:20:22.069714Z     info    Status server has successfully terminated
2024-05-06T21:20:22.070076Z     error   accept tcp [::]:15020: use of closed network connection
2024-05-06T21:20:22.070582Z     info    Graceful termination period is 40s, starting...
2024-05-06T21:20:22.302714Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
[2024-05-06T21:20:02.012Z] "GET /test/20000 HTTP/1.1" 200 - via_upstream - "-" 0 8 20004 20004 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "35292dff-e7d7-991c-96a5-51994afea3df" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:33402 29.64.38.195:8080 10.6.142.8:0 - default
[2024-05-06T21:20:02.456Z] "GET /test/20000 HTTP/1.1" 200 - via_upstream - "-" 0 8 20004 20003 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "226f3c0d-adf0-93f6-85b1-a4d38c88e2c9" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:33416 29.64.38.195:8080 10.6.142.8:0 - default
[2024-05-06T21:20:02.651Z] "GET /test/20000 HTTP/1.1" 200 - via_upstream - "-" 0 8 20003 20003 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "e94ab5c2-4831-9136-a39f-f8fa099b3a24" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:33428 29.64.38.195:8080 10.6.142.8:0 - default
2024-05-06T21:20:24.303017Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:26.308280Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:28.303341Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:30.304595Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:32.303639Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:34.303735Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:36.303739Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:38.303172Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:40.306051Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
[2024-05-06T21:20:20.944Z] "GET /test/20000 HTTP/1.1" 200 - via_upstream - "-" 0 8 20016 20016 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "e8cb083b-c460-9302-b533-36a3281cd1fc" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:44298 29.64.38.195:8080 10.6.142.8:0 - default
[2024-05-06T21:20:21.478Z] "GET /test/20000 HTTP/1.1" 200 - via_upstream - "-" 0 8 20003 20003 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "2ae2823b-656b-99fe-91f6-2e37f05c158d" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:44308 29.64.38.195:8080 10.6.142.8:0 - default
[2024-05-06T21:20:21.647Z] "GET /test/20000 HTTP/1.1" 200 - via_upstream - "-" 0 8 20005 20004 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "6f8f4129-224d-9df7-a5c3-e1d4c424dc40" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:44302 29.64.38.195:8080 10.6.142.8:0 - default
2024-05-06T21:20:42.303110Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
[2024-05-06T21:20:22.048Z] "GET /test/20000 HTTP/1.1" 200 - via_upstream - "-" 0 8 20003 20003 "10.6.142.8" "Apache-HttpClient/4.5.13 (Java/17.0.5)" "2f8b77e3-9a96-9817-b441-0178bee24369" "grasefull-shutdown-service-ci02211379-eiftefsemp1dm-gateway.apps.ift-efsemp1-dm.delta.sbrf.ru" "127.0.0.1:8080" inbound|8080|| 127.0.0.1:33402 29.64.38.195:8080 10.6.142.8:0 - default
2024-05-06T21:20:44.303014Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:46.307205Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:48.305068Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:50.303660Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:52.313439Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:54.304119Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:56.303060Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:20:58.302852Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:21:00.302730Z     warn    Envoy proxy is NOT ready: server is not live, current state is: DRAINING
2024-05-06T21:21:02.071626Z     info    Graceful termination period complete, terminating remaining proxies.
2024-05-06T21:21:02.071707Z     warn    Aborted proxy instance
2024-05-06T21:21:02.071717Z     warn    Aborting proxy
2024-05-06T21:21:02.071822Z     info    Envoy aborted normally
2024-05-06T21:21:02.071829Z     info    Agent has successfully terminated
2024-05-06T21:21:02.071881Z     error   error serving tap http server: http: Server closed
2024-05-06T21:21:02.072166Z     info    sds     SDS server for workload certificates started, listening on "./var/run/secrets/workload-spiffe-uds/socket"
2024-05-06T21:21:02.072228Z     info    ads     ADS: "@" gracefull-shutdown-768df89d4c-d9tk5.ci02211379-eiftefsemp1dm-gateway-1 terminated
2024-05-06T21:21:02.072267Z     info    ads     ADS: "@" gracefull-shutdown-768df89d4c-d9tk5.ci02211379-eiftefsemp1dm-gateway-3 terminated
2024-05-06T21:21:02.072287Z     info    ads     ADS: "@" gracefull-shutdown-768df89d4c-d9tk5.ci02211379-eiftefsemp1dm-gateway-2 terminated
~~~

для sleep 20 sec: 0 ошибок
для sleep 10 sec: 0 ошибок
для sleep 2 sec: много ошибок c 00:28:15.934 по 00:28:14.583, 
    в течении 500ms pod отвечал ошибками:
    upstream connect error or disconnect/reset before headers. reset reason: connection termination
    upstream connect error or disconnect/reset before headers. reset reason: connection failure, transport failure reason: delayed connect error: 111
для sleep 200ms: много ошибок c 00:32:11.491 по 00:32:12.366
    в течении 875ms pod отвечал ошибками

## Тест6 в openshift, shutdown: immediate
sidecar.istio.io/inject: 'true'
"proxy.istio.io/config": "{ \"terminationDrainDuration\": \"40s\" }"
terminationGracePeriodSeconds: 40
shutdown: immediate
java получает SIGTERM
Threads: 10, Loop count: 50

для sleep 20 sec: 0 ошибок
для sleep 10 sec: 0 ошибок
для sleep 2 sec: много ошибок c 00:28:15.934 по 00:28:14.583, 
