package fi.metatavu.vp.workplanning.routes

import fi.metatavu.vp.workplanning.Metadata
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

/**
 * Entity for routes
 */
@Entity
class Route: Metadata() {

    @Id
    var id: UUID? = null

    @Column
    var vehicleId: UUID? = null

    @Column
    var driverId: UUID? = null

    override lateinit var creatorId: UUID

    override lateinit var lastModifierId: UUID
}