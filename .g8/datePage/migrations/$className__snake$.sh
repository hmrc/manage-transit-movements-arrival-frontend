#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.$package$.routes"

if [ ! -f ../conf/app.$package$.routes ]; then
  echo "Write into prod.routes file"
  awk '/health.Routes/ {\
    print;\
    print "";\
    print "->         /manage-transit-movements/arrival                   app.$package$.Routes"
    next }1' ../conf/prod.routes >> tmp && mv tmp ../conf/prod.routes
fi

echo "" >> ../conf/app.$package$.routes
echo "GET        /:mrn/$package;format="packaged"$/$title;format="normalize"$                        controllers.$package$.$className$Controller.onPageLoad(mrn: MovementReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.$package$.routes
echo "POST       /:mrn/$package;format="packaged"$/$title;format="normalize"$                        controllers.$package$.$className$Controller.onSubmit(mrn: MovementReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.$package$.routes

echo "GET        /:mrn/$package;format="packaged"$/change-$title;format="normalize"$                 controllers.$package$.$className$Controller.onPageLoad(mrn: MovementReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.$package$.routes
echo "POST       /:mrn/$package;format="packaged"$/change-$title;format="normalize"$                 controllers.$package$.$className$Controller.onSubmit(mrn: MovementReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.$package$.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.title = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.heading = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.checkYourAnswersLabel = $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.hint = For example, 14 1 2020" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required.all = Enter the date for $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required.two = The date for $title$" must include {0} and {1} >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required = The date for $title$ must include {0}" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.invalid = Enter a real date for $title$" >> ../conf/messages.en


echo "Adding to UserAnswersEntryGenerators"
awk '/self: Generators =>/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$package;format="space,upper-camel"$$className$UserAnswersEntry: Arbitrary[(pages.$package$.$className$Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        value <- arbitrary[pages.$package$.$className$Page.type#Data].map(Json.toJson(_))";\
    print "      } yield (pages.$package$.$className$Page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary$package;format="space,upper-camel"$$className$UserAnswersEntry.arbitrary ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration $className;format="snake"$ completed"
