#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.$package$.routes"

if [ ! -f ../conf/app.$package$.routes ]; then
  echo "Write into app.routes file"
  awk '
  /# microservice specific routes/ {
    print;
    print "";
    next;
  }
  /^\$/ {
    if (!printed) {
      printed = 1;
      print "->         /                                                 app.$package$.Routes";
      next;
    }
    print;
    next;
  }
  {
    if (!printed) {
      printed = 1;
      print "->         /                                                 app.$package$.Routes";
    }
    print
  }' ../conf/app.routes > tmp && mv tmp ../conf/app.routes
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
echo "$package$.$className;format="decap"$.error.nonNumeric = Enter your $title$ using numbers" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required = Enter your $title$" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.wholeNumber = Enter your $title$ using whole numbers" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.maximum = $title$ must be {0} or less" >> ../conf/messages.en

echo "Migration $className;format="snake"$ completed"
