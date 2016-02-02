package UnisimPT

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._

class UnisimPT extends Simulation {

	val httpProtocol = http
		.baseURL("http://10.48.71.146")
		// .proxy(Proxy("192.168.1.1", 3128).httpsPort(3128))


	val headers = Map("Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
		"Pragma" -> "no-cache",
		"Accept-Encoding" -> "gzip, deflate",
		"Content-Type" -> "application/json; charset=utf-8")


	val StarterPackGroup = group("Starter pack activation") {
		    pause(0 milliseconds, 1000 milliseconds)
			.exec(addCookie(Cookie("{hiddenfield}_auth", "vtesthtestatest_auth.txt")))
			.exec(addCookie(Cookie("VtestHtestAtest_A", "customer")))
			.exec(addCookie(Cookie("_unisim_session", "cookie.txt")))

			.exec(http("Starter pack homepage")
				.get("/")
				.headers(headers))

			.exec(http("Starter pack validate")
				.post("/activation_key/validate")
				.headers(headers)
				.body(RawFileBody("activation_key.txt"))
				.check(status is 200, substring("\"key_status\":\"valid\"")))
			.pause(1)
			.exec(http("Starter pack document_verification")
				.post("/document_verification")
				.headers(headers)
				.body(RawFileBody("personal_info.txt"))
				.check(status is 200, substring("\"verification_result\":\"valid\"")))
			.pause(1)
			.exec(http("Starter pack msisdns")
				.post("/msisdns/allocate")
				.headers(headers)
				.body(RawFileBody("msisdns_allocate.txt")))
			.pause(1)
			.exec(http("Starter pack order")
				.post("/order")
				.headers(headers)
				.body(RawFileBody("order.txt"))
				.check(status is 200, regex("order_id\":\"1-1[0-9]{7}")))
			.pause(1)
			.exec(http("Starter pack status")
				.get("/order/status?activation_key=U09-1111-1111-1111")
				.headers(headers))
		}

	val TwoDollerSimGroup = group("2 doller sim activation"){
				pause(0 milliseconds, 1000 milliseconds)
				.exec(addCookie(Cookie("{hiddenfield}_auth", "vtesthtestatest_auth.txt")))
				.exec(addCookie(Cookie("VtestHtestAtest_A", "customer")))
				.exec(addCookie(Cookie("_unisim_session", "cookie.txt")))

				.exec(http("2 doller sim homepage")
					.get("/")
					.headers(headers))

				.exec(http("2 doller sim validate")
					.post("/activation_key/validate")
					.headers(headers)
					.body(RawFileBody("activation_key_2.txt"))
					.check(status is 200, substring("\"key_status\":\"valid\"")))

				.pause(1)
				.exec(http("2 doller sim document_verification")
					.post("/document_verification")
					.headers(headers)
					.body(RawFileBody("personal_info_2.txt"))
					.check(status is 200, substring("\"verification_result\":\"valid\"")))

				.pause(1)
				.exec(http("2 doller sim msisdns")
					.post("/msisdns/allocate")
					.headers(headers)
					.body(RawFileBody("msisdns_allocate_2.txt")))
				.pause(1)
				.exec(http("2 doller sim order")
					.post("/order")
					.headers(headers)
					.body(RawFileBody("order_2.txt"))
					.check(status is 200, regex("order_id\":\"1-1[0-9]{7}")))

				.pause(1)
				.exec(http("2 doller sim status")
					.get("/order/status?activation_key=U09-2222-2222-2222")
					.headers(headers))
			}


			var scn = scenario("both types of cards")
					  .during(30 minutes) {
					  exec(StarterPackGroup, TwoDollerSimGroup)
					  }


			setUp(
				scn.inject(
					rampUsers(3) over (3 seconds)
				).protocols(httpProtocol)
				)
}
