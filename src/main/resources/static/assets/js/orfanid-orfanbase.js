$(document).ready(function() {
    $.ajax({
        type: "POST",
        contentType: 'application/json',
        dataType: "text",
        url: "/orfanbase-genes",
        success: function (results) {
            var genes = JSON.parse(results);
            // genes.forEach(function (result) {
            //     // Converting UNIX timestamp to date
            //     var timestamp = result.analysisDate;
            //     result.analysisDate = unixTimestampToDate(timestamp/1000);
            // });
            var table = $('#GenesViewTable').DataTable({
                "data":genes,
                "columns": [
                    {"data" : "analysisDate"},
                    {"data" : "organism"},
                    {"data" : "geneId"},
                    {"data" : "description"},
                    {"data" : "orfanLevel"},
                    {"data" : "analysisId"},
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
                dom: 'frtlip',
                "aaSorting": [],
                "columnDefs": [
                    {
                    "targets": 5,
                    "visible": false
                    },
                    {
                    "targets": -1,
                    "data": null,
                        "orderable": false,
                    "defaultContent": "<button class=\"btn btn-small\"><i class=\"large material-icons\">insert_chart</i></button>"
                } ]
            });

            $('#GenesViewTable tbody').on( 'click', 'button', function () {
                var data = table.row( $(this).parents('tr') ).data();
                var sessionid = data.analysisId;
                window.location.href = "/result?sessionid=" + sessionid;
            });
        },
        error: function (error) {
            console.log("Error occurred while fetching results: "+ error);
            window.location.href = "/results";
        }
    });
});