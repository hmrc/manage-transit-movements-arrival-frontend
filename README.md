
# manage-transit-movements-arrival-frontend

This service allows a user to record a transit movement arrival at its destination.

Service manager port: 10121

### Testing

Run unit tests:
<pre>sbt test</pre>
Run integration tests:
<pre>sbt it/test</pre>
Run accessibility linter tests:
<pre>sbt A11y/test</pre>

### Running manually or for journey tests

<pre>
sm2 --start CTC_TRADERS_P5_ACCEPTANCE
sm2 --stop MANAGE_TRANSIT_MOVEMENTS_ARRIVAL_FRONTEND
sbt run
</pre>

If you hit an entry point before running the journey tests, it gets the compile out of the way and can help keep the first tests from failing.  

e.g.: http://localhost:10121/manage-transit-movements/arrivals/movement-reference-number

### Feature toggles

The following features can be toggled in [application.conf](conf/application.conf):

| Key                             | Argument type | sbt                                                           | Description                                                                                                                                                                                    |
|---------------------------------|---------------|---------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `feature-flags.phase-6-enabled` | `Boolean`     | `sbt -Dfeature-flags.phase-6-enabled=true run`                | If enabled:<ul><li>customs-reference-data will retrieve reference data from crdl-cache</li><li>submission logic will follow phase 6 protocol</li>                                              |
| `trader-test.enabled`           | `Boolean`     | `sbt -Dtrader-test.enabled=true run`                          | If enabled, this will override the behaviour of the "Is this page not working properly?" and "feedback" links. This is so we can receive feedback in the absence of Deskpro in `externaltest`. |
| `banners.showUserResearch`      | `Boolean`     | `sbt -Dbanners.showUserResearch=true run`                     | Controls whether or not we show the user research banner.                                                                                                                                      |
| `play.http.router`              | `String`      | `sbt -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes run` | Controls which router is used for the application, either `prod.Routes` or `testOnlyDoNotUseInAppConf.Routes`                                                                                  |

### Scaffold

See [manage-transit-movements-departure-frontend](https://github.com/hmrc/manage-transit-movements-departure-frontend/blob/main/README.md#running-scaffold)

### User answers reader

See [manage-transit-movements-departure-frontend](https://github.com/hmrc/manage-transit-movements-departure-frontend/blob/main/README.md#user-answers-reader)


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

