package pages.$package$

import pages.behaviours.PageBehaviours
import models.UkAddress

class $className$PageSpec extends PageBehaviours {

  "$className$Page" - {

    beRetrievable[UkAddress]($className$Page)

    beSettable[UkAddress]($className$Page)

    beRemovable[UkAddress]($className$Page)
  }
}
