$(document).ready(function() {
    $('.modal').modal();
    var urlParams = new URLSearchParams(window.location.search);
    var sessionid = urlParams.get("sessionid");

    var orfanLevels;
    var numberOfOrphanGenes;

    // Getting ORFanGenes Summary
    $.ajax({
        type: "POST",
        contentType: 'application/json',
        dataType: "text",
        url: "/data/summary",
        data: '{"sessionid":"' + sessionid + '"}',
        success: function (result) {
            var orfanGenesSummary = JSON.parse(result);
            $('#orfanGenesSummary').DataTable({
                "data":orfanGenesSummary,
                "columns": [
                    {"data" : "type"},
                    {"data" : "count"}
                ],
                "oLanguage": {
                    "sStripClasses": "",
                    "sSearch": "",
                    "sSearchPlaceholder": "Enter Search Term Here"
                },
                dom: 'frt',
                "aaSorting": []
            });
        },
        error: function (error) {
            console.log("error: "+ error);
        }
    });

    // Getting ORFanGenes Summary Chart
    $.ajax({
        type: "POST",
        contentType: 'application/json',
        dataType: "text",
        url: "/data/summary/chart",
        data: '{"sessionid":"' + sessionid + '"}',
        success: function (result) {
            var orfanGenesSummaryChart = JSON.parse(result);
            orfanLevels = orfanGenesSummaryChart.x;
            numberOfOrphanGenes = orfanGenesSummaryChart.y;

            var data = [{
                x : orfanLevels,
                y : numberOfOrphanGenes,
                type : 'bar',
                marker : {
                    color : '#ef6c00'
                }
            }];
            var layout = {
                yaxis: {
                    title: 'Number of Orphan Genes'
                }};
            Plotly.newPlot('genesummary', data, layout);
        },
        error: function (error) {
            console.log("Error occured in ORFanGenes Summary Chart: "+ error);
        }
    });

    //Getting ORFan Genes
    $.ajax({
        type: "POST",
        contentType: 'application/json',
        dataType: "text",
        url: "/data/genes",
        data: '{"sessionid":"' + sessionid + '"}',
        success: function (result) {
            var orfanGenes = JSON.parse(result);
            var table = $('#orfanGenes').DataTable({
                "data":orfanGenes,
                "columns": [
                    {"data" : "geneid",
                        "render": function(data, type, row, meta){
                            if(type === 'display'){
                                data = '<a href=https://www.ncbi.nlm.nih.gov/search/all/?term="' + data + '">' + data + '</a>';
                            }
                            return data;
                        }
                    },
                    {"data" : "description"},
                    {"data" : "orfanLevel"},
                    {"data" : "homologyEvidence"}
                ],
                "oLanguage": {
                    "sStripClasses": "",
                    "sSearch": "",
                    "sSearchPlaceholder": "Enter Search Term Here",
                    "sInfo": "Showing _START_ -_END_ of _TOTAL_ genes",
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
                    "defaultContent": "<a class=\"waves-effect waves-light btn modal-trigger\" href=\"#blastResultModal\"><i class=\"large material-icons\">insert_chart</i></a>"
                } ]
            });

            $('#orfanGenes tbody').on( 'click', 'a', function () {
                var data = table.row( $(this).parents('tr') ).data();
                console.log(data["geneid"]);
                //Getting BLAST Results
                $.ajax({
                    type: "POST",
                    contentType: 'application/json',
                    dataType: "text",
                    url: "/data/blast",
                    data: '{"sessionid":"' + sessionid + '", "id" : "' + data["geneid"] + '"}',
                    success: function (result) {
                        var blastResult;
                        try {
                            blastResult = JSON.parse(result);
                        }
                        catch(err) {
                            console.error("blastResult passing error")
                        }
                        console.log(blastResult);
                        var treeData = blastResult["tree"];
                        console.log(treeData);

                        var myChart = echarts.init(document.getElementById('blastResults'));
                        myChart.showLoading();
                        myChart.hideLoading();
                        myChart.setOption(option = {
                            tooltip: {
                                trigger: 'item',
                                triggerOn: 'mousemove'
                            },
                            series: [
                                {
                                    type: 'tree',

                                    data: [treeData],

                                    top: '1%',
                                    left: '7%',
                                    bottom: '1%',
                                    right: '20%',

                                    symbolSize: 7,
                                    initialTreeDepth: 9,

                                    label: {
                                        normal: {
                                            position: 'left',
                                            verticalAlign: 'middle',
                                            align: 'right',
                                            fontSize: 9
                                        }
                                    },

                                    leaves: {
                                        label: {
                                            normal: {
                                                position: 'right',
                                                verticalAlign: 'middle',
                                                align: 'left'
                                            }
                                        }
                                    },

                                    expandAndCollapse: true,
                                    animationDuration: 550,
                                    animationDurationUpdate: 750
                                }
                            ]
                        });

                        $('#blastDescription').html(blastResult["description"]);
                        // $('#blastResultModal').modal();
                    },
                    error: function (error) {
                        console.log("error occurred in fetching blast results  for " + data["geneid"] + " : " + error.toString());
                    }
                });
            });
        }
    });
});



function saveResult() {
    var urlParams = new URLSearchParams(window.location.search);
    var sessionid = (urlParams.get('sessionid'));
    var email = document.getElementById("email").value;
    var data = {
        "sessionid" : sessionid,
        "email" : email
    };

    $.ajax({
        type: "POST",
        contentType: 'application/json',
        dataType: "text",
        url: "/save",
        data: JSON.stringify(data),
        success: function (result) {
            // TODO show success message
        },
        error: function (error) {
            // TODO show error message
        }
    });
}

function downloadBlast() {
    let urlParams = new URLSearchParams(window.location.search);
    let sessionid = (urlParams.get('sessionid'));

    $.ajax({
        type: "GET",
        url: "/download/blast/" + sessionid,
        success: function () {
            window.location = "/download/blast/" + sessionid;
        }
    });
}