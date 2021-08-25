// see https://javascript-minifier.com/


var VERSION = "2.2.0"
// instead of submitting directly to jira.rcsb.org, we are making use of a proxy server
// see JiraFeedbackProxy project: https://github.com/rcsb/JiraFeedbackProxy
var JIRA_PROXY = "https://jira-proxy.rcsb.org/"
var HTML_SRC = "https://cdn.rcsb.org/jira-feedback/includes/jira-fdbck.html"
var LOGO_RCSB = "https://cdn.rcsb.org/rcsb-pdb/v2/common/images/rcsb_logo.png"
var LOGO_PDB101 = "https://cdn.rcsb.org/pdb101/common/images/logo-pdb101.png"
var ISSUE_TYPE = "Bug"
var LOG_PREFIX = "jira-fdbck: "
var MAX_UPLOADS = 3
var MAX_FILE_SIZE_BYTES = 5000000 //5MB


$(function () {
    var hostname = document.location.hostname;
    var logoSrc = (hostname.indexOf("pdb101") !== -1) ? LOGO_PDB101 : LOGO_RCSB;
    var projKey = (hostIsDev(hostname)) ? "RO" : "HELP";

    //console.info(LOG_PREFIX + "hostname=" + hostname);
    //console.info(LOG_PREFIX + "projKey=" + projKey);
    //console.info(LOG_PREFIX + "USING LOCAL");


    // recruit html markup for jira-fdbck content onto page into <div id="jira-fdbck"></div>
    $.get(HTML_SRC, function (data) {
        $("#jira-fdbck").html(data);
        $(".modal-logo").attr("src", logoSrc);
        $("#projectkey").val(projKey);
        $("#issuetype").val(ISSUE_TYPE);
        // console.log(LOG_PREFIX + "Ajax feedback html import was performed (feedback modal prepared).");
    });

    // Each time a form is clicked on
    // uses localstorage for checkbox
    // launch/show form
    $(document).on("click", ".jira-fdbck-btn", function () {
        $("#jira-fdbck-modal").modal("show");

        console.log(LOG_PREFIX + 'jira-fdbck-btn CLICKED')

        //getting value from web storage for fname, lname, email
        var checkedVal = JSON.parse(localStorage.getItem('privacyPolicyAgreement'));
        console.log(LOG_PREFIX + 'checkedVal=' + checkedVal)
        document.getElementById('privacyPolicyAgreement').checked = checkedVal

        var fnameVal= sessionStorage.getItem('fname');
        document.getElementById('fname').value = fnameVal
        var lnameVal= sessionStorage.getItem('lname');
        document.getElementById('lname').value = lnameVal
        var emailVal= sessionStorage.getItem('email');
        document.getElementById('email').value = emailVal

        var disabled = !(checkedVal)
        $("#jira-fdbck-submit").prop("disabled", disabled);
    });

    // FRONT-371 - Checkbox of Privacy Policy
    $(document).on("click", "#privacyPolicyAgreement", function (event) {
        if (event.target.checked) {
            $("#jira-fdbck-submit").prop("disabled", false);
        } else {
            $("#jira-fdbck-submit").prop("disabled", true);
        }
    });

    // RESET each time modal feedback form is launched
    $("#jira-fdbck").on("show.bs.modal", "#jira-fdbck-modal", function () {
        //clear form fields each time modal is launched to capture a new issue
        $("#feedbackfrm .form-control").val("");
        $("#privacyPolicyAgreement").removeAttr('checked');
        $("#maxuploads").text(MAX_UPLOADS);

        //clear any field validation prompts
        clearValidationPrompts();
    });

     // form submit handler
    $("#jira-fdbck").on("click", "#jira-fdbck-submit", function () {

        //localstorage for fname, lname, email
        var fnameInput = document.getElementById('fname');
        var lnameInput = document.getElementById('lname');
        var emailInput = document.getElementById('email');
        var privacyCheckbox = document.getElementById('privacyPolicyAgreement');

        if (localStorage || sessionStorage) {
            // store the value of key in web storage
            sessionStorage.setItem('fname', fnameInput.value);
            sessionStorage.setItem('lname', lnameInput.value);
            sessionStorage.setItem('email', emailInput.value);
            localStorage.setItem('privacyPolicyAgreement', privacyCheckbox.checked);
            //if (user.browser.family === 'Safari') 
        }
        // disable submit button to prevent erroneous duplicated submits
        $("#jira-fdbck-submit").prop("disabled", true);

        // 2018-02-22, RPS: FRONT-245 --> for prototyping needs simply overloading environmentProps
        // to include Optional User Background info as well, until such time that JIRA config can be
        // updated to accept the new data items as custom JIRA fields for HELP project
        var environmentProps = {
            "Location": window.location.href,
            "User-Agent": navigator.userAgent,
            "Referrer": document.referrer,
            "Screen Resolution": screen.width + " x " + screen.height
        };

        var environmentStr = JSON.stringify(environmentProps, null, 4);
        // console.log(LOG_PREFIX + environmentStr);

        if (problemWithReqdField() || maxFilesExceeded() || surpassFileSizeLimit()) {
            // if we have a problem with any of the required fields or if
            // file handling limits exceeded then abort form submission

            // have to reenable submit button to allow user to submit after correcting issues
            $("#jira-fdbck-submit").prop("disabled", false);
            return false;
        } else {
            // collect/generate the submission data
            var userBackgroundInstType = "Not Provided/Declined to State";
            var userBackgroundInstTypeRaw = $("#instype").val();
            if (userBackgroundInstTypeRaw) {
                if (userBackgroundInstTypeRaw.length > 1) {
                    userBackgroundInstType = userBackgroundInstTypeRaw;
                }
            }

            var userBackgroundRole = "Not Provided/Declined to State";
            var userBackgroundRoleRaw = $("#role").val();
            if (userBackgroundRoleRaw) {
                if (userBackgroundRoleRaw.length > 1) {
                    userBackgroundRole = userBackgroundRoleRaw;
                }
            }

            var userBackgroundResearchInterest = "Not Provided/Declined to State";
            var userBackgroundResearchInterestRaw = $("#rsrch-intrst").val();
            if (userBackgroundResearchInterestRaw) {
                if (userBackgroundResearchInterestRaw.length > 1) {
                    userBackgroundResearchInterest = userBackgroundResearchInterestRaw;
                }
            }

            var formData =
                {
                    "fields": {
                        "project": {
                            "key": $("#projectkey").val()
                        },
                        "summary": $("#subject").val(),
                        "description": $("#description").val(),
                        "issuetype": {
                            "name": $("#issuetype").val()
                        },
                        "environment": environmentStr,
                        //accommodate custom fields whose IDs correspond to identifiers generated by JIRA and used in REST call processing
                        "customfield_10333": $("#fname").val(),
                        "customfield_10327": $("#lname").val(),
                        "customfield_10322": $("#email").val(),
                        "customfield_11116": {"value": userBackgroundInstType},
                        "customfield_11117": {"value": userBackgroundRole},
                        "customfield_11118": {"value": userBackgroundResearchInterest}
                    }
                };

            console.log(JSON.stringify(formData, null, 2))

            var restPayload = JSON.stringify(formData);

            function doneFxn(jsonRtrn) {
                var issueKey = jsonRtrn.key;
                // console.log(LOG_PREFIX + " feedback submission completed.");
                // console.log(LOG_PREFIX + "issueKey is: " + issueKey);

                if (document.getElementById("file").files.length == 0) {
                    // console.log(LOG_PREFIX + "no files selected");
                } else {
                    // console.log(LOG_PREFIX + "File(s) were selected for upload.");
                    handleFileAttach(issueKey);
                }

                $("#jira-fdbck-modal").modal("hide");
                $("#jira-cnfrm-modal").modal("show");
                setTimeout(function () {
                    $("#jira-cnfrm-modal").modal("hide");
                }, 5000);
            }

            function errorFxn(jqXHRrtrn, textStatus, errorThrown) {
                // function invoked whenever server-side processing of feedback request is unsuccessful

                console.error(LOG_PREFIX + " feedback submission failed");
                var errorDisplStr = "";

                if (jqXHRrtrn.responseText && jqXHRrtrn.responseText.length > 1) {
                    errorDisplStr = ": ";
                    var errorObj = JSON.parse(jqXHRrtrn.responseText);
                    console.error(LOG_PREFIX + " error on submit! " + JSON.stringify(errorObj.errors));
                }

                var errorRelayContent = "<p>Summary: " + formData.fields.summary +
                    "</p><p>Description: " + formData.fields.description +
                    "</p><p>First Name: " + formData.fields.customfield_10333 +
                    "</p><p>Last Name: " + formData.fields.customfield_10327 +
                    "</p><p>Email: " + formData.fields.customfield_10322 +
                    "</p><p>Environment: " + formData.fields.environment;

                $("#relaycontent").empty().append(errorRelayContent);
                $("#jira-fdbck-modal").modal("hide");
                $("#jira-nosrvr-modal").modal("show");

            }

            // process the form
            //console.info("process the form");
            $.ajax({
                type: "POST",
                url: JIRA_PROXY + "jiraproxyissue",
                data: restPayload,
                dataType: "json",
                contentType: "application/json", //this needs to be set to avoid CORS blocking,
                success: doneFxn,
                error: errorFxn
            });
        }

    });

    // clears prompts for missing fields when fields are populated
    $(document).on("change", ".reqd", function (event) {
        var elemId = $(this).attr("id");
        if ($(this).val().length > 1) {
            $("#" + elemId + "-group").removeClass("has-error");
            $("#" + elemId + "-group .help-block").remove();
        }
    });

    $(document).on("change", "#file", function (event) {
        if (parseInt($(this).get(0).files.length) > MAX_UPLOADS) {
            $("#file-group").addClass("has-error").append("<div class=\"help-block\">Can only upload a maximum of " + MAX_UPLOADS + " files.</div>");
        } else {
            $("#file-group").removeClass("has-error");
            $("#file-group .help-block").remove();
        }

    });
});

