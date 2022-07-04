#!/bin/bash

echo ""
echo "Applying migration MyNewYesNo"

echo "Adding routes to conf/app.identification.routes"

if [ ! -f ../conf/app.identification.routes ]; then
  echo "Write into prod.routes file"
  awk '/health.Routes/ {\
    print;\
    print "";\
    print "->         /manage-transit-movements/arrival                   app.identification.Routes"
    next }1' ../conf/prod.routes >> tmp && mv tmp ../conf/prod.routes
fi

echo "" >> ../conf/app.identification.routes
echo "GET        /:mrn/identification/my-new-yes-no                        controllers.identification.MyNewYesNoController.onPageLoad(mrn: MovementReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.identification.routes
echo "POST       /:mrn/identification/my-new-yes-no                        controllers.identification.MyNewYesNoController.onSubmit(mrn: MovementReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.identification.routes

echo "GET        /:mrn/identification/change-my-new-yes-no                 controllers.identification.MyNewYesNoController.onPageLoad(mrn: MovementReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.identification.routes
echo "POST       /:mrn/identification/change-my-new-yes-no                 controllers.identification.MyNewYesNoController.onSubmit(mrn: MovementReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.identification.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "identification.myNewYesNo.title = My New Yes No" >> ../conf/messages.en
echo "identification.myNewYesNo.heading = My New Yes No" >> ../conf/messages.en
echo "identification.myNewYesNo.checkYourAnswersLabel = My New Yes No" >> ../conf/messages.en
echo "identification.myNewYesNo.error.required = Select yes if My New Yes No" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/self: Generators =>/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIdentificationMyNewYesNoUserAnswersEntry: Arbitrary[(pages.identification.MyNewYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        value <- arbitrary[pages.identification.MyNewYesNoPage.type#Data].map(Json.toJson(_))";\
    print "      } yield (pages.identification.MyNewYesNoPage, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitraryIdentificationMyNewYesNoUserAnswersEntry.arbitrary ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration MyNewYesNo completed"
