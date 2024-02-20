
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

#### Transition
<pre>
sm2 --start CTC_TRADERS_P5_ACCEPTANCE_TRANSITION
sm2 --stop MANAGE_TRANSIT_MOVEMENTS_ARRIVAL_FRONTEND_TRANSITION
sbt -Dplay.aditional.module=config.TransitionModule run
</pre>

#### Final
<pre>
sm2 --start CTC_TRADERS_P5_ACCEPTANCE
sm2 --stop MANAGE_TRANSIT_MOVEMENTS_ARRIVAL_FRONTEND
sbt -Dplay.aditional.module=config.PostTransitionModule run
</pre>

If you hit an entry point before running the journey tests, it gets the compile out of the way and can help keep the first tests from failing.  

e.g.: http://localhost:10121/manage-transit-movements/arrivals/movement-reference-number

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

