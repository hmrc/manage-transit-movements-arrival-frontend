package pages.$package$

import models.reference.$referenceClass$
import pages.behaviours.PageBehaviours

class $className$Spec extends PageBehaviours {

  "$className$Page" - {

    beRetrievable[$referenceClass$]($className$Page)

    beSettable[$referenceClass$]($className$Page)

    beRemovable[$referenceClass$]($className$Page)
  }
}
