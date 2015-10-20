//////////////////////////
// Models relates to users
/////////////////////////
window.User = Backbone.Model.extend({
    initialize: function(a){
        //console.log("User initialize");
    }
});

window.CurrentUser = Backbone.Collection.extend({
    model: User,
    url: "/rest/account/accountDetails"
});

window.UserRunCollection = Backbone.Collection.extend({
    model: User,
    url: function(){
        return "/rest/users/runId/"+this.runId;
    },
    parse: function(response){
        return response.users;
    }
});


//////////////////////////////////////////
// Defined header to make the service call
//////////////////////////////////////////
var setHeader = function (xhr) {
    // Check cookie and read that value

    //console.log(location.href);

    //var s = document.cookie;

    var accessToken = "";

    // Problem:
    // 1) someone put an access token manually it will restore the current cookie
    // 2)

    if(location.href.indexOf("accessToken=") > -1){
        var aux = location.href.split("accessToken=")[1];
        accessToken = aux.split("&")[0];
        //console.log(location.href, aux, accessToken);

        var date = new Date();
        date.setTime(date.getTime() + (1 * 24 * 60 * 60 * 1000));
        var expires = "; expires=" + date.toGMTString();

        $.cookie("arlearn.AccessToken", accessToken, {expires: date, path: "/"});
        $.cookie("arlearn.OauthType", 2, {expires: date, path: "/"});

        document.location = "/";


    }

    if($.cookie("arlearn.AccessToken")){
        accessToken = $.cookie("arlearn.AccessToken");
        //console.log(location.href, accessToken);
    }

    xhr.setRequestHeader('Authorization', 'GoogleLogin auth='+accessToken);
    xhr.setRequestHeader('Accept', 'application/json');

    //console.log(accessToken,xhr);
}