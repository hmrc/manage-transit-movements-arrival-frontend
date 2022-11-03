package pages.incident.location

import models.reference.UnLocode
import pages.behaviours.PageBehaviours

class UnLocodePageSpec extends PageBehaviours {

  "UnLocodePage" - {

    beRetrievable[UnLocode](UnLocodePage)

    beSettable[UnLocode](UnLocodePage)

    beRemovable[UnLocode](UnLocodePage)
  }
}
