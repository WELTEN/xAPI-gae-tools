package nl.welteninstituut.tel.la.rest;

import nl.welteninstituut.tel.oauth.jdo.UserLoggedInManager;

import java.util.StringTokenizer;

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
public class Service {

    protected String userId;
    protected int accountType;

    public String getBearer(String token){
        if (token.contains("auth="))
        return token.substring(token.indexOf("auth=") + 5);
        else return  token;
    }

    protected boolean validCredentials(String authToken) {
        String account = UserLoggedInManager.getUser(authToken);
        if (account != null) {
            setFullid(account);
        }
        return account != null;
    }

    public String getInvalidCredentialsBean() {
        return "{'error': 'credentials are invalid'}";
    }

    public void setFullid(String accountName) {
        StringTokenizer st = new StringTokenizer(accountName, ":");
        if(st.hasMoreTokens()) {
            accountType = Integer.valueOf(Integer.parseInt(st.nextToken()));
        }

        if(st.hasMoreTokens()) {
            this.userId = st.nextToken();
        }

    }
}