////////////////////////////////////////////////////////////////////////////
// helper functions
////////////////////////////////////////////////////////////////////////////

    /**
     * File attached input handler
     * @param issueKey
     */
    function handleFileAttach(issueKey) {
        var form = document.forms.namedItem("feedbackfrm");
        var oData = new FormData(form);

        var url = JIRA_PROXY + "jiraproxyattchmnt";

        var oReq = new XMLHttpRequest();
        oReq.open("POST", url, true);
        oReq.onload = function (oEvent) {
            if (oReq.status == 200) {
                // console.log(LOG_PREFIX + "File(s) successfully uploaded.");
            } else {
                console.error(LOG_PREFIX + "Error: " + oReq.status + " occurred when trying to upload file(s).");
            }
        };
        oData.append("issue", issueKey);
        oReq.send(oData);
    }

    /**
     * Check if host is within *.rcsb.org domain space
     * @param host
     * @returns {boolean}
     */
    function hostIsRcsbDomain(host) {

        var rcsbHostRegex = /\.rcsb\.org$/;
        if (!rcsbHostRegex.test(host)) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Check if host is a dev server
     * @param host
     * @returns {boolean}
     */
    function hostIsDev(host) {
        var reDev = /(127.0.0.1)|(128.6.244.)|(132.249.213.)|(132.249.210.)|(localhost)|(beta)|(dev)|(documentation)|(staging)|(test)|(testing)|(release)/;

        // for each item, check for matches
        var m = host.match(reDev);
        if (m) {
            // console.log(LOG_PREFIX + "Regex MATCHED [" + m[1] + "] with [" + host + "]");
            return true;
        }
        // no matches - return false
        return false;
    }

    /**
     * Regex to check if email input meets basic requirements
     * @param email
     * @returns {boolean}
     */
    function validateEmail(email) {
        var email_regex = /^[a-zA-Z0-9._\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,5}$/;
        if (email_regex.test(email)) {
            return true;
        }
        return false;
    }

    /**
     * Function to clear UI display of error displays
     */
    function clearValidationPrompts() {
        $(".has-error").removeClass("has-error");
        $(".help-block").remove();
    }

    /**
     * Simple rate limiter function (number of uploads)
     * @returns {boolean}
     */
    function maxFilesExceeded() {
        var $fileUpload = $("#file");
        if (parseInt($fileUpload.get(0).files.length) > MAX_UPLOADS) {
            $("#file-group").addClass("has-error").append("<div class=\"help-block\">Can only upload a maximum of " + MAX_UPLOADS + " files.</div>");
            return true;
        }
        return false;
    }

    /**
     * Simple rate limiter function (size of uploads)
     * @returns {boolean}
     */
    function surpassFileSizeLimit() {
        var fileInput = document.getElementById("file");
        var bTooBig = false;
        var fileNameArr = [];

        if (fileInput.files.length > 0) {
            for (var x = 0; x < fileInput.files.length; x++) {
                var thisFile = fileInput.files[x];
                // console.log(LOG_PREFIX + "Current file: " + thisFile.name + " is " + thisFile.size + " bytes in size");
                if (thisFile.size > MAX_FILE_SIZE_BYTES) {
                    fileNameArr.push(thisFile.name);
                    bTooBig = true;
                }
            }
        }
        if (bTooBig) {
            $("#file-group").addClass("has-error").append("<div class=\"help-block\">Problem with file(s): " + fileNameArr.join(", ") + ". Individual file size cannot exceed " + MAX_FILE_SIZE_BYTES / 1000000 + " MB.</div>");
        }
        return bTooBig;
    }

    /**
     * Data input checker
     * @returns {boolean}
     */
    function problemWithReqdField() {
        var bWeHaveAProblem = false;

        // clear prior validatiom prompts
        clearValidationPrompts();

        $(".reqd").each(function () {
            var elemId = $(this).attr("id");

            if ($(this).val().length < 1) {
                bWeHaveAProblem = true;
                // add the actual error message under our input
                $("#" + elemId + "-group").addClass("has-error").append("<div class=\"help-block\">Required value missing</div>");
            } else {
                // if current field is email we need to verify proper email format if there is a value
                if (elemId === "email") {
                    var bValidEmail = validateEmail($("#" + elemId).val());
                    if (!bValidEmail) {
                        bWeHaveAProblem = true;
                        // add the actual error message under our input
                        $("#" + elemId + "-group").addClass("has-error").append("<div class=\"help-block\">Not valid email format</div>");
                    }
                }
            }
        });

        return bWeHaveAProblem;
    }
