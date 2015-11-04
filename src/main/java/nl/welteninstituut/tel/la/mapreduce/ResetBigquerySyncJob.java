package nl.welteninstituut.tel.la.mapreduce;

import com.google.appengine.tools.mapreduce.MapJob;
import com.google.appengine.tools.mapreduce.MapReduceJob;
import com.google.appengine.tools.mapreduce.MapReduceJobException;
import com.google.appengine.tools.mapreduce.MapReduceResult;
import com.google.appengine.tools.mapreduce.MapReduceSettings;
import com.google.appengine.tools.mapreduce.MapReduceSpecification;
import com.google.appengine.tools.mapreduce.MapSettings;
import com.google.appengine.tools.mapreduce.MapSpecification;
import com.google.appengine.tools.mapreduce.Marshallers;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.mapreduce.*;
import com.google.appengine.tools.mapreduce.inputs.DatastoreInput;
import com.google.appengine.tools.mapreduce.inputs.DatastoreKeyInput;
import com.google.appengine.tools.mapreduce.outputs.DatastoreOutput;
import nl.welteninstituut.tel.la.rest.Submit;

import java.io.Serializable;

/**
 * ****************************************************************************
 * Copyright (C) 2013 Open Universiteit Nederland
 * <p/>
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors: Stefaan Ternier
 * ****************************************************************************
 */
public class ResetBigquerySyncJob extends Job {

    private int state;

    public ResetBigquerySyncJob(int state) {
        this.state = state;
    }

    public void start() {
        String id = MapJob.start(getMapCreationJobSpec(100, 5, 5), new MapSettings.Builder(getSettings()).build());
    }




    private MapSpecification<Entity, Entity, Void> getMapCreationJobSpec(int bytesPerEntity, int entities,
                                                                         int shardCount) {
        MapSpecification<Entity, Entity, Void> spec = new MapSpecification.Builder<Entity, Entity, Void>(
                new DatastoreInput("Statement", 4 ),
                new MapOnlyCountMapper(),
                new DatastoreOutput())
                .setJobName("Update synchronisation state of entities")
                .build();
        return spec;
    }


    class MapOnlyCountMapper extends MapOnlyMapper<Entity, Entity> {

        public MapOnlyCountMapper(){}


        @Override
        public void map(Entity entity) {
            entity.setProperty("syncronisationState", state);
            emit(entity);
        }
    }
}
