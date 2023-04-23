import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Language;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ProducerExample {
    public static final String COMMA_DELIMITER = ",";
    public static final String BOOTSTRAP_SERVERS = "127.0.0.1:9092";

    public static void main(String[] args) throws IOException {
        List<Language> records = new ArrayList<Language>();
        URL resourceFile = ProducerExample.class.getResource("dataset/issues.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(resourceFile.getFile()))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                records.add(new Language(values[0],Integer.parseInt(values[1]),Integer.parseInt(values[2]),Integer.parseInt(values[3])));
            }
        }

        KafkaProducer<String, String> producer = producerFactory();
        records.stream().forEach((language) -> {
            System.out.println(language.toString());

            ObjectMapper mapper = new ObjectMapper();
            try {

                ProducerRecord<String, String> record=new ProducerRecord<String, String>("test", mapper.writeValueAsString(language));
                producer.send(record);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });

    }

    public static KafkaProducer<String, String> producerFactory(){
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<String, String>(properties);
    }
}
