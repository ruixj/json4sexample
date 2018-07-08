package com.tr.adc.util

import org.apache.avro.Schema
import org.json4s.{JNull, JValue}
import org.json4s.JsonAST._
import org.json4s.jackson.Json
import org.json4s.jackson.JsonMethods._

import scala.io.Source
import scala.collection.mutable.ListBuffer

/**
  * Created by u0129225 on 7/27/2016.
  */
object Json4sUtil {
  implicit val formats = org.json4s.DefaultFormats

  def MapFromJson(jsonStr: String) = {
    parse(jsonStr).extract[Map[String, _]]
  }

  def JObjectFromJson(jsonStr: String): JValue = {
    parse(jsonStr)
  }

  def toJson(v: Map[String, Any]): String = {
    Json(org.json4s.DefaultFormats).write(v)
  }

  def filterJVal(jval: JValue, filter:Map[String,Int]): JValue = {
    jval match {
      case JObject(jobj) =>
        val transObj = jobj.map{ p =>
          if( filter.contains(p._1))
            (p._1, JNothing)
          else
            (p._1, filterJVal(p._2,filter))
        }

        JObject(transObj)
      case JArray(jarr) =>
        val transArr = jarr.map{ obj => filterJVal(obj,filter) }
        JArray(transArr)
      case _ => jval
    }
  }

  def map2JsonStr(typemap:Map[String,Any]): String = {


    typemap.foreach( e =>
    {
        e._2 match {
          case map: Map[String,Any] =>
            val recordList = ListBuffer[(String,JValue)](("type",JString("record")))
            recordList += (("name",JString(e._1)))

            map2JsonStr(e._2.asInstanceOf[Map[String,Any]])
          case _ =>

        }
    })

    ""



  }
  def main(args: Array[String]): Unit = {
    ///home/ruixj/work/code/testjson4s/src/main/scala
    val jsonstr = Source.fromFile("/home/ruixj/work/code/testjson4s/src/main/scala/test.json" ).mkString
    println( jsonstr)
    val json = JObjectFromJson(jsonstr)
    val filterMap = Map("namespace" -> 1)
    val newjson = filterJVal(json,filterMap)

    println(compact(render(newjson)))

  }
}

