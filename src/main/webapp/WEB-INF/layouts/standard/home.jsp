<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="en">

<jsp:include page="includes/header.jsp"/>

<body>

<jsp:include page="includes/topmenu.jsp"/>

<div class="container-fluid mainPanel">
    <div class="row-fluid">
        <div id="menuContent" class="span4">
            <ul class="nav nav-tabs">
                <li class="active"><a id="profileTab" href="#profileTabContent" data-toggle="pill">
                    &nbsp;<spring:message code="tatami.show.profile"/></a></li>
                <li><a id="updateProfileTab" href="#updateProfileTabContent" data-toggle="pill"><i
                        class="icon-edit"></i>&nbsp;
                    <spring:message code="tatami.update.profile"/></a></li>
            </ul>
            <div class="alert alert-info">
                <div class="tab-content" style="margin-left: -10px;">
                    <div class="tab-pane active" id="profileTabContent">
                        <div class="container-fluid">
                            <div class="row-fluid">
                                <div class="span4"><img id="picture"/></div>
                                <div class="span8">
                                    <span id="profile_view"></span>
                                </div>
                            </div>
                            <div id="badges" class="well well-small row-fluid">
                                <div class="span4">
                                    <span id="tweetCount" class="badge"></span><br/><spring:message
                                        code="tatami.badge.tweets"/>
                                </div>
                                <div class="span4">
                                    <span id="friendsCount" class="badge"></span><br/><spring:message
                                        code="tatami.badge.followed"/>
                                </div>
                                <div class="span4">
                                    <span id="followersCount" class="badge"></span><br/><spring:message
                                        code="tatami.badge.followers"/>
                                </div>
                            </div>
                            <div class="row-fluid">
                                <div class="span12">
                                    <form class="form-inline" onsubmit="return tweet();">
                                        <textarea id="tweetContent" rel="popover" class="focused" maxlength="140"
                                                  placeholder="Type a new tweet..."></textarea>
                                        <button type="submit" class="btn btn-primary">Tweet</button>
                                    </form>
                                    <div class="error"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="updateProfileTabContent">
                        <div class="container-fluid">
                            <div class="row-fluid">
                                <form id="updateUserForm" onsubmit="return updateProfile();">
                                    <fieldset>
                                        <img id="pictureInput"/>
                                        <label><spring:message
                                                code="tatami.user.picture"/> <a href="http://www.gravatar.com" target="_blank">http://www.gravatar.com</a>
                                        </label>
                                        <label><spring:message
                                                code="tatami.user.email"/> :</label>
                                        <input id="emailInput"
                                               name="email"
                                               type="email"
                                               required="required"
                                               size="15"
                                               maxlength="60"
                                               placeholder="Enter e-mail..."/>

                                        <label><spring:message
                                                code="tatami.user.firstName"/> :</label>
                                        <input
                                                id="firstNameInput" name="firstName"
                                                type="text"
                                                required="required"
                                                size="15" maxlength="40"
                                                placeholder="Enter first name..."/>
                                        <label><spring:message
                                                code="tatami.user.lastName"/> :</label>
                                        <input id="lastNameInput"
                                               name="lastName"
                                               type="text"
                                               required="required"
                                               size="15"
                                               maxlength="40"
                                               placeholder="Enter last name..."/>
                                    </fieldset>

                                    <button type="submit" class="btn btn-primary">Update</button>
                                </form>
                                <div class="error"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div>
                <div class="alert alert-info" id="followUserContent"></div>
            </div>
        </div>

        <div id="mainContent" class="span8">
            <div class="tabbable">
                <ul class="nav nav-tabs">
                    <li class="active"><a id="mainTab" href="#timeLinePanel" data-toggle="tab"><i
                            class="icon-th-list"></i>&nbsp;<spring:message code="tatami.tweets"/></a></li>
                    <li><a id="favTab" href="#favLinePanel" data-toggle="tab"><i
                            class="icon-star"></i>&nbsp;<spring:message code="tatami.user.favoritetweets"/></a></li>
                    <li><a id="tagTab" href="#tagLinePanel" data-toggle="tab"><i
                            class="icon-tag"></i>&nbsp;<spring:message code="tatami.tags"/></a></li>
                    <li><a id="searchTab" href="#searchLinePanel" data-toggle="tab"><i
                            class="icon-search"></i>&nbsp;<spring:message code="tatami.search"/></a></li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i
                                class="icon-signal"></i>&nbsp;<spring:message code="tatami.tweets.stats"/>&nbsp;<b
                                class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><a id="piechartTab" href="#piechartPanel" data-toggle="tab"><spring:message
                                    code="tatami.stats.tweets.piechart"/></a></li>
                            <li><a id="punchchartTab" href="#punchchartPanel" data-toggle="tab"><spring:message
                                    code="tatami.stats.tweets.punchchart"/></a></li>
                        </ul>
                    </li>
                </ul>
                <div class="tab-content alert alert-success">
                    <div class="tab-pane active" id="timeLinePanel"></div>
                    <div class="tab-pane" id="favLinePanel"></div>
                    <div class="tab-pane" id="tagLinePanel"></div>
                    <div class="tab-pane" id="searchLinePanel"></div>
                    <div class="tab-pane" id="piechartPanel"></div>
                    <div class="tab-pane" id="punchchartPanel"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp"/>

<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<spring:eval expression="@applicationProps['tatami.version']" var="applicationVersion"/>

<spring:url value="resources/{applicationVersion}" var="resourceUrl">
    <spring:param name="applicationVersion" value="${applicationVersion}"/>
</spring:url>

<script src="/assets/js/tatami/constants.js"></script>
<script src="/assets/js/tatami/standard/tatami.js"></script>
<script src="/assets/js/tatami/standard/tatami.charts.js"></script>
<script type="text/javascript">
    google.load("visualization", "1", {packages:["corechart"]});
    var login = "<sec:authentication property="principal.username"/>";
    var tag = "${tag}";
    var page = "home";

    $(document).ready(function() {
        initHome();
    });
</script>
</body>
</html>
