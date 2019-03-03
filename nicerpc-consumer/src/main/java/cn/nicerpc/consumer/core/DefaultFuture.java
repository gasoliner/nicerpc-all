package cn.nicerpc.consumer.core;

import cn.nicerpc.common.param.ClientRequest;
import cn.nicerpc.common.param.Response;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultFuture {

    public final static ConcurrentHashMap<Long, DefaultFuture> allDefaultFuture = new ConcurrentHashMap<>();

    final Lock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    private Response response;

    private long timeout = 2 * 60 * 1000l;

    private long startTime = System.currentTimeMillis();

    public DefaultFuture(ClientRequest request) {
        allDefaultFuture.put(request.getId(), this);

    }

    /**
     * 主线程获取数据
     * 首先要等待结果
     *
     * @return
     */
    public Response get() {
        lock.lock();
        try {
            while (!done()) {
                condition.await();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return this.response;
    }

    public Response get(long time) {
        lock.lock();
        try {
            while (!done()) {
                condition.await(time, TimeUnit.SECONDS);
                if ((System.currentTimeMillis() - startTime) > time * 1000) {
                    System.out.println("请求超时！");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return this.response;
    }

    private boolean done() {
        return this.response != null;
    }

    public static void receive(Response response) {
        DefaultFuture temp = allDefaultFuture.get(response.getId());
        if (temp != null) {
            Lock lock = temp.lock;
            lock.lock();
            try {
                temp.setResponse(response);
                temp.condition.signal();
                allDefaultFuture.remove(response.getId());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        } else {
            System.out.println("wrong ! temp == null!");
        }
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getStartTime() {
        return startTime;
    }

    static class FutureThread extends Thread {
        @Override
        public void run() {
            super.run();
            for (Long id:
            allDefaultFuture.keySet()) {
                DefaultFuture fu = allDefaultFuture.get(id);
                if (fu == null) {
                    allDefaultFuture.remove(id);
                } else {
//                    假如这个链路超时了
                    if ((System.currentTimeMillis() - fu.startTime) > fu.timeout) {
                        Response response = new Response();
                        response.setId(id);
                        response.setCode("333333");
                        response.setMsg("链路请求超时");
                        receive(response);
                    }
                }
            }
        }
    }

    static {
        FutureThread thread = new FutureThread();
        /**
         * 在Java中有两类线程：用户线程 (User Thread)、守护线程 (Daemon Thread)。
         *
         * 所谓守护 线程，是指在程序运行的时候在后台提供一种通用服务的线程，比如垃圾回收线程就是一个很称职的守护者，
         * 并且这种线程并不属于程序中不可或缺的部分。
         * 因 此，当所有的非守护线程结束时，程序也就终止了，同时会杀死进程中的所有守护线程。
         * 反过来说，只要任何非守护线程还在运行，程序就不会终止。
         *
         * 用户线程和守护线程两者几乎没有区别，唯一的不同之处就在于虚拟机的离开：如果用户线程已经全部退出运行了，只剩下守护线程存在了，
         * 虚拟机也就退出了。 因为没有了被守护者，守护线程也就没有工作可做了，也就没有继续运行程序的必要了。
         *
         * https://www.cnblogs.com/luochengor/archive/2011/08/11/2134818.html
         */
        thread.setDaemon(true);
        thread.start();
    }
}
