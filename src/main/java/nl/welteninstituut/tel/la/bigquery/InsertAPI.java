package nl.welteninstituut.tel.la.bigquery;

import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.Table;
import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.api.services.bigquery.model.TableDataInsertAllResponse;
import nl.welteninstituut.tel.la.Configuration;

import java.io.IOException;
import java.util.List;

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
public class InsertAPI {

    private static InsertAPI instance;

    private InsertAPI(){

    }

    public static InsertAPI getInstance(){
        if (instance ==  null) instance = new InsertAPI();
        return instance;
    }

    public Bigquery.Tables.Insert insertTable(Table table) throws IOException {
        return Common.bigquery.tables().insert(Configuration.get(Configuration.BQProject), Configuration.get(Configuration.BQDataSet), table);
    }

    public TableDataInsertAllResponse insertRowList(List rowList, String tableId) throws IOException {
        TableDataInsertAllRequest content =
                new TableDataInsertAllRequest().setRows(rowList);
        return Common.bigquery.tabledata().insertAll(
                Configuration.get(Configuration.BQProject),
                Configuration.get(Configuration.BQDataSet),
                tableId, content).execute();
    }
}
