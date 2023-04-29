import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.VideoGame;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

public class ProducerExample {
    public static final String DELIMITER = ",";
    public static final String BOOTSTRAP_SERVERS = "127.0.0.1:9092";

    public static void main(String[] args) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<VideoGame> records = new ArrayList<VideoGame>();
        URL resourceFile = ProducerExample.class.getResource("dataset/vgsales.csv");
        BufferedReader br = new BufferedReader(new FileReader(resourceFile.getFile()));
        br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(DELIMITER);
            try{
                records.add(createVideoGame(values));
            }catch (Exception e){
                //IF there is an exception adding the record just ignore it
            }


        }
        KafkaProducer<String, String> producer = producerFactory();
        records.stream().forEach((videoGame) -> {
            System.out.println(videoGame.toString());
            ObjectMapper mapper = new ObjectMapper();
            try {
                ProducerRecord<String, String> record=new ProducerRecord<String, String>("test", mapper.writeValueAsString(videoGame));
                producer.send(record);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }
    //Dataset provides sales in millions
    public static VideoGame createVideoGame(String[] values){
        return new VideoGame(Integer.parseInt(values[0]), values[1],values[2],
                (Integer.parseInt(values[3])),values[5],Float.parseFloat(values[6]),
                (Float.parseFloat(values[7])),Float.parseFloat(values[8]),Float.parseFloat(values[9]));
    }

    public static KafkaProducer<String, String> producerFactory(){
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<String, String>(properties);
    }
}
