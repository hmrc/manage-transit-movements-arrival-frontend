package pages.incident.equipment.itemNumber

import pages.behaviours.PageBehaviours

class ConfirmRemoveItemNumberPageSpec extends PageBehaviours {

  "ConfirmRemoveItemNumberPage" - {

    beRetrievable[Boolean](ConfirmRemoveItemNumberPage)

    beSettable[Boolean](ConfirmRemoveItemNumberPage)

    beRemovable[Boolean](ConfirmRemoveItemNumberPage)
  }
}
