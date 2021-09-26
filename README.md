# my-redis

### Problem statememt-:
mini-redis

If you are not familiar with how Redis works – read up on https://redis.io
 
Create a service which do the following: - 
 
-        Store a key value pair in memory via a http POST method
o    For example, posting {‘k’: “v”} to http://<localhost>/my-redis/ should store the value in the server and return the appropriate HTTP response code
-        Retrieve a value from memory given the key via a http GET method
o    For example, a GET call on  http://<localhost>/my-redis/k should return “v” and return the appropriate HTTP response code
-        Retrieve an incremented value of a key via a http PUT method
o    For example, posting {‘ik’: 1} to http://<localhost>/my-redis/ should store the value in the server and return the appropriate HTTP response code
o    A PUT call to http://<localhost>/my-redis/increment/ik should internally increment the value of the key ik in the server to 2 and return 2 along with the applicable response codes
o    A GET call to http://<localhost>/my-redis/ik should return 2 along with the applicable http response code to prove the above point
 
Points to note:
-        Extra credit if you can do this in java or golang
-       Extra credit if you can do this  The increment method MUST be written in a thread-safe manner. 
-       To prove this, 
 
o    Post {‘threadSafeKey’: 1} to your server http://<localhost>/my-redis
o    Run apache bench -- ab -n 100 -c 10 http://<localhost>/my-redis/increment/threadSafekey  (100 calls with a max of 10 being concurrent)
o    A GET call on http://<localhost>/my-redis/threadSafeKey should return 101 along with the appropriate http response code
 

 
 
### Solution proposed-:
 
#### There are three flavours of Update operations-:
1. MyRedisMapComputeDao - This uses ConcurrentHashMap's computeIfPresent to atomically update.
2. MyRedisAtomicCASUpdateDao - This uses AtomicInteger for CHM value, in high contention, it may waste CPU cycles
3. MyRedisConcurrentUpdatePerformantDao - This uses LongAdder which is recommended for high concurrent updates(high contention).

#### Apache Bench was used to measure the performance of these three flavors in concurrency level of 10 with 1000 requests(AB is not for load test, please use JMeter) using following-:

ab -u empty_file.txt -n 1000 -c 10 http://127.0.0.1:8080/my-redis/key/


#### And below are the results -:
-------------------------------------------------------------------------------------------
#### For myRedisMapComputeDao

Finished 1000 requests


Server Software:        
Server Hostname:        127.0.0.1
Server Port:            8080

Document Path:          /my-redis/key/
Document Length:        61 bytes

Concurrency Level:      10
Time taken for tests:   0.357 seconds
Complete requests:      1000
Failed requests:        0
Total transferred:      166000 bytes
Total body sent:        140000
HTML transferred:       61000 bytes
Requests per second:    2802.67 [#/sec] (mean)
Time per request:       3.568 [ms] (mean)
Time per request:       0.357 [ms] (mean, across all concurrent requests)
Transfer rate:          454.34 [Kbytes/sec] received
                        383.18 kb/s sent
                        837.52 kb/s total

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    1   0.7      0       6
Processing:     0    3   6.1      2      56
Waiting:        0    2   6.1      1      56
Total:          1    3   6.1      2      57
WARNING: The median and mean for the initial connection time are not within a normal deviation
        These results are probably not that reliable.

##### Percentage of the requests served within a certain time (ms)
  50%      2
  66%      3
  75%      3
  80%      3
  90%      4
  95%      6
  98%     29
  99%     56
 100%     57 (longest request)
 -------------------------------------------------------------------------------------------

#### For myRedisAtomicCASUpdateDao

 Finished 1000 requests


Server Software:        
Server Hostname:        127.0.0.1
Server Port:            8080

Document Path:          /my-redis/key/
Document Length:        61 bytes

Concurrency Level:      10
Time taken for tests:   0.329 seconds
Complete requests:      1000
Failed requests:        0
Total transferred:      166000 bytes
Total body sent:        140000
HTML transferred:       61000 bytes
Requests per second:    3041.00 [#/sec] (mean)
Time per request:       3.288 [ms] (mean)
Time per request:       0.329 [ms] (mean, across all concurrent requests)
Transfer rate:          492.97 [Kbytes/sec] received
                        415.76 kb/s sent
                        908.74 kb/s total

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    1   0.6      1       5
Processing:     0    2   3.6      2      32
Waiting:        0    2   3.6      1      32
Total:          1    3   3.6      2      33

Percentage of the requests served within a certain time (ms)
  50%      2
  66%      3
  75%      3
  80%      4
  90%      5
  95%      6
  98%     18
  99%     32
 100%     33 (longest request)

-------------------------------------------------------------------------------------------
#### For myRedisConcurrentUpdatePerformantDao

Finished 1000 requests


Server Software:        
Server Hostname:        127.0.0.1
Server Port:            8080

Document Path:          /my-redis/key/
Document Length:        61 bytes

Concurrency Level:      10
Time taken for tests:   0.342 seconds
Complete requests:      1000
Failed requests:        0
Total transferred:      166000 bytes
Total body sent:        140000
HTML transferred:       61000 bytes
Requests per second:    2919.84 [#/sec] (mean)
Time per request:       3.425 [ms] (mean)
Time per request:       0.342 [ms] (mean, across all concurrent requests)
Transfer rate:          473.33 [Kbytes/sec] received
                        399.20 kb/s sent
                        872.53 kb/s total

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    1   0.6      1       5
Processing:     1    2   1.4      2      12
Waiting:        0    2   1.3      2      11
Total:          1    3   1.5      3      13

Percentage of the requests served within a certain time (ms)
  50%      3
  66%      3
  75%      4
  80%      4
  90%      5
  95%      7
  98%      8
  99%      9
 100%     13 (longest request)


-------------------------------------------------------------------------------------------

### Conclusion -: In concurrent environment, all variants(myRedisMapComputeDao, myRedisAtomicCASUpdateDao, myRedisConcurrentUpdatePerformantDao) performed in a thread safe manner but there was almost no performance difference seen across the three variants for update, which needs more figuring
