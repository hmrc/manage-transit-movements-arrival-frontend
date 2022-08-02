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
echo "$package$.$className;format="decap"$.addressLine1 = Building and Street" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.addressLine2 = City" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.postalCode = Post code" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.checkYourAnswersLabel = $title$" >> ../conf/messages.en

echo "$package$.$className;format="decap"$.error.postcode.invalid = The postcode of {0}’s address must only include letters a to z, numbers 0 to 9 and spaces" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.postcode.invalidFormat = Enter the postcode of {0}’s address in the right format, like AB1 1AB" >> ../conf/messages.en

echo "$package$.$className;format="decap"$.error.invalid = The {0} of {1}’s address must only include letters a to z without accents, numbers 0 to 9, ampersands (&), apostrophes, at signs (@), forward slashes, full stops, hyphens, question marks and spaces" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.required = Enter the {0} of {1}’s address" >> ../conf/messages.en
echo "$package$.$className;format="decap"$.error.length = The {0} of {1}’s address must be 35 characters or less" >> ../conf/messages.en

echo "Migration $className;format="snake"$ completed"
