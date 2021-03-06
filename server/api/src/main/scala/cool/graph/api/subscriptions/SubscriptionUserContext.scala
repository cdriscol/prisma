package cool.graph.api.subscriptions

import cool.graph.shared.models.Project
import sangria.ast.Document

case class SubscriptionUserContext(
    nodeId: String,
    project: Project,
    requestId: String,
    log: Function[String, Unit],
    queryAst: Option[Document] = None
)
