import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx.vertx().createHttpServer().requestHandler(rc -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            rc.response().end("Service Response");
        }).listen(8888);
        Vertx.vertx().createHttpServer().requestHandler(rc -> rc.response().end("Service Ready")).listen(7999);
        Runtime.getRuntime().addShutdownHook(new Thread("shutdown") {
            public void run() {
                System.out.println("sleeping before shutdown");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            }
        });
    }
}
