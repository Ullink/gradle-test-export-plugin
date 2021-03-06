package com.ullink.testtools.elastic

import org.elasticsearch.action.bulk.BulkProcessor
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.gradle.api.logging.Logger

class ElasticSearchProcessor {

    def buildTransportClient(Properties parameters) {
        String clusterName = parameters.getProperty("clusterName", "elasticsearch")
        Settings settings = new Settings.Builder()
                .put("cluster.name", clusterName)
                .build()
        int port = parameters.getProperty("port", "9300") as int
        String ipAddress = parameters.getProperty("host", "127.0.0.1")
        return new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(ipAddress), port))
    }

    def buildBulkRequest(TransportClient client, BulkProcessor.Listener bulkProcessorListener) {
        return BulkProcessor.builder(client, bulkProcessorListener)
                .setBulkActions(200)
                .setFlushInterval(TimeValue.timeValueSeconds(20))
                .build()

    }

    def buildBulkProcessorListener(Logger logger) {
        return new BulkProcessor.Listener() {
            @Override
            void beforeBulk(long executionId, BulkRequest request) {
                logger.info("Execution of bulk ${executionId} ${request.description} starting")
            }

            @Override
            void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                def failures = response.items.findAll { it.failure }
                if (failures.size()) {
                    logger.info("Execution of bulk ${executionId} ${request.description} completed with errors")
                    for (def failure : failures) {
                        logger.error("An error occurred while executing item ${failure.failure.id}: ${failure.failure.status}: ${failure.failure.message}", failure.failure.cause)
                    }
                } else {
                    logger.info("Execution of bulk ${executionId} ${request.description} completed successfully")
                }
            }

            @Override
            void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                logger.error("Execution of bulk ${executionId} ${request.description} failed", failure)
            }
        }
    }
}
