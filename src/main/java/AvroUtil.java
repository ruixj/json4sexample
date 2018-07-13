/////////////////////////////////////////////////////////
//
// File: AvroUtil.java
// Description: Source code of Avro utility in data norm
//
//
// Copyright (c) 20115 by Thomson Reuters. All rights reserved.
//
// No portion of this software in any form may be used or
// reproduced in any manner without written consent from
// Thomson Reuters
//




import org.apache.avro.Schema;
import org.apache.avro.generic.*;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.io.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class AvroUtil {

    public static Schema toSchema(String json) {
        Schema schema = new Schema.Parser().parse(json);
        return schema;
    }

    public static String toJson(GenericRecord data) {
        String js = data.toString();
        return js;
    }

    public static String toJson(GenericRecord data, boolean isPretty) {
        String js = toJson(data);
        if(isPretty) {
            return JsonUtil.prettifyJson(js);
        } else {
            return js;
        }
    }


    public static Object sel(GenericRecord record, String path) {
        String[] strs = path.split("\\.");
        Object r = record;
        for(String str : strs) {
            r = ((GenericRecord)r).get(str);
        }
        return r;
    }


    public static byte[] encode(Schema schema, GenericRecord record) throws IOException {
        //if record is null, return empty byte[]
        if(record == null)
            return "".getBytes();

        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(record, encoder);
        encoder.flush();
        out.flush();
        return out.toByteArray();
    }

    public static Record decode(Schema schema, byte[] bytes) throws IOException {
        Record record = decode(schema, schema, bytes);
        return record;
    }

    public static Record decode(Schema schema, byte[] bytes, int offset, int length) throws IOException {
        Record record = decode(schema, schema, bytes, offset, length);
        return record;
    }

    public static Record decode(Schema writerSchema, Schema readerSchema, byte[] bytes) throws IOException {
        Record record = decode(writerSchema, readerSchema, bytes, 0, bytes.length);
        return record;
    }

    public static Record decode(Schema writerSchema, Schema readerSchema,
                                byte[] bytes, int offset, int length) throws IOException {
        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(writerSchema, readerSchema);
        Record record = new Record(readerSchema);
        if (length > 0) {
            Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, offset, length, null);
            reader.read(record, decoder);
        }
        return record;
    }

}
