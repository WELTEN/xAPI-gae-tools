package nl.welteninstituut.tel.la.rest;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.*;
import com.google.appengine.tools.mapreduce.inputs.DatastoreInput;
import com.google.appengine.tools.mapreduce.outputs.DatastoreOutput;
import com.google.appengine.tools.mapreduce.outputs.InMemoryOutput;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import com.google.appengine.api.datastore.Text;

/**
 * Created by str on 18/05/15.
 */
@Path("/mapreduce")
public class MapReduce implements Serializable {

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/init")
    public String init() throws IOException {
//        String id = MapReduceJob.start(getCreationJobSpec(50, 5, 5), new MapReduceSettings.Builder(getSettings()).setBucketName("xapi-proxy-dev").build());
        String id = MapJob.start(getMapCreationJobSpec(50,5,5),new MapSettings.Builder(getSettings()).build());
        return "{'ok':'true'}";
    }

    private MapReduceSpecification<Entity, String, Long, KeyValue<String, Long>, List<List<KeyValue<String, Long>>>> getCreationJobSpec(int bytesPerEntity, int entities,
                                                                                                                                        int shardCount) {


        return new MapReduceSpecification.Builder<Entity, String, Long, KeyValue<String, Long>, List<List<KeyValue<String, Long>>>>(
                new DatastoreInput("Statement", 4 ),
                new CountMapper(),
                new CountReducer(),
                new InMemoryOutput<KeyValue<String, Long>>())
                .setKeyMarshaller(Marshallers.getStringMarshaller())
                .setValueMarshaller(Marshallers.getLongMarshaller())
                .setJobName("MapReduceTest count")
                .setNumReducers(4)
                .build();
    }

    private MapSpecification<Entity, Entity, Void> getMapCreationJobSpec(int bytesPerEntity, int entities,
                                                                    int shardCount) {
        // [START mapSpec]
        MapSpecification<Entity, Entity, Void> spec = new MapSpecification.Builder<Entity, Entity, Void>(
                new DatastoreInput("Statement", 4 ),
                new MapOnlyCountMapper(),
                new DatastoreOutput())
                .setJobName("Create MapReduce entities")
                .build();
        // [END mapSpec]
        return spec;
    }

    protected MapSettings getSettings() {
        MapSettings settings = new MapSettings.Builder()
                .setWorkerQueueName("default")
                .setModule("default")
                .build();
        return settings;
    }



    class CountReducer extends Reducer<String, Long, KeyValue<String, Long>> {

        private static final long serialVersionUID = 1316637485625852869L;

        @Override
        public void reduce(String key, ReducerInput<Long> values) {
            long total = 0;
            while (values.hasNext()) {
                total += values.next();
            }
            emit(KeyValue.of(key, total));
        }
    }

    class MapOnlyCountMapper extends MapOnlyMapper<Entity, Entity> {

        public MapOnlyCountMapper(){}

        @Override
        public void map(Entity entity) {
            Text payload = (Text) entity.getProperty("statementPayload");
            String learningLockerId = (String) entity.getProperty("learningLockerId");
            if (payload != null) {
                if (learningLockerId == null)  learningLockerId = "" +(entity.getKey().getId());

                Submit.toBigQuery(payload.getValue(), learningLockerId);
            }
        }
    }

    class CountMapper extends Mapper<Entity, String, Long> {

        private static final long serialVersionUID = 4973057382538885270L;

        private void incrementCounter(String name, long delta) {
            getContext().getCounter(name).increment(delta);
        }

        private void emitCharacterCounts(String s) {
        }

        @Override
        public void map(Entity entity) {
////            System.out.println(entity);
//            Text payload = (Text) entity.getProperty("statementPayload");
//            if (payload != null) {
//                System.out.println(payload.getValue());
//            }
        }
    }
}


