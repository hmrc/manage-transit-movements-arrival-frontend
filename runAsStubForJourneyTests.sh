#!/bin/bash
sbt -Dapplication.router=testOnlyDoNotUseInAppConf.Routes -Dmicroservice.services.destination.port=9481 -Dmicroservice.services.destination.startUrl=common-transit-convention-trader-at-destination run
