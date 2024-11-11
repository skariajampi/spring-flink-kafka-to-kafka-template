package org.example.serde;

import org.apache.avro.specific.SpecificRecord;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;

public class FlinkKafkaAvroSerialization<K extends SpecificRecord, V extends SpecificRecord>
        implements KafkaRecordSerializationSchema<Tuple2<K, V>> {

    private static final String SUBJECT_KEY_SUFFIX = "-key";
    private static final String SUBJECT_VALUE_SUFFIX = "-value";
    private final TypeInformation<Tuple2<K, V>> typeInformation;
    private final SerializationSchema<K> serializationSchemaKey;
    private final SerializationSchema<V> serializationSchemaValue;
    private final String topic;

    public FlinkKafkaAvroSerialization(Class<K> keyClass, Class<V> valueClass, String schemaRegistryUrl, String topic) {
        this.topic = topic;
        this.typeInformation = new TupleTypeInfo<>(TypeInformation.of(keyClass), TypeInformation.of(valueClass));
        serializationSchemaKey = ConfluentRegistryAvroSerializationSchema.forSpecific(keyClass, getKeySubject(topic), schemaRegistryUrl);
        serializationSchemaValue = ConfluentRegistryAvroSerializationSchema.forSpecific(valueClass, getValueSubject(topic), schemaRegistryUrl);
    }

    @Override
    public void open(SerializationSchema.InitializationContext context, KafkaSinkContext sinkContext) throws Exception {
        KafkaRecordSerializationSchema.super.open(context, sinkContext);
    }

    @Nullable
    @Override
    public ProducerRecord<byte[], byte[]> serialize(Tuple2<K, V> kvTuple2, KafkaSinkContext kafkaSinkContext, Long aLong) {
        return new ProducerRecord<>(topic, serializationSchemaKey.serialize(kvTuple2.f0), serializationSchemaValue.serialize(kvTuple2.f1));
    }

    private String getKeySubject(String topic){
        return topic + SUBJECT_KEY_SUFFIX;
    }

    private String getValueSubject(String topic){
        return topic + SUBJECT_VALUE_SUFFIX;
    }

}

