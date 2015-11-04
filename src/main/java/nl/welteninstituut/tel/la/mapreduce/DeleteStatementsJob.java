package nl.welteninstituut.tel.la.mapreduce;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.mapreduce.*;
import com.google.appengine.tools.mapreduce.inputs.DatastoreKeyInput;
import com.google.appengine.api.datastore.Query;

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
public class DeleteStatementsJob extends Job {

    private String origin;

    public DeleteStatementsJob(String origin) {
        this.origin = origin;
    }

    public void start() {
        String id = MapJob.start(getMapCreationJobSpec(5), new MapSettings.Builder(getSettings()).build());
    }


    private MapSpecification<Key, Void, Void> getMapCreationJobSpec(int mapShardCount) {
        Query.Filter propertyFilter =
                new Query.FilterPredicate("origin",
                        Query.FilterOperator.EQUAL,
                        origin);
        Query q = new Query("Statement").setFilter(propertyFilter);
        DatastoreKeyInput input = new DatastoreKeyInput(q, mapShardCount);
        DeleteEntityMapper mapper = new DeleteEntityMapper();
        return new MapSpecification.Builder<Key, Void, Void>(input, mapper)
                .setJobName("Delete Statements")
                .build();
    }

    class DeleteEntityMapper extends MapOnlyMapper<Key, Void> {

        private transient DatastoreMutationPool batcher;
        @Override
        public void beginSlice() {
            batcher = DatastoreMutationPool.create();
        }

        @Override
        public void endSlice() {
            batcher.flush();
        }

        @Override
        public void map(Key key) {
            batcher.delete(key);
        }
    }
}
