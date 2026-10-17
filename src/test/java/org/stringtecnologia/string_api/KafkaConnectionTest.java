package org.stringtecnologia.string_api;


import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaConnectionTest {

    @Test
    void validarConexaoKafka() throws Exception {

        String brokers = String.join(",",
                "kafka-cluster-dual-role-0.kafka-cluster-kafka-brokers.prod.svc:9092",
                "kafka-cluster-dual-role-1.kafka-cluster-kafka-brokers.prod.svc:9092",
                "kafka-cluster-dual-role-2.kafka-cluster-kafka-brokers.prod.svc:9092"
        );

        Properties properties = new Properties();

        properties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                brokers
        );

        properties.put(
                AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG,
                10000
        );

        properties.put(
                AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG,
                15000
        );

        try (AdminClient admin = AdminClient.create(properties)) {

            var cluster = admin.describeCluster();

            System.out.println("Cluster ID: "
                    + cluster.clusterId().get(15, TimeUnit.SECONDS));

            var nodes = cluster.nodes().get(15, TimeUnit.SECONDS);

            nodes.forEach(node ->
                    System.out.printf(
                            "Broker ID=%d HOST=%s PORT=%d%n",
                            node.id(),
                            node.host(),
                            node.port()
                    )
            );

            var topics = admin.describeTopics(
                    List.of(
                            "ocr.imagens.dev",
                            "ocr.imagens.dev.dlt"
                    )
            ).allTopicNames().get(15, TimeUnit.SECONDS);

            topics.forEach((nome, descricao) ->
                    System.out.printf(
                            "TOPICO=%s PARTICOES=%d%n",
                            nome,
                            descricao.partitions().size()
                    )
            );
        }
    }
}
