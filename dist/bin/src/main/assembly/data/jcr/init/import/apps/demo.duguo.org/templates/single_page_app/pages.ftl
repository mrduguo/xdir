<!-- Start of page: #welcome -->
<div data-role="page" id="welcome" data-theme="a">
    <div data-role="header">
        <h1>University MIS</h1>
    </div>
    <!-- /header -->
    <div data-role="content">
        <h2>Welcome <span id="welcomeUserName"></span>!</h2>
            <ul data-role="listview" data-split-icon="gear">
                <li>
                    <a href="#">
                        <img src="http://media.linkedin.com/mpr/mpr/shrink_80_80/p/3/000/04e/312/18cfe8d.jpg"/>

                        <h3>Guo Du</h3>

                        <p>Senior Software Engineer at NewBay Software</p>
                    </a>
                    <a href="#editprofile" data-rel="dialog" data-transition="slideup">Edit Profile</a>
                </li>
                <li>
                    <a href="tel:088888888">
                        <h3>Mobile</h3>
                        <p>088-8888 888</p>
                    </a>
                </li>
                <li>
                    <a href="mailto:mrduguo@duguo.org">
                        <h3>Email</h3>
                        <p>mrduguo@duguo.org</p>
                    </a>
                </li>
                <li>
                    <a href="http://facebook.com">
                        <h3>Facebook</h3>
                        <p>facebook.com/mrduguo</p>
                    </a>
                </li>
            </ul>
    </div>
    <!-- /content -->
    <div data-role="footer">
        <div data-role="navbar">
            <ul>
                <li><a href="#welcome" data-icon="home" class="ui-btn-active ui-state-persist">Welcome</a></li>
                <li><a href="#courses" data-icon="grid" data-transition="none">Courses</a></li>
                <li><a href="#login" class="logoutButton" data-icon="forward">Logout</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
</div><!-- /page welcome -->

<!-- Start of page: #login -->
<div data-role="page" id="login" data-theme="a">
    <div data-role="header">
        <h1>University MIS</h1>
    </div>
    <!-- /header -->
    <div data-role="content">
        <div>
            <p>Please login to access the system:</p>

            <form id="login_form" method="post">
                <input id="username" name="username" placeholder="Username" type="text" autocapitalize="off"
                       autocomplete="off"
                       autocorrect="off">
                <input id="password" name="password" placeholder="Password" type="password">
                <input id="signinButton" type="submit" value="Sign in">
            </form>
        </div>
    </div>
    <!-- /content -->
</div><!-- /page login -->

<!-- Start of page: #courses -->
<div data-role="page" id="courses" data-theme="a">
    <div data-role="header">
        <h1>University MIS</h1>
    </div>
    <!-- /header -->
    <div data-role="content">
        <ul data-role="listview">
            <li><a href="#course?courseId=1">
                <img src="http://jquerymobile.com/test/docs/lists/images/album-bb.jpg"/>

                <h3>Broken Bells</h3>

                <p>Broken Bells</p>
            </a></li>
            <li><a href="#course?courseId=2">
                <img src="http://jquerymobile.com/test/docs/lists/images/album-hc.jpg"/>

                <h3>Warning</h3>

                <p>Hot Chip</p>
            </a></li>
        </ul>
    </div>
    <!-- /content -->
    <div data-role="footer">
        <div data-role="navbar">
            <ul>
                <li><a href="#welcome" data-icon="home" data-transition="none">Welcome</a></li>
                <li><a href="#courses" data-icon="grid" class="ui-btn-active ui-state-persist">Courses</a></li>
                <li><a href="#login" class="logoutButton" data-icon="forward">Logout</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
</div><!-- /page courses -->

<!-- Start of page: #course -->
<div data-role="page" id="course" data-theme="a">
    <div data-role="header">
        <h1>Course A</h1>
    </div>
    <!-- /header -->
    <div data-role="content">
        <ul data-role="listview" data-split-icon="gear">
            <li>
                <a href="#teacher?teacherId=2">
                    <h3>Lecture: Judy</h3>

                    <p>Who is Judy?</p>
                </a>
                <a href="lists-split-purchase.html" data-rel="dialog" data-transition="slideup">Purchase album
                </a>
            </li>
            <li><a href="#students?courseId=2">Students <span class="ui-li-count">62</span></a></li>
        </ul>
    </div>
    <!-- /content -->
    <div data-role="footer">
        <div data-role="navbar">
            <ul>
                <li><a href="#welcome" data-icon="home" data-transition="none">Welcome</a></li>
                <li><a href="#courses" data-icon="grid" class="ui-btn-active ui-state-persist">Courses</a></li>
                <li><a href="#login" class="logoutButton" data-icon="forward">Logout</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
