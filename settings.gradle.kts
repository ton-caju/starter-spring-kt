rootProject.name = "starter"

include(
    "domain",
    "driven:persistence",
    "driven:event-producer",
    "driven:rest-client",
    "driver:rest-server",
    "driver:event-consumer"
)
