package com.arquivei.event_router

import java.util.Properties

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.util.Try


object Main extends App {
  val readTopic = sys.env("READ_TOPIC")
  val writeTopic = sys.env("WRITE_TOPIC_PREFIX")
  val config: Properties = {
    val application_id = sys.env("APPLICATION_ID")
    val bootstrap_servers = sys.env("BOOTSTRAP_SERVERS")
    val user = sys.env("SASL_USERNAME")
    val pass = sys.env("SASL_PASSWORD")
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, application_id)
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers)
    p.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT")
    p.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip")
    p.put(SaslConfigs.SASL_JAAS_CONFIG, s"""org.apache.kafka.common.security.plain.PlainLoginModule required username="$user" password="$pass";""")
    p.put(SaslConfigs.SASL_MECHANISM, "PLAIN")
    p.put("auto.offset.reset", "earliest")
    p
  }

  val builder = new StreamsBuilder()

  builder.stream[String, String](readTopic)
    .to((_, value, _) => topicName(Try(parse(value)).getOrElse(JNull)))

  val topology = builder.build()
  val streams = new KafkaStreams(topology, config)
  streams.start()


  implicit val defaultFormats = DefaultFormats

  def topicName(event: JValue): String = {
    val Source = (event \ "Source").extractOpt[String]
    val Type = (event \ "Type").extractOpt[String]
    if (Source.isEmpty || Type.isEmpty) {
      s"$writeTopic.fallback_topic"
    } else {
      s"$writeTopic.${Source.get}_${Type.get}"
    }
  }
}