</div><!-- /page course -->

<!-- Start of page: #students -->
<div data-role="page" id="students" data-theme="a">
    <div data-role="header">
        <h1>Course A: Students</h1>
    </div>
    <!-- /header -->
    <div data-role="content">
        <ul data-role="listview" data-filter="true">
            <li><a href="#student?studentId=1">Acura</a></li>
            <li><a href="index.html">Audi</a></li>
            <li><a href="index.html">BMW</a></li>
            <li><a href="index.html">Cadillac</a></li>
            <li><a href="index.html">Chrysler</a></li>
            <li><a href="index.html">Dodge</a></li>
            <li><a href="index.html">Ferrari</a></li>
            <li><a href="index.html">Ford</a></li>
            <li><a href="index.html">GMC</a></li>
            <li><a href="index.html">Honda</a></li>
            <li><a href="index.html">Hyundai</a></li>
            <li><a href="index.html">Infiniti</a></li>
            <li><a href="index.html">Jeep</a></li>
            <li><a href="index.html">Kia</a></li>
            <li><a href="index.html">Lexus</a></li>
            <li><a href="index.html">Mini</a></li>
            <li><a href="index.html">Nissan</a></li>
            <li><a href="index.html">Porsche</a></li>
            <li><a href="index.html">Subaru</a></li>
            <li><a href="index.html">Toyota</a></li>
            <li><a href="index.html">Volkswagon</a></li>
            <li><a href="index.html">Volvo</a></li>
        </ul>
    </div>
    <!-- /content -->
    <div data-role="footer">
        <div data-role="navbar">
            <ul>
                <li><a href="#welcome" data-icon="home" data-transition="none">Welcome</a></li>
                <li><a href="#courses" data-icon="grid" class="ui-btn-active ui-state-persist">Courses</a></li>
                <li><a href="#login" class="logoutButton" data-icon="forward">Logout</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
</div><!-- /page students -->

<!-- Start of page: #student -->
<div data-role="page" id="student" data-theme="a">
    <div data-role="header">
        <h1>Student A</h1>
    </div>
    <!-- /header -->
    <div data-role="content">
        student profile
    </div>
    <!-- /content -->
    <div data-role="footer">
        <div data-role="navbar">
            <ul>
                <li><a href="#welcome" data-icon="home" data-transition="none">Welcome</a></li>
                <li><a href="#courses" data-icon="grid" class="ui-btn-active ui-state-persist">Courses</a></li>
                <li><a href="#login" class="logoutButton" data-icon="forward">Logout</a></li>
            </ul>
        </div>
        <!-- /navbar -->
    </div>
</div><!-- /page student -->


<div data-role="page" id="dialog">
    <div data-role="header">
        <h1 id="errTitle"></h1>
    </div>
    <div data-role="content" id="errMsg">
    </div>
</div>


<style type="text/css">
    #username, #password {
        margin: 0;
        padding: 10px 8px;
        -webkit-appearance: none;
        display: block;
    }

    #username {
        border-bottom-left-radius: 0;
        border-bottom-right-radius: 0;
    }

    #password {
        border-top-left-radius: 0;
        border-top-right-radius: 0;
        margin-top: 2px;
    }
</style>
<script type="text/javascript">
    $(window).bind('pagecontainercreate', function () {
        $.mobile.firstPage = $("#login");
    });

    $(document).delegate("#welcome", "pagebeforeshow", function (event, ui) {
        if (jQuery.data(document.body, "username") == null) {
            //$.mobile.changePage($("#login"));
        } else {
            $("#welcomeUserName").html(jQuery.data(document.body, "username"));
        }
    });


    var firstLogin = true;
    $("#login_form").submit(function () {
        if (firstLogin) {
            firstLogin = false;
            $("#errTitle").html("Login Failed");
            $("#errMsg").html("Wrong username or password!");
            $.mobile.changePage($("#dialog"), { transition:"slideup", role:"dialog"});
        } else {
            jQuery.data(document.body, "username", $("input[type=text][name=username]").val());
            this.reset();
            $.mobile.changePage($("#welcome"), { transition:"fade"});
        }
        return false;
    });

    $(".logoutButton").click(function () {
        jQuery.data(document.body, "username", null);
    });


</script>
