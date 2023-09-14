package sns.fifo.producer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListTopicsRequest;
import software.amazon.awssdk.services.sns.model.ListTopicsResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.Topic;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

public class JsonSNSFIFOProducer {
	
	public static void main(String[] args) {
		String snsFIFOTopic = "";
		if (args[0].endsWith(".fifo")) {
			snsFIFOTopic = args[0];
		} else {
			snsFIFOTopic = args[0].concat(".fifo");
		}
		try {
			JsonSNSFIFOProducer.snsSender(snsFIFOTopic, args[1].concat("-").concat(JsonSNSFIFOProducer.getTodayDate()), Integer.parseInt(args[2]));
		} catch (NumberFormatException e) {
			System.out.println("Pass three parameters in that order - 1 - The Topic Name, 2 - A string to be used as a unique batch identifier, 3 - Total number of messages to send out in this batch");
			e.printStackTrace();
		}
	}
	
	public static void snsSender(String snsTopic, String messageKey, int numberOfMessages) {
		SnsClient snsClient = SnsClient.builder().build();
		String snsTopicARN = JsonSNSFIFOProducer.getTopicARNFromTopicName(snsClient, snsTopic);
		List<String> people = JsonSNSFIFOProducer.readDataFile(); 
		 int numberOfMessagesToSend=0; 
		 if (people.size() > numberOfMessages) { 
			 numberOfMessagesToSend = numberOfMessages; 
		 } else { 
			 numberOfMessagesToSend = people.size(); 
		 }
		 for (int i=1;i<=5;i++) {
		for (int j=1;j<= numberOfMessagesToSend; j++) {
			Person thisPerson = JsonSNSFIFOProducer.getPersonFromLine(people.get(j));
			JsonSNSFIFOProducer.sendMessage(snsClient, snsTopicARN, thisPerson, messageKey, j, i);
		}
		 }
	}
	
	public static String getTopicARNFromTopicName(SnsClient snsClient, String topicName) {
		String topicARN = "";
		try {
            ListTopicsRequest request = ListTopicsRequest.builder()
                   .build();

            ListTopicsResponse result = snsClient.listTopics(request);
            List<Topic> topics = result.topics();
            for (Topic topic: topics) {
            	String currentTopicARN = topic.topicArn();
            	if (currentTopicARN.endsWith(topicARN)) {
            		topicARN = currentTopicARN;
            		break;
            	}
            }

        } catch (SnsException e) {
        	System.err.println("Incorrect Topic Name Specified");
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
		return topicARN;
	}
	
	public static void sendMessage(SnsClient snsClient, String topicARN, Person person, String messageKey, int messageNumber, int repititionNumber) {
		try {
			Map<String, MessageAttributeValue> attributes = new HashMap<String, MessageAttributeValue>();
            attributes.put("MessageKey", MessageAttributeValue.builder().dataType("String").stringValue(messageKey).build());
            attributes.put("MessageNumber", MessageAttributeValue.builder().dataType("String").stringValue(Integer.toString(messageNumber)).build());
            attributes.put("MessageRepititionNumber", MessageAttributeValue.builder().dataType("String").stringValue(Integer.toString(repititionNumber)).build());
            PublishRequest request = PublishRequest.builder()
                .message(person.toJson())
                .topicArn(topicARN)
                .messageAttributes(attributes)
                .subject("Sending Message with Key = " + messageKey + " and message number = " + Integer.toString(messageNumber))
                .messageDeduplicationId(messageKey.concat("-").concat(Integer.toString(messageNumber)))
                .messageGroupId(messageKey.concat("-").concat(person.getState()))
                .build();
            System.out.println("**********************************************************");
            System.out.println("Now going to send one SNS message to topic - " + topicARN);
            System.out.println(
					"Message Key = " + messageKey + " and Message Number = " + messageNumber + " and Repitition Number = " + repititionNumber);
            System.out.println("Message Body = " + person.toJson());
            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());
            System.out.println("Now done sending one SNS message");
            System.out.println("**********************************************************");

         } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
         }
    }
	
	public static List<String> readDataFile() {
		List<String> personList = new ArrayList<String>();
		InputStream is = JsonSNSFIFOProducer.class.getClassLoader().getResourceAsStream("us-500.csv");
		BufferedReader bf = new BufferedReader(new InputStreamReader(is));
		String thisLine = null;
		try {
			thisLine = bf.readLine();
			while (null != thisLine) {
				personList.add(thisLine);
				thisLine = bf.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return personList;
	}
	
	public static Person getPersonFromLine(String line) {
		String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
		Person thisPerson = new Person();
		thisPerson.setFirstname(fields[0]);
		thisPerson.setLastname(fields[1]);
		thisPerson.setCompany(fields[2]);
		thisPerson.setStreet(fields[3]);
		thisPerson.setCity(fields[4]);
		thisPerson.setCounty(fields[5]);
		thisPerson.setState(fields[6]);
		thisPerson.setZip(fields[7]);
		thisPerson.setHomePhone(fields[8]);
		thisPerson.setCellPhone(fields[9]);
		thisPerson.setEmail(fields[10]);
		thisPerson.setWebsite(fields[11]);
		return thisPerson;
	}
	
	public static String getTodayDate() {
		
		LocalDateTime ldt = LocalDateTime.now();
        String formattedDateStr = DateTimeFormatter.ofPattern("MM-dd-YYYY-HH-MM-SS").format(ldt);
        return formattedDateStr;
	}

}
