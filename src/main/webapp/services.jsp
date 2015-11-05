<%@ page import="nl.welteninstituut.tel.la.Configuration"%>
<%@ page import="nl.welteninstituut.tel.oauth.jdo.UserLoggedInManager"%>
<%@ page import="nl.welteninstituut.tel.oauth.jdo.AccountJDO"%>
<%@ page import="nl.welteninstituut.tel.oauth.jdo.AccountManager"%>
<%@ page import="nl.welteninstituut.tel.oauth.jdo.OauthConfigurationJDO"%>
<%@ page import="nl.welteninstituut.tel.oauth.jdo.OauthKeyManager"%>
<%@ page import="nl.welteninstituut.tel.oauth.jdo.OauthServiceAccount"%>
<%@ page import="nl.welteninstituut.tel.oauth.jdo.OauthServiceAccountManager"%>
<%@ page import="nl.welteninstituut.tel.util.StringPool"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>AdminLTE 2 | Services</title>
<!-- Tell the browser to be responsive to screen width -->
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport" />
<!-- Bootstrap 3.3.5 -->
<link rel="stylesheet" href=" bootstrap/css/bootstrap.min.css" />
<!-- Font Awesome -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css" />
<!-- Ionicons -->
<link rel="stylesheet"
	href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css" />
<!-- Theme style -->
<link rel="stylesheet" href="dist/css/AdminLTE.css" />
<!-- iCheck -->
<link rel="stylesheet" href="plugins/iCheck/square/blue.css" />

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<%
	String accessToken = request.getParameter("accessToken");
	String type = request.getParameter("type");
	String exp = request.getParameter("exp");
	
%>
<body class="hold-transition login-page">
	<div class="login-box">
		<div class="login-logo">
			<a href="<%=String.format("index.html?accessToken=%s&type=%s&exp=%s", accessToken, type, exp)%>"> Learning<b>Pulse</b> project</a>
		</div>

		<!-- /.login-logo -->
		<div class="login-box-body">
			<p class="login-box-msg">Grant access to services</p>
			<div class="social-auth-links text-center">


				<%
					String userName = UserLoggedInManager.getUser(accessToken);
					AccountJDO account = AccountManager.getAccount(userName);

					if (account == null) {
				%>

				<h4>You are not signed in to Learning Pulse</h4>
				<a href="/login.jsp">login</a>
				<%
					} else {
				%>
				<%=account.getName()%>
				
				<p />


				<%
					if (Configuration.listContains(Configuration.SECONDARY_ACCOUNT,
								AccountJDO.FITBITCLIENT)) {
							OauthConfigurationJDO jdo = OauthKeyManager
									.getConfigurationObject(AccountJDO.FITBITCLIENT);
							String clientId = jdo.getClient_id();
							String redirectUrl = jdo.getRedirect_uri();

							String url = "https://www.fitbit.com/oauth2/authorize?response_type=code&scope=activity+heartrate+profile+settings&"
									+ "client_id="
									+ clientId
									+ "&redirect_uri="
									+ redirectUrl
									+ "&state="
									+ request.getParameter("accessToken");
				%>
				<a class="btn btn-block btn-social btn-ecolearning btn-flat"
					href="<%=url%>"><i class="fa fa-fitbit"></i>Connect to
					Fitbit</a>
				<%
					}
				
					if (Configuration.listContains(Configuration.SECONDARY_ACCOUNT,
						AccountJDO.RESCUETIMECLIENT)) {
						
						OauthServiceAccount rescueTimeAccount = OauthServiceAccountManager.getAccount(AccountJDO.RESCUETIMECLIENT, account.getLocalId());
						String apiKey = rescueTimeAccount != null ? rescueTimeAccount.getAccessToken() : StringPool.BLANK;
						
				%>
				
				            <!-- search form -->
            <form action="/handler" method="POST" class="sidebar-form">
               	RescueTime - enter here the <a href="https://www.rescuetime.com/anapi/manage">Full API key</a>
            
                <div class="input-group">
                    <input type="text" name="rt-key" class="form-control" placeholder="API key..." value="">
                    <input type="hidden" name="accessToken" value="<%=accessToken%>" />
                    <input type="hidden" name="type" value="<%=type%>" />
                    <input type="hidden" name="exp" value="<%=exp%>" />
              <span class="input-group-btn">
                <button type="submit" name="search" id="search-btn" class="btn btn-flat"><i class="fa fa-plus"></i></button>
              </span>
                </div>
            </form>
				
				<%
					
					}
					}
				%>
			</div>


		</div>
		<!-- /.login-box-body -->
	</div>
	<!-- /.login-box -->

	<!-- jQuery 2.1.4 -->
	<script src="plugins/jQuery/jQuery-2.1.4.min.js"></script>
	<!-- Bootstrap 3.3.5 -->
	<script src="bootstrap/js/bootstrap.min.js"></script>
	<!-- iCheck -->
	<script src="plugins/iCheck/icheck.min.js"></script>
	<script>
		$(function() {
			$('input').iCheck({
				checkboxClass : 'icheckbox_square-blue',
				radioClass : 'iradio_square-blue',
				increaseArea : '20%' // optional
			});
		});
	</script>
</body>
</html>