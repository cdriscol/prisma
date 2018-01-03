package cool.graph.api.mutations

import cool.graph.api.ApiBaseSpec
import cool.graph.gc_values.DateTimeGCValue
import cool.graph.shared.models.Project
import cool.graph.shared.project_dsl.SchemaDsl
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{FlatSpec, Matchers}

class TransactionalNestedExecutionSpec extends FlatSpec with Matchers with ApiBaseSpec {

  //At the moment we are only inserting the inner where, the outer condition is checked s

  //Test Where
  // - multiple where's nested
  //Test the parsing of the exception for different datatypes
  // - put a catch all handling on it in the end?
  //

  //Implement Relation
  //Test Relation

  "a one to one relation" should "fail gracefully on wrong STRING where and assign error correctly and not execute partially" in {

    val outerWhere = """"Outer Unique""""
    val innerWhere = """"Inner Unique""""
    val falseWhere = """"False  Where""""
    val falseWhereInError = """False  Where"""

    val project = SchemaDsl() { schema =>
      val note = schema.model("Note").field("outerString", _.String).field("outerUnique", _.String, isUnique = true)
      schema.model("Todo").field_!("innerString", _.String).field("innerUnique", _.String, isUnique = true).oneToOneRelation("note", "todo", note)
    }
    database.setup(project)

    verifyTransactionalExecutionAndErrorMessage(outerWhere, innerWhere, falseWhere, falseWhereInError, project)
  }

  "a one to one relation" should "fail gracefully on wrong INT where and assign error correctly and not execute partially" in {

    val outerWhere = 1
    val innerWhere = 2
    val falseWhere = 3
    val falseWhereInError = 3

    val project = SchemaDsl() { schema =>
      val note = schema.model("Note").field("outerString", _.String).field("outerUnique", _.Int, isUnique = true)
      schema.model("Todo").field_!("innerString", _.String).field("innerUnique", _.Int, isUnique = true).oneToOneRelation("note", "todo", note)
    }
    database.setup(project)

    verifyTransactionalExecutionAndErrorMessage(outerWhere, innerWhere, falseWhere, falseWhereInError, project)
  }

  "a one to one relation" should "fail gracefully on wrong FLOAT where and assign error correctly and not execute partially" in {

    val outerWhere = 1.0
    val innerWhere = 2.0
    val falseWhere = 3.0
    val falseWhereInError = 3.0

    val project = SchemaDsl() { schema =>
      val note = schema.model("Note").field("outerString", _.String).field("outerUnique", _.Float, isUnique = true)
      schema.model("Todo").field_!("innerString", _.String).field("innerUnique", _.Float, isUnique = true).oneToOneRelation("note", "todo", note)
    }
    database.setup(project)

    verifyTransactionalExecutionAndErrorMessage(outerWhere, innerWhere, falseWhere, falseWhereInError, project)
  }

  "a one to one relation" should "fail gracefully on wrong BOOLEAN = FALSE where and assign error correctly and not execute partially" in {

    val outerWhere = true
    val innerWhere = true
    val falseWhere = false
    val falseWhereInError = false

    val project = SchemaDsl() { schema =>
      val note = schema.model("Note").field("outerString", _.String).field("outerUnique", _.Boolean, isUnique = true)
      schema.model("Todo").field_!("innerString", _.String).field("innerUnique", _.Boolean, isUnique = true).oneToOneRelation("note", "todo", note)
    }
    database.setup(project)

    verifyTransactionalExecutionAndErrorMessage(outerWhere, innerWhere, falseWhere, falseWhereInError, project)
  }

  "a one to one relation" should "fail gracefully on wrong BOOLEAN = TRUE where and assign error correctly and not execute partially" in {

    val outerWhere = false
    val innerWhere = false
    val falseWhere = true
    val falseWhereInError = true

    val project = SchemaDsl() { schema =>
      val note = schema.model("Note").field("outerString", _.String).field("outerUnique", _.Boolean, isUnique = true)
      schema.model("Todo").field_!("innerString", _.String).field("innerUnique", _.Boolean, isUnique = true).oneToOneRelation("note", "todo", note)
    }
    database.setup(project)

    verifyTransactionalExecutionAndErrorMessage(outerWhere, innerWhere, falseWhere, falseWhereInError, project)
  }

  "a one to one relation" should "fail gracefully on wrong GRAPHQLID where and assign error correctly and not execute partially" in {

    val outerWhere = """"Some Outer ID""""
    val innerWhere = """"Some Inner ID""""
    val falseWhere = """"Some False ID""""
    val falseWhereInError = "Some False ID"

    val project = SchemaDsl() { schema =>
      val note = schema.model("Note").field("outerString", _.String).field("outerUnique", _.GraphQLID, isUnique = true)
      schema.model("Todo").field_!("innerString", _.String).field("innerUnique", _.GraphQLID, isUnique = true).oneToOneRelation("note", "todo", note)
    }
    database.setup(project)

    verifyTransactionalExecutionAndErrorMessage(outerWhere, innerWhere, falseWhere, falseWhereInError, project)
  }

