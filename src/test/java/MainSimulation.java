import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class MainSimulation extends Simulation {


    ChainBuilder search = exec(
        http("Home").get("/")
    );

    HttpProtocolBuilder httpProtocol =
        http.baseUrl("http://localhost:8888")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
            );

    ScenarioBuilder users = scenario("Users").exec(search);

    {
        setUp(
            users.injectClosed(
                constantConcurrentUsers(1).during(200))
        ).protocols(httpProtocol);
    }
}
