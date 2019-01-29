$(document).ready(function() {
    $.ajax({
        type: "POST",
        contentType: 'application/json',
        dataType: "text",
        url: "/results",
        success: function (results) {
            var savedResults = JSON.parse(results);
            savedResults.forEach(function (result) {
                // Converting UNIX timestamp to date
                var timestamp = result.date;
                var savedDate = new Date(timestamp * 1000);
                var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
                var year = savedDate.getFullYear();
                var month = months[savedDate.getMonth()];
                var date = savedDate.getDate();
                var time = date + ' ' + month + ' ' + year;
                result.date = time;
            });
            var table = $('#ResultViewTable').DataTable({
                "data":savedResults,
                "columns": [
                    {"data" : "date"},
                    {"data" : "sessionid"},
                    {"data" : "email"},
                    {"data" : "organism"},
                    {"data" : "view"}
                ],
                "oLanguage": {
                    "sStripClasses": "",
                    "sSearch": "",
                    "sSearchPlaceholder": "Enter Search Term Here",
                    "sLengthMenu": '<span>Rows per page:</span>'+
                        '<select class="browser-default">' +
                        '<option value="5">5</option>' +
                        '<option value="10">10</option>' +
                        '<option value="20">20</option>' +
                        '<option value="50">50</option>' +
                        '<option value="100">100</option>' +
                        '<option value="-1">All</option>' +
                        '</select></div>'
                },
                dom: 'frt',
                "aaSorting": [],
                "columnDefs": [ {
                    "targets": -1,
                    "data": null,
                    "defaultContent": "<button class=\"btn btn-small\"><i class=\"large material-icons\">insert_chart</i></button>"
                } ]
            });

            $('#ResultViewTable tbody').on( 'click', 'button', function () {
                var data = table.row( $(this).parents('tr') ).data();
                var sessionid = data.sessionid;
                window.location.href = "/result?sessionid=" + sessionid;
            });
        },
        error: function (error) {
            console.log("Error occurred while fetching results: "+ error);
        }
    });
});