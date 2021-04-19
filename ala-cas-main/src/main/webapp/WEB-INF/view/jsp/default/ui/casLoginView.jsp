<%--
TODO: add ALA licensing info. 
--%>

<jsp:directive.include file="includes/top.jsp" />
<section id="breadcrumb">
	<div class="container">
		<div class="row">
			<nav aria-label="Breadcrumb" role="navigation">
				<ol class="breadcrumb-list">
					<li><a href="<fmt:message key="header.main_website" />"><fmt:message key="casLoginView.home" /></a></li>
					<li class="active"><fmt:message key="casLoginView.authentication" /></li>
				</ol>
			</nav>
		</div>
	</div>
</section>

<c:if test="${not pageContext.request.secure}">
    <div id="msg" class="errors">
        <span class="ssoError">
            <h2>Non-secure Connection</h2>
            <p>You are currently accessing CAS over a non-secure connection.  Single Sign On WILL NOT WORK.  In order to have single sign on work, you MUST log in over HTTPS.</p>
        </span>
    </div>
</c:if>

<!-- Container -->
<div class="container" id="main">

    <!-- Main col -->
    <div class="col-xs-12 col-sm-6 col-sm-offset-3 col-md-4 col-md-offset-4">
	<h1 class="hidden"><fmt:message key="casLoginView.welcome" /></h1>

	<div class="panel panel-default">
	    <div class="panel-body">

		    <div class="logo-brand">
			<div class="brand-layout-control">
                <div class="brand-layout-logo"/>
			</div>
			<!--<h2 class="heading-medium-large"><fmt:message key="casLoginView.sign_in_to_application" /></h2>-->
		    </div>

		    <a href="${FacebookClientUrl}" class="btn btn-lg btn-facebook btn-block margin-bottom-1 font-xxsmall"><fmt:message key="casLoginView.sign_in_facebook" /></a>
		    <a href="${TwitterClientUrl}"  class="btn btn-lg btn-twitter btn-block margin-bottom-1 font-xxsmall"><fmt:message key="casLoginView.sign_in_twitter" /></a>
		    <a href="${Google2ClientUrl}"  class="btn btn-lg btn-google btn-block margin-bottom-1 font-xxsmall"><fmt:message key="casLoginView.sign_in_google" /></a>

		    <p class="separator t-center margin-bottom-2"><span><fmt:message key="casLoginView.or" /></span></p>

		    <form:form class="form-signin" method="post" id="fm1" commandName="${commandName}" htmlEscape="true">
                <fmt:message key="casLoginView.password" var="passwordText"/>
                <fmt:message key="casLoginView.email_address" var="emailText" />


                <fieldset>
			<div class="form-group" id="ala-login-fields">
			    <label for="username" class="sr-only">
				<spring:message code="screen.welcome.label.netid" />
			    </label>

			    <c:choose>
				<c:when test="${not empty sessionScope.openIdLocalId}">
				    <strong>${sessionScope.openIdLocalId}</strong>
				    <input type="hidden" id="username" name="username" value="${sessionScope.openIdLocalId}" />
				</c:when>
				<c:otherwise>
				    <spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
				    <form:input cssClass="required form-control input-lg"
						cssErrorClass="error"
						id="username"
						size="25"
						tabindex="1"
						accesskey="${userNameAccessKey}"
						path="username"
						autocomplete="off"
						htmlEscape="true"
						placeholder="${emailText}" autofocus="autofocus" />
				</c:otherwise>
				</c:choose>
			</div>


			<div class="form-group margin-bottom-2" style="position: relative;">
			    <label for="password" class="sr-only">
				<spring:message code="screen.welcome.label.password" />
			    </label>
			    <%--
			    NOTE: Certain browsers will offer the option of caching passwords for a user.  There is a non-standard attribute,
			    "autocomplete" that when set to "off" will tell certain browsers not to prompt to cache credentials.  For more
			    information, see the following web page:
			    http://www.technofundo.com/tech/web/ie_autocomplete.html
			    --%>
			    <spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
			    <form:password cssClass="required form-control input-lg"
					   cssErrorClass="error"
					   id="password"
					   path="password"
					   accesskey="${passwordAccessKey}"
					   htmlEscape="true"
					   autocomplete="off"
					   size="25"
					   placeholder="${passwordText}"
					   tabindex="2" />

			    <a class="forgot" href="/userdetails/registration/forgottenPassword?lang=${language}" target="_blank"><fmt:message key="casLoginView.forgot_password" /></a>
			</div>

			<!-- Alert Information -->
			<form:errors path="*" id="msg" cssClass="alert alert-danger alert-dismissible" element="div" role="alert" htmlEscape="false">
			</form:errors>
			<!-- End Alert Information -->

			<div class="checkbox checkbox-rememberMe margin-bottom-2">
			    <label>
					<input type="checkbox" id="rememberMe" name="rememberMe" value="true" tabindex="3" /> <fmt:message key="casLoginView.remember_me" />
			    </label>
			</div>
			</fieldset>

			<input type="hidden" name="lt"        value="${loginTicket}" />
			<input type="hidden" name="execution" value="${flowExecutionKey}" />
			<input type="hidden" name="_eventId"  value="submit" />

			<button class="btn btn-lg btn-primary btn-block margin-bottom-1 font-xxsmall" accesskey="l" type="submit" tabindex="3">
			    <fmt:message key="login.dropdown.login" />
			</button>

			<!-- NOTE: you can leave the link "/userdetails/registration/createAccount" UNLESS you are changing the the userdetails
			     servlet mapping or deploying userdetails on a diff host.
			-->
			<p class="small text-center"><fmt:message key="casLoginView.dont_have_an_account" /> <a href="/userdetails/registration/createAccount?lang=${language}"><fmt:message key="casLoginView.sign_up_now" /></a>.</p>

		    </form:form>

	    </div>
	</div>

    </div><!-- End main col -->

</div><!-- End container#main -->

<jsp:directive.include file="includes/bottom.jsp" />
