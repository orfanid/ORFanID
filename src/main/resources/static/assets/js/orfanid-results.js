$(document).ready(function() {
    $.ajax({
        type: "POST",
        contentType: 'application/json',
        dataType: "text",
        url: "/all-analysis",
        success: function (results) {
            var savedResults = JSON.parse(results);
            var table = $('#ResultViewTable').DataTable({
                "data":savedResults,
                "columns": [
                    {"data" : "analysisDate"},
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
                    "sLengthMenu": '<span>Rows per page:</span><select class="browser-default">' +
                    '<option value="5">5</option>' +
                    '<option value="10">10</option>' +
                    '<option value="20">20</option>' +
                    '<option value="30">30</option>' +
                    '<option value="40">40</option>' +
                    '<option value="50">50</option>' +
                    '<option value="-1">All</option>' +
                    '</select></div>'
                },
                dom: 'frtlip',
                "aaSorting": [],
                "columnDefs": [ {
                    "targets": -1,
                    "data": null,
                    "orderable": false,
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