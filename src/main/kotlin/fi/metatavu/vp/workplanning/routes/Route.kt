package fi.metatavu.vp.workplanning.routes

import fi.metatavu.vp.workplanning.persistence.Metadata
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

/**
 * Entity for routes
 */
@Entity
@Table(name = "route")
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