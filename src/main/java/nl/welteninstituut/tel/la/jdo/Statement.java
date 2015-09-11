package nl.welteninstituut.tel.la.jdo;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/*******************************************************************************
 * Copyright (C) 2013 Open Universiteit Nederland
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors: Stefaan Ternier
 ******************************************************************************/

@PersistenceCapable
public class Statement {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    protected Long id;

    @Persistent
    protected String learningLockerId;

    @Persistent
    private Text statementPayload;

    @Persistent
    private Long lastModificationDate;

    @Persistent
    private String authorizationData;

    @Persistent
    private boolean isSynchronized;

    @Persistent
    private Text errorMessage;

    public long getIdentifier(){
        return id;
    }

//    public void setIdentifier(Long id) {
//        if (id != null)
//            this.id = KeyFactory.createKey(Statement.class.getSimpleName(), id);
//    }

    public Text getStatementPayload() {
        return statementPayload;
    }

    public void setStatementPayload(Text statementPayload) {
        this.statementPayload = statementPayload;
    }

    public Long getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Long lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public String getAuthorizationData() {
        return authorizationData;
    }

    public void setAuthorizationData(String authorizationData) {
        this.authorizationData = authorizationData;
    }

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean isSynchronized) {
        this.isSynchronized = isSynchronized;
    }

    public Text getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(Text errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getLearningLockerId() {
        return learningLockerId;
    }

    public void setLearningLockerId(String learningLockerId) {
        this.learningLockerId = learningLockerId;
    }
}