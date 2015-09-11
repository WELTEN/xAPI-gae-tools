package nl.welteninstituut.tel.la.tasks;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public abstract class GenericBean  implements Runnable{
    private static final Logger log = Logger.getLogger(GenericBean.class.getName());

    private String token;


    public GenericBean() {

    }

    public GenericBean(String token) {
        super();
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void scheduleTask() {
        Queue queue = QueueFactory.getDefaultQueue();
        TaskOptions to  = TaskOptions.Builder.withUrl("/asyncTask")
                .param("type", this.getClass().getName());
        queue.add(setParameters(to));
    }

    protected TaskOptions setParameters(TaskOptions to) {
        Iterator<Field> fields = getRelevantBeanProperties(this.getClass()).iterator();
        while (fields.hasNext()) {
            Field field = (Field) fields.next();
            try {
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    Method m = getClass().getMethod(getBeanMethodName(field.getName()));
                    Object value = m.invoke(this);
                    if (value !=null) to = to.param(field.getName(),value.toString());
                }
            } catch(NoSuchMethodException e){
                if (!"log".equals(field.getName()))
                    log.log(Level.WARNING, e.getMessage(), e);
            }catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }

        }
        return to;
    }


    protected List<Field> getRelevantBeanProperties(Class beanCls) {
        Vector<Field> returnFields = new Vector<Field>();
        Field[] fields = beanCls.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return returnFields;
        }
        for (int i = 0; i < fields.length; i++) {
            returnFields.add(fields[i]);
        }
        Class superClass = beanCls.getSuperclass();
        if (!superClass.equals(Object.class)) {
            List<Field> subfields = getRelevantBeanProperties(superClass);
            returnFields.addAll(subfields);
        }

        return returnFields;
    }

    protected String getBeanMethodName(String nameOfField) {
        if (nameOfField == null || nameOfField == "")
            return "";
        String method_name = "get";
        method_name += nameOfField.substring(0, 1).toUpperCase();

        if (nameOfField.length() == 1)
            return method_name;

        method_name += nameOfField.substring(1);
        return method_name;
    }


    public void run() {
    }
}
