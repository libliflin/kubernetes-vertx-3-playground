import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.util.UUID;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class MainSimulation extends Simulation {


    ChainBuilder search = forever().on(
        pace(0,1)
        .exec(
            http("Home")
            .get((session) -> "/?" 
                + "requestSleep=4000" 
                + "&shutdownSleep=35000"
                + "&shutdownCode=404"
                + "&livenessCode=200"
                + "&executeBlocking=false"
                + "&id=" + UUID.randomUUID().toString())            
        ));

    HttpProtocolBuilder httpProtocol =
        http.baseUrl("http://host.com")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader(
                "Gatling/3.8.0"
            );

    ScenarioBuilder users = scenario("Users").exec(search);

    {
        setUp(users.injectOpen(atOnceUsers(20)))
        .maxDuration(400)
        .protocols(httpProtocol);
    }
}
