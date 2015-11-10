package nl.welteninstituut.tel.la.mapreduce;

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.tools.mapreduce.MapReduceSettings;
import com.google.appengine.tools.mapreduce.MapSettings;

import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.security.GeneralSecurityException;

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
public class Job implements Serializable {
    public final static String MODULE_NAME= "default";
    public final static String WORKER_NAME= "default";


    protected MapSettings getSettings() {
        MapSettings settings = new MapSettings.Builder()
                .setWorkerQueueName(WORKER_NAME)
                .setModule(MODULE_NAME)
                .build();
        return settings;
    }

    protected MapReduceSettings getMapReduceSettings(){
//
        String bucket = AppIdentityServiceFactory.getAppIdentityService().getDefaultGcsBucketName();
        System.out.println(bucket);
        MapReduceSettings settings = new MapReduceSettings.Builder()

                .setWorkerQueueName(WORKER_NAME)
                .setModule(MODULE_NAME)
                .build();
        return settings;
    }

    public void testwrite() throws Exception{
        // Get a file service
        FileService fileService = FileServiceFactory.getFileService();

// Create a new Blob file with mime-type "text/plain"
        AppEngineFile file = fileService.createNewBlobFile("text/plain");

// Open a channel to write to it
        boolean lock = false;
        FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

// Different standard Java ways of writing to the channel
// are possible. Here we use a PrintWriter:
        PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
        out.println("The woods are lovely dark and deep.");
        out.println("But I have promises to keep.");

// Close without finalizing and save the file path for writing later
        out.close();
        String path = file.getFullPath();

// Write more to the file in a separate request:
        file = new AppEngineFile(path);

// This time lock because we intend to finalize
        lock = true;
        writeChannel = fileService.openWriteChannel(file, lock);

// This time we write to the channel directly
        writeChannel.write(ByteBuffer.wrap
                ("And miles to go before I sleep.".getBytes()));

// Now finalize
        writeChannel.closeFinally();
    }
}
