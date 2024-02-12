package fi.metatavu.vp.workplanning.functional

import fi.metatavu.vp.workplanning.functional.impl.UserManagementMock
import fi.metatavu.vp.workplanning.functional.impl.VehicleManagementMock
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(VehicleManagementMock::class),
    QuarkusTestResource(UserManagementMock::class),
)
class SystemTestIT {

    @Test
    fun testPing() {
        When { get("/v1/system/ping") }
            .then()
            .assertThat()
            .statusCode(200)
            .body(equalTo("pong"))
    }
}