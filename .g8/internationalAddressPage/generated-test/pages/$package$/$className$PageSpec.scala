package pages.$package$

import pages.behaviours.PageBehaviours
import models.InternationalAddress

class $className$PageSpec extends PageBehaviours {

  "$className$Page" - {

    beRetrievable[InternationalAddress]($className$Page)

    beSettable[InternationalAddress]($className$Page)

    beRemovable[InternationalAddress]($className$Page)
  }
}
