$(document).ready(function() {
    function unixTimestampToDate(UNIX_timestamp){
        var a = new Date(UNIX_timestamp * 1000);
        var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
        var year = a.getFullYear();
        var month = months[a.getMonth()];
        var date = a.getDate();
        var time = date + ' ' + month + ' ' + year;
        return time;
    }

    $.ajax({
        type: "POST",
        contentType: 'application/json',
        dataType: "text",
        url: "/all-analysis",
        success: function (results) {
            var savedResults = JSON.parse(results);
            savedResults.forEach(function (result) {
                // Converting UNIX timestamp to date
                var timestamp = result.analysisDate;
                result.date = unixTimestampToDate(timestamp/1000);
            });
            var table = $('#ResultViewTable').DataTable({
                "data":savedResults,
                "columns": [
                    {"data" : "date"},
                    {"data" : "analysisId"},
                    {"data" : "email"},
                    {"data" : "organism"},
                    {"data" : "numberOfGenes"},
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
                var sessionid = data.analysisId;
                window.location.href = "/result?sessionid=" + sessionid;
            });
        },
        error: function (error) {
            console.log("Error occurred while fetching results: "+ error);
        }
    });
});