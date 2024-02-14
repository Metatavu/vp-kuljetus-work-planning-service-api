package fi.metatavu.vp.workplanning.functional

import fi.metatavu.vp.workplanning.functional.impl.UserManagementMock
import fi.metatavu.vp.workplanning.functional.impl.VehicleManagementMock
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusIntegrationTest

/**
 * Native tests for System API
 */
@QuarkusIntegrationTest
@QuarkusTestResource.List(
    QuarkusTestResource(VehicleManagementMock::class),
    QuarkusTestResource(UserManagementMock::class),
)
class NativeSystemTestIT: SystemTestIT()