  "a one to one relation" should "fail gracefully on wrong ENUM where and assign error correctly and not execute partially" in {

    val outerWhere = "A"
    val innerWhere = "B"
    val falseWhere = "C"
    val falseWhereInError = "C"

    val project = SchemaDsl() { schema =>
      val enum = schema.enum("SomeEnum", Vector("A", "B", "C"))
      val note = schema.model("Note").field("outerString", _.String).field("outerUnique", _.Enum, enum = Some(enum) ,isUnique = true)
      schema.model("Todo").field_!("innerString", _.String).field("innerUnique", _.Enum, enum = Some(enum) , isUnique = true).oneToOneRelation("note", "todo", note)

    }
    database.setup(project)

    verifyTransactionalExecutionAndErrorMessage(outerWhere, innerWhere, falseWhere, falseWhereInError, project)
  }

  "a one to one relation" should "fail gracefully on wrong DateTime where and assign error correctly and not execute partially" ignore {
    //date time is tricky since the shape is transformed
    //I would expect the where to find stuff if I use the same shape that I entered
    //OutwardFacing we use ISO8601
    //SQL needs a different format for the where queries and the errorparsing
    //we also accept shortened ISO8601 versions and change extend them internally

    val outerWhere = """"2018""""
    val innerWhere = """"2019""""
    val falseWhere = """"2020""""
    val falseWhereInError = DateTimeGCValue(new DateTime("2020", DateTimeZone.UTC)).toMySqlDateTimeFormat

    val project = SchemaDsl() { schema =>
      val note = schema.model("Note").field("outerString", _.String).field("outerUnique", _.DateTime ,isUnique = true)
      schema.model("Todo").field_!("innerString", _.String).field("innerUnique", _.DateTime, isUnique = true).oneToOneRelation("note", "todo", note)

    }
    database.setup(project)

    verifyTransactionalExecutionAndErrorMessage(outerWhere, innerWhere, falseWhere, falseWhereInError, project)
  }

  "a one to one relation" should "fail gracefully on wrong JSON where and assign error correctly and not execute partially" ignore {
    //we're mixing play and spray jsons all over the place which messes up pattern matches

    val outerWhere = """"{\"a\": \"a\"}""""
    val innerWhere = """"{\"a\": \"b\"}""""
    val falseWhere = """"{\"a\": \"c\"}""""
    val falseWhereInError = """"{\"a\": \"c\"}""""

    val project = SchemaDsl() { schema =>
      val note = schema.model("Note").field("outerString", _.String).field("outerUnique", _.Json,isUnique = true)
      schema.model("Todo").field_!("innerString", _.String).field("innerUnique", _.Json, isUnique = true).oneToOneRelation("note", "todo", note)

    }
    database.setup(project)

    verifyTransactionalExecutionAndErrorMessage(outerWhere, innerWhere, falseWhere, falseWhereInError, project)
  }


