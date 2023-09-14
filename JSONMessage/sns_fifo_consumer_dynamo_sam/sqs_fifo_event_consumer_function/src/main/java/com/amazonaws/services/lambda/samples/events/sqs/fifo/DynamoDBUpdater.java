package com.amazonaws.services.lambda.samples.events.sqs.fifo;


import java.util.Map;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.MessageAttribute;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

public class DynamoDBUpdater {

	String dynamoDBTableName;
	AmazonDynamoDB client;
	DynamoDB dynamoDB;
	Table dynamoTable;
	

	public DynamoDBUpdater(String dynamoDBTableName) {
		super();
		if (null == dynamoDBTableName) {
			this.dynamoDBTableName = "SQS_LAMBDA_DYNAMO_TABLE";
		} else {
			this.dynamoDBTableName = dynamoDBTableName;
		}
		String AWS_SAM_LOCAL = System.getenv("AWS_SAM_LOCAL");
		if (null == AWS_SAM_LOCAL) {
			this.client = AmazonDynamoDBClientBuilder.standard().build();
		} else {
			this.client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(new EndpointConfiguration("http://127.0.0.1:8000", "")).build();
			this.dynamoDBTableName = "SQS_LAMBDA_DYNAMO_TABLE";
		}
		//this.client = AmazonDynamoDBClientBuilder.standard().build();
		this.dynamoDB = new DynamoDB(client);
		this.dynamoTable = dynamoDB.getTable(this.dynamoDBTableName);
	}
	
	public PutItemOutcome insertIntoDynamoDB(SQSMessage msg, Person thisPerson, LambdaLogger logger, long batchReceiptTime, String lambdaMicroVMID) {
		logger.log("Now inserting a row in DynamoDB for messageID = " + msg.getMessageId());
		Item item = new Item();
		item.withPrimaryKey("MessageID", msg.getMessageId());
		item.withString("LambdaMicroVMID", lambdaMicroVMID);
		item.withLong("BatchReceiptTime", batchReceiptTime);
		item.withString("ReceiptHandle", msg.getReceiptHandle());
		item.withString("EventSourceARN", msg.getEventSourceArn());
		item.withString("EventSource", msg.getEventSource());
		item.withString("AWSRegion", msg.getAwsRegion());
		item.withString("MD5OfBody", msg.getMd5OfBody());
		item.withString("Firstname", thisPerson.getFirstname());
		item.withString("Lastname", thisPerson.getLastname());
		item.withString("Company", thisPerson.getCompany());
		item.withString("Address_Street", thisPerson.getStreet());
		item.withString("City", thisPerson.getCity());
		item.withString("County", thisPerson.getCounty());
		item.withString("State", thisPerson.getState());
		item.withString("Zip", thisPerson.getZip());
		item.withString("Home_Phone", thisPerson.getHomePhone());
		item.withString("Cell_Phone", thisPerson.getCellPhone());
		item.withString("Email", thisPerson.getEmail());
		item.withString("Website", thisPerson.getWebsite());
		Map<String, String> attributes = msg.getAttributes();
	      attributes.forEach((k,v) -> {
	    	  item.withString(k, v);
	      });
		item.withString("Md5OfMessageAttributes", msg.getMd5OfMessageAttributes());
		Map<String, MessageAttribute> messageAttributes = msg.getMessageAttributes();
	      messageAttributes.forEach((k,v) -> {
	    	  item.withString(k, v.getStringValue());
	      });
	    logger.log("Now done inserting a row in DynamoDB for messageID = " + msg.getMessageId());
		return dynamoTable.putItem(item);
	}
	
}
