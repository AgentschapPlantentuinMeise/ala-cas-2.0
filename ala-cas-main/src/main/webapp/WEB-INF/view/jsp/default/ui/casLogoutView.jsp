<%--

Licensed to Jasig under one or more contributor license
agreements. See the NOTICE file distributed with this work
for additional information regarding copyright ownership.
Jasig licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License.  You may obtain a
copy of the License at the following location:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

--%>
<jsp:directive.include file="includes/top.jsp" />

<div class="container" id="main">
	<!-- Main col -->
	<div class="col-xs-12 col-sm-6 col-sm-offset-3 col-md-4 col-md-offset-4">
		<div class="panel panel-default">
			<div class="panel-body">
				<div id="msg" class="success">
					<h3 class="heading-medium"><spring:message code="screen.logout.header" /></h3>
					<p class="lead color--primary-red"><spring:message code="screen.logout.success" /></p>
					<div class="alert alert-danger alert-dismissible" role="alert">
						<spring:message code="screen.logout.security" />
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<jsp:directive.include file="includes/bottom.jsp" />