  "a many2many relation" should "fail gracefully on wrong GRAPHQLID for multiple nested wheres" in {

    val outerWhere = """"Some Outer ID""""
    val innerWhere = """"Some Inner ID""""
    val innerWhere2 = """"Some Inner ID2""""
    val falseWhere = """"Some False ID""""
    val falseWhere2 = """"Some False ID2""""
    val falseWhereInError = "Some False ID"
    val falseWhereInError2 = "Some False ID2"


    val project = SchemaDsl() { schema =>
      val note = schema.model("Note").field("outerString", _.String).field("outerUnique", _.GraphQLID, isUnique = true)
      schema.model("Todo").field_!("innerString", _.String).field("innerUnique", _.GraphQLID, isUnique = true).manyToManyRelation("notes", "todos", note)
    }
    database.setup(project)

    val createResult = server.executeQuerySimple(
      s"""mutation {
         |  createNote(
         |    data: {
         |      outerString: "Outer String"
         |      outerUnique: $outerWhere
         |      todos: {
         |        create: [
         |        {innerString: "Inner String", innerUnique: $innerWhere},
         |        {innerString: "Inner String", innerUnique: $innerWhere2}
         |        ]
         |      }
         |    }
         |  ){
         |    id
         |  }
         |}""".stripMargin,
      project
    )

    server.executeQuerySimpleThatMustFail(
      s"""
         |mutation {
         |  updateNote(
         |    where: { outerUnique: $outerWhere }
         |    data: {
         |      outerString: "Changed Outer String"
         |      todos: {
         |        update: [
         |        {where: { innerUnique: $innerWhere },data:{ innerString: "Changed Inner String"}},
         |        {where: { innerUnique: $falseWhere2 },data:{ innerString: "Changed Inner String"}}
         |        ]
         |      }
         |    }
         |  ){
         |    id
         |  }
         |}
      """.stripMargin,
      project,
      errorCode = 3039,
      errorContains = s"No Node for the model Todo with value $falseWhereInError2 for innerUnique found."
    )

    server.executeQuerySimple(s"""query{note(where:{outerUnique:$outerWhere}){outerString}}""", project, dataContains = s"""{"note":{"outerString":"Outer String"}}""")
    server.executeQuerySimple(s"""query{todo(where:{innerUnique:$innerWhere}){innerString}}""", project, dataContains = s"""{"todo":{"innerString":"Inner String"}}""")
    server.executeQuerySimple(s"""query{todo(where:{innerUnique:$innerWhere2}){innerString}}""", project, dataContains = s"""{"todo":{"innerString":"Inner String"}}""")


    server.executeQuerySimpleThatMustFail(
      s"""
         |mutation {
         |  updateNote(
         |    where: { outerUnique: $outerWhere }
         |    data: {
         |      outerString: "Changed Outer String"
         |      todos: {
         |        update: [
         |        {where: { innerUnique: $falseWhere},data:{ innerString: "Changed Inner String"}},
         |        {where: { innerUnique: $innerWhere2 },data:{ innerString: "Changed Inner String"}}
         |        ]
         |      }
         |    }
         |  ){
         |    id
         |  }
         |}
      """.stripMargin,
      project,
      errorCode = 3039,
      errorContains = s"No Node for the model Todo with value $falseWhereInError for innerUnique found."
    )

    server.executeQuerySimple(s"""query{note(where:{outerUnique:$outerWhere}){outerString}}""", project, dataContains = s"""{"note":{"outerString":"Outer String"}}""")
    server.executeQuerySimple(s"""query{todo(where:{innerUnique:$innerWhere}){innerString}}""", project, dataContains = s"""{"todo":{"innerString":"Inner String"}}""")
    server.executeQuerySimple(s"""query{todo(where:{innerUnique:$innerWhere2}){innerString}}""", project, dataContains = s"""{"todo":{"innerString":"Inner String"}}""")
  }


  private def verifyTransactionalExecutionAndErrorMessage(outerWhere: Any, innerWhere: Any, falseWhere: Any, falseWhereInError: Any, project: Project) = {
    val createResult = server.executeQuerySimple(
      s"""mutation {
         |  createNote(
         |    data: {
         |      outerString: "Outer String"
         |      outerUnique: $outerWhere
         |      todo: {
         |        create: {
         |         innerString: "Inner String"
         |         innerUnique: $innerWhere
         |         }
         |      }
         |    }
         |  ){
         |    id
         |  }
         |}""".stripMargin,
      project
    )

    server.executeQuerySimpleThatMustFail(
      s"""
         |mutation {
         |  updateNote(
         |    where: { outerUnique: $outerWhere }
         |    data: {
         |      outerString: "Changed Outer String"
         |      todo: {
         |        update: {
         |          where: { innerUnique: $falseWhere },
         |          data:{ innerString: "Changed Inner String" }
         |        }
         |      }
         |    }
         |  ){
         |    id
         |  }
         |}
      """.stripMargin,
      project,
      errorCode = 3039,
      errorContains = s"No Node for the model Todo with value $falseWhereInError for innerUnique found."
    )

    server.executeQuerySimple(s"""query{note(where:{outerUnique:$outerWhere}){outerString}}""", project, dataContains = s"""{"note":{"outerString":"Outer String"}}""")
    server.executeQuerySimple(s"""query{todo(where:{innerUnique:$innerWhere}){innerString}}""", project, dataContains = s"""{"todo":{"innerString":"Inner String"}}""")

    server.executeQuerySimpleThatMustFail(
      s"""
         |mutation {
         |  updateNote(
         |    where: { outerUnique: $falseWhere }
         |    data: {
         |      outerString: "Changed Outer String"
         |      todo: {
         |        update: {
         |          where: { innerUnique: $innerWhere },
         |          data:{ innerString: "Changed Inner String" }
         |        }
         |      }
         |    }
         |  ){
         |    id
         |  }
         |}
      """.stripMargin,
      project,
      errorCode = 3039,
      errorContains = s"No Node for the model Note with value $falseWhereInError for outerUnique found."
    )

    server.executeQuerySimple(s"""query{note(where:{outerUnique:$outerWhere}){outerString}}""", project, dataContains = s"""{"note":{"outerString":"Outer String"}}""")
    server.executeQuerySimple(s"""query{todo(where:{innerUnique:$innerWhere}){innerString}}""", project, dataContains = s"""{"todo":{"innerString":"Inner String"}}""")
  }
}
