package resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;

import java.io.IOException;

/**
 * Created by piotrek on 15.06.16.
 */
public class ObjectIdSerializerModule extends SimpleModule {

    public ObjectIdSerializerModule() {
        super("ObjectIdSerializerModule", new Version(0, 1, 0, "alpha"));
        this.addSerializer(ObjectId.class, new ObjectIdSerializer());
    }

    public class ObjectIdSerializer extends JsonSerializer<ObjectId> {

        @Override
        public void serialize(ObjectId value, JsonGenerator jgen
                , SerializerProvider provider)
                throws IOException, JsonProcessingException {
            jgen.writeString(value.toString());
        }
    }
}
