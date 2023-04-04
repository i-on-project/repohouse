package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.http.services.DeliveryServices
import org.springframework.web.bind.annotation.RestController

@RestController
class DeliveryController(
    private val deliveryServices: DeliveryServices,
) {
    // TODO: getDeliveryInfo :
    //      with links for each team
    //      with action for edit/delete delivery
    //      with action for sync
    // TODO: deleteDelivery if no teams exist
    // TODO: createDelivery
    // TODO: syncDelivery
}
