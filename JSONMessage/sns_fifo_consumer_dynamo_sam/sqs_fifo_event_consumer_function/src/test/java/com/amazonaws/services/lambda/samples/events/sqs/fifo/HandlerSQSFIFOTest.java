package com.amazonaws.services.lambda.samples.events.sqs.fifo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

class HandlerSQSFIFOTest {
	private static final String sqsEventJson = "{\n"
			+ "    \"records\": [\n"
			+ "        {\n"
			+ "            \"messageId\": \"0c48de76-e8b3-42be-8215-132d2ffbec72\",\n"
			+ "            \"receiptHandle\": \"AQEBhAqVrcIYmq1BEvsE9+h43x7GAOC1Oo+jgca24bwI00xH2AUeINK3wT9pXhZ/asx3MNbZl1HQ447BW96YEHschIIF7WzFKB9U61FSptfdFxn5/C6KYDQyyBjiHysRDaAv0pX0INnq7zBqExwAfX/eFAkebzdUcNfu8/OKSN11EPiwSBkkhx+k+peNiSPT/J2KiA5VC8tKcH/DFO0fqZcX7LRwqVZEMnn30n0Nrxj4gMMLYPW5NbDbUXHXkFepeGsD0p1o77umprJfIC/5h7Drd+Galif6fHDZuWARqZROkppewEOmR6azyHQ8G7h4BNex\",\n"
			+ "            \"body\": \"{\\\"firstname\\\":\\\"Minna\\\",\\\"lastname\\\":\\\"Amigon\\\",\\\"company\\\":\\\"\\\\\\\"Dorl, James J Esq\\\\\\\"\\\",\\\"street\\\":\\\"2371 Jerrold Ave\\\",\\\"city\\\":\\\"Kulpsville\\\",\\\"county\\\":\\\"Montgomery\\\",\\\"state\\\":\\\"PA\\\",\\\"zip\\\":\\\"19443\\\",\\\"homePhone\\\":\\\"215-874-1229\\\",\\\"cellPhone\\\":\\\"215-422-8694\\\",\\\"email\\\":\\\"minna_amigon@yahoo.com\\\",\\\"website\\\":\\\"http://www.dorljamesjesq.com\\\"}\",\n"
			+ "            \"md5OfBody\": \"1ef18686545824284979f91d7333ac58\",\n"
			+ "            \"md5OfMessageAttributes\": \"6d0df3ec8b83b2d737dd4ba3076f7f54\",\n"
			+ "            \"eventSourceArn\": \"arn:aws:sqs:us-west-2:664251831272:LambdaSNSToSQSFIFOQueueDynamoJavaSAM.fifo\",\n"
			+ "            \"eventSource\": \"aws:sqs\",\n"
			+ "            \"awsRegion\": \"us-west-2\",\n"
			+ "            \"attributes\": {\n"
			+ "                \"ApproximateReceiveCount\": \"1\",\n"
			+ "                \"SentTimestamp\": \"1689977590584\",\n"
			+ "                \"SequenceNumber\": \"18879378336899055872\",\n"
			+ "                \"MessageGroupId\": \"TestMessage13-07-21-2023-22-07-31-PA\",\n"
			+ "                \"SenderId\": \"AIDASRT3EJNNEBNPWAHTG\",\n"
			+ "                \"MessageDeduplicationId\": \"TestMessage13-07-21-2023-22-07-31-10\",\n"
			+ "                \"ApproximateFirstReceiveTimestamp\": \"1689977590615\"\n"
			+ "            },\n"
			+ "            \"messageAttributes\": {\n"
			+ "                \"MessageKey\": {\n"
			+ "                    \"stringValue\": \"TestMessage13-07-21-2023-22-07-31\",\n"
			+ "                    \"binaryValue\": null,\n"
			+ "                    \"stringListValues\": [],\n"
			+ "                    \"binaryListValues\": [],\n"
			+ "                    \"dataType\": \"String\"\n"
			+ "                },\n"
			+ "                \"MessageNumber\": {\n"
			+ "                    \"stringValue\": \"10\",\n"
			+ "                    \"binaryValue\": null,\n"
			+ "                    \"stringListValues\": [],\n"
			+ "                    \"binaryListValues\": [],\n"
			+ "                    \"dataType\": \"String\"\n"
			+ "                }\n"
			+ "            }\n"
			+ "        },\n"
			+ "        {\n"
			+ "            \"messageId\": \"8d5704f2-ecd7-4a0c-9e0a-9476ae10936d\",\n"
			+ "            \"receiptHandle\": \"AQEBz9e1nAEwkZ+sFcNbZiYkEg8kVwYzuNkHYnWMWfp53vSGAF+OTxEbTKgr0QfkEmqaL2aXRnF1xxcrjBbhTXk7W7KPiKmtl+8G06r8S/Ao1ECC7g3q9o6reShSdZzV1XyGxqYlG6TMfwG1wy0XxrGvp/Tt/iCNYVbL3ZSeKHk47ahaCZDSFUyR7LNWB0hzzdFxTHXRTGvR9PzRXSt9MKlGGOSGUPdU1wdl6c1W/TQwMIbsNCLGOObWamJYk8koKxezTyJYyJ8ziZUhd2gWNUKKJDB3el2fMaJRes0OcWjkEwRNLncZYO7r8pnreBOAPZ+V\",\n"
			+ "            \"body\": \"{\\\"firstname\\\":\\\"Abel\\\",\\\"lastname\\\":\\\"Maclead\\\",\\\"company\\\":\\\"Rangoni Of Florence\\\",\\\"street\\\":\\\"37275 St  Rt 17m M\\\",\\\"city\\\":\\\"Middle Island\\\",\\\"county\\\":\\\"Suffolk\\\",\\\"state\\\":\\\"NY\\\",\\\"zip\\\":\\\"11953\\\",\\\"homePhone\\\":\\\"631-335-3414\\\",\\\"cellPhone\\\":\\\"631-677-3675\\\",\\\"email\\\":\\\"amaclead@gmail.com\\\",\\\"website\\\":\\\"http://www.rangoniofflorence.com\\\"}\",\n"
			+ "            \"md5OfBody\": \"e0113d8a717033df6d10ab8ab9515437\",\n"
			+ "            \"md5OfMessageAttributes\": \"b7f954435d30f026606188ca44d3c627\",\n"
			+ "            \"eventSourceArn\": \"arn:aws:sqs:us-west-2:664251831272:LambdaSNSToSQSFIFOQueueDynamoJavaSAM.fifo\",\n"
			+ "            \"eventSource\": \"aws:sqs\",\n"
			+ "            \"awsRegion\": \"us-west-2\",\n"
			+ "            \"attributes\": {\n"
			+ "                \"ApproximateReceiveCount\": \"1\",\n"
			+ "                \"SentTimestamp\": \"1689977590605\",\n"
			+ "                \"SequenceNumber\": \"18879378336904432384\",\n"
			+ "                \"MessageGroupId\": \"TestMessage13-07-21-2023-22-07-31-NY\",\n"
			+ "                \"SenderId\": \"AIDASRT3EJNNEBNPWAHTG\",\n"
			+ "                \"MessageDeduplicationId\": \"TestMessage13-07-21-2023-22-07-31-11\",\n"
			+ "                \"ApproximateFirstReceiveTimestamp\": \"1689977590615\"\n"
			+ "            },\n"
			+ "            \"messageAttributes\": {\n"
			+ "                \"MessageKey\": {\n"
			+ "                    \"stringValue\": \"TestMessage13-07-21-2023-22-07-31\",\n"
			+ "                    \"binaryValue\": null,\n"
			+ "                    \"stringListValues\": [],\n"
			+ "                    \"binaryListValues\": [],\n"
			+ "                    \"dataType\": \"String\"\n"
			+ "                },\n"
			+ "                \"MessageNumber\": {\n"
			+ "                    \"stringValue\": \"11\",\n"
			+ "                    \"binaryValue\": null,\n"
			+ "                    \"stringListValues\": [],\n"
			+ "                    \"binaryListValues\": [],\n"
			+ "                    \"dataType\": \"String\"\n"
			+ "                }\n"
			+ "            }\n"
			+ "        }\n"
			+ "    ]\n"
			+ "}";

	@Mock
	DynamoDBUpdater ddbUpdater;	
	
	@Test
	@ExtendWith(MockitoExtension.class)
	void invokeTest() {
		
		ObjectMapper om = new ObjectMapper();
		//SQSEvent event = gson.fromJson(sqsEventJson, SQSEvent.class);
		SQSEvent event = null;
		try {
			event = om.readValue(sqsEventJson, SQSEvent.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		Context context = new TestContext();
		DynamoDBUpdater dbUpdater = mock(DynamoDBUpdater.class);
		HandlerSQSFIFO handler = new HandlerSQSFIFO();
		handler.ddbUpdater = dbUpdater;
		SQSBatchResponse result = handler.handleRequest(event, context);
		assertEquals(result.getBatchItemFailures().size(), 0);
	}

}
