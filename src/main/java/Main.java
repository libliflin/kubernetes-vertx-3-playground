import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class Main {

    private static void log(String m, Object... args) {
        String ip = "";
        {try{ ip = InetAddress.getLocalHost().toString();}catch(Exception ex){}}
        String threadname = Thread.currentThread().getName();
        String message = String.format(m, args);
        System.out.println(ip + " " + threadname + " " + message);
    }

    public static void main(String[] args) throws IOException {

        Vertx vertx = Vertx.vertx();
        AtomicInteger responseCode = new AtomicInteger(200);
        AtomicInteger shutdownCode = new AtomicInteger(404);
        AtomicLong shutdownSleep = new AtomicLong(35000);
        AtomicInteger livenessCode = new AtomicInteger(200);

        log( "starting up");

        vertx.createHttpServer().requestHandler(rc -> {
            int responseCodeF = responseCode.get();

            Boolean executeBlocking = Boolean.valueOf(rc.getParam("executeBlocking"));
            shutdownSleep.set(Long.valueOf(rc.getParam("shutdownSleep")));
            shutdownCode.set(Integer.valueOf(rc.getParam("shutdownCode")));
            long requestSleep = Long.valueOf(rc.getParam("requestSleep"));
            livenessCode.set(Integer.valueOf(rc.getParam("livenessCode")));
            String id = rc.getParam("id");
            log( "recieved request responseCode=%s, requestSleep=%s, id=%s", responseCodeF, requestSleep, id);
            Handler<Promise<String>> fn = (ignore) -> {
                try {
                    Thread.sleep(requestSleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log("responding to request responseCode=%s, requestSleep=%s, id=%s,", responseCodeF, requestSleep, id);
                rc.response().setStatusCode(responseCodeF).end("Service Response");
            };
            if (executeBlocking) {
                vertx.executeBlocking(fn, false, (ignore) -> {});
            } else {
                fn.handle(null);
            }
        }).listen(8888);
        vertx.createHttpServer().requestHandler(rc -> {
            log("%s to livenessCode=%s,", rc.path(), livenessCode.get());
            rc.response().setStatusCode(livenessCode.get()).end("Service Ready");
        }).listen(7999);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log("Initating shutdown shutdownSleep=%s, shutdownCode=%s,", shutdownSleep.get(), shutdownCode.get());
            responseCode.set(shutdownCode.get());
            try {
                Thread.sleep(shutdownSleep.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log("Finish shutdown shutdownSleep=%s, shutdownCode=%s,", shutdownSleep.get(), shutdownCode.get());
            }, "shutdown"));
    }
}