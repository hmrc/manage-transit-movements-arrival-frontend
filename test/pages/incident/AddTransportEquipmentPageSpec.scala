package pages.incident

import pages.behaviours.PageBehaviours
import pages.incident.equipment.AddTransportEquipmentPage

class AddTransportEquipmentPageSpec extends PageBehaviours {

  "AddTransportEquipmentPage" - {

    beRetrievable[Boolean](AddTransportEquipmentPage)

    beSettable[Boolean](AddTransportEquipmentPage)

    beRemovable[Boolean](AddTransportEquipmentPage)
  }
}
