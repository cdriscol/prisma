package cool.graph.deploy.specutils

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.prisma.errors.{BugsnagErrorReporter, ErrorReporter}
import cool.graph.deploy.DeployDependencies
import cool.graph.deploy.server.DummyClusterAuth
import cool.graph.graphql.GraphQlClient
import cool.graph.messagebus.pubsub.inmemory.InMemoryAkkaPubSub

case class DeployTestDependencies()(implicit val system: ActorSystem, val materializer: ActorMaterializer) extends DeployDependencies {
  override implicit def self: DeployDependencies = this

  implicit val reporter: ErrorReporter = BugsnagErrorReporter(sys.env.getOrElse("BUGSNAG_API_KEY", ""))

  val internalTestDb = new InternalTestDatabase()
  val clientTestDb   = new ClientTestDatabase()

  override lazy val internalDb = internalTestDb.internalDatabase
  override lazy val clientDb   = clientTestDb.clientDatabase

  override lazy val migrator              = TestMigrator(clientDb, internalDb, migrationPersistence)
  override lazy val clusterAuth           = DummyClusterAuth()
  override lazy val invalidationPublisher = InMemoryAkkaPubSub[String]()

  override lazy val graphQlClient = {
    val port = sys.props.getOrElse("STUB_SERVER_PORT", sys.error("No running stub server detected! Can't instantiate GraphQlClient."))
    GraphQlClient(s"http://localhost:$port")
  }
}
