<%@ page import="nl.welteninstituut.tel.la.Configuration" %>
<%@ page import="nl.welteninstituut.tel.oauth.jdo.AccountJDO" %>
<%@ page import="nl.welteninstituut.tel.oauth.OauthGoogleWorker" %>
<%@ page import="nl.welteninstituut.tel.oauth.jdo.OauthConfigurationJDO" %>
<%@ page import="nl.welteninstituut.tel.oauth.jdo.OauthKeyManager" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>AdminLTE 2 | Log in</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.5 -->
    <link rel="stylesheet" href=" bootstrap/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="dist/css/AdminLTE.css">
    <!-- iCheck -->
    <link rel="stylesheet" href="plugins/iCheck/square/blue.css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body class="hold-transition login-page">
    <div class="login-box">
      <div class="login-logo">
        <a href="index2.html"> ECO<b>Learning Analytics</b> login</a>
      </div><!-- /.login-logo -->
      <div class="login-box-body">
        <p class="login-box-msg">Sign in to start your session</p>
        <div class="social-auth-links text-center">
        <%
          boolean showOr= false;
//          System.out.println("metaccount "+Configuration.getAppId());
//          System.out.println(Configuration.get(Configuration.METAACCOUNT));
//            Configuration.printOut();
          if (Configuration.listContains(Configuration.METAACCOUNT, AccountJDO.GOOGLECLIENT)){
            showOr= true;
              OauthConfigurationJDO jdo = OauthKeyManager.getConfigurationObject(AccountJDO.GOOGLECLIENT);
              String client_id = jdo.getClient_id();
              String redirect_uri = jdo.getRedirect_uri();

              String url = "https://accounts.google.com/o/oauth2/auth?redirect_uri=" + redirect_uri +
                    "&response_type=code&client_id=" + client_id + "&approval_prompt=force" +
                    "&scope=https://www.googleapis.com/auth/userinfo.profile  https://www.googleapis.com/auth/userinfo.email";
        %>
          <a class="btn btn-block btn-social btn-ecolearning btn-flat" href="<%=url%>"><i class="fa fa-ecolearning"></i>Login with Google</a>
        <%
          }
          if (Configuration.listContains(Configuration.METAACCOUNT, AccountJDO.ECOCLIENT)){
              if (showOr) { %><p>- OR -</p><%  }
            showOr = true;
            OauthConfigurationJDO jdo = OauthKeyManager.getConfigurationObject(AccountJDO.ECOCLIENT);
            String client_id = jdo.getClient_id();
            String redirect_uri = jdo.getRedirect_uri();
            String url = "http://idp.ecolearning.eu/authorize?response_type=code&scope=openid+profile+email&client_id="+client_id+"&redirect_uri="+redirect_uri;

        %>
          <a class="btn btn-block btn-social btn-ecolearning btn-flat" href="<%=url%>"><i class="fa fa-ecolearning"></i>[Dev] Login with ECO</a>


          <%

          }
        %>



          <!--<a href="#" class="btn btn-block btn-social btn-facebook btn-flat"><i class="fa fa-facebook"></i> Sign in using Facebook</a>-->
          <!--<a href="#" class="btn btn-block btn-social btn-google btn-flat"><i class="fa fa-google-plus"></i> Sign in using Google+</a>-->
        </div><!-- /.social-auth-links -->

        <!--<a href="#">I forgot my password</a><br>-->
        <!--<a href="register.html" class="text-center">Register a new membership</a>-->

      </div><!-- /.login-box-body -->
    </div><!-- /.login-box -->

    <!-- jQuery 2.1.4 -->
    <script src="plugins/jQuery/jQuery-2.1.4.min.js"></script>
    <!-- Bootstrap 3.3.5 -->
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <!-- iCheck -->
    <script src="plugins/iCheck/icheck.min.js"></script>
    <script>
      $(function () {
        $('input').iCheck({
          checkboxClass: 'icheckbox_square-blue',
          radioClass: 'iradio_square-blue',
          increaseArea: '20%' // optional
        });
      });
    </script>
  </body>
</html>
