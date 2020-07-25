$(document).ready(
	function() {

		var orfanLevels;
		var numberOfOrphanGenes;
		var selectedBlastResults;
		var blastTable;

		var userid = $('#username2').val();
		console.log("UserID: "+userid);

		$.getJSON(document.location.origin+'/users/'+ userid +'/ORFanGenesSummaryChart.json',
			function(json) {
				orfanLevels = json.x;
				numberOfOrphanGenes = json.y;

				var data = [ {
					x : orfanLevels,
					y : numberOfOrphanGenes,
					type : 'bar',
					marker : {
						color : '#ef6c00'
					}
				} ];
				var layout = {
					yaxis: {
					title: 'Number of Orphan Genes'
				}};
				Plotly.newPlot('genesummary', data, layout);
			}
		);
		$('#ORFanGenes').DataTable( {
			"ajax": document.location.origin+'/users/'+ userid +'/ORFanGenes.json',
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
			dom: 'frtlipB',
			buttons: [['csv', 'print']],
		});
		$('#ORFanGenesSummary').DataTable( {
			"ajax": document.location.origin+'/users/'+ userid +'/ORFanGenesSummary.json',
			"oLanguage": {
				"sStripClasses": "",
				"sSearch": "",
				"sSearchPlaceholder": "Enter Search Term Here"
			},
			dom: 'frt'
		});

		blastTable = $('#blastresults').DataTable( {
			"columnDefs": [
				{
					"targets": [ 1 ],
					"visible": true,
					"searchable": true
				}],
				"ajax": document.location.origin+'/users/'+ userid +'/blastresults.json',
				"bDestroy": true,
				"oLanguage": {
					"sStripClasses": "",
					"sSearch": "",
					"sSearchPlaceholder": "Enter Search Term Here",
					"sInfo": "Showing _START_ -_END_ of _TOTAL_ blast results",
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
				dom: 'frtlipB',
				buttons: [['csv', 'print']]
			});

			// add materialize CSS to print buttons
			$('.buttons-csv').addClass('waves-effect waves-light btn');
			$('.buttons-print').addClass('waves-effect waves-light btn');

			$('#ORFanGenes').on('click', 'td', function() {
                // find the row of the table
                var geneid = $(this).closest('tr').find("td:first").html();
								console.log(geneid);
								// get selected orfan gene id
								$('#selectedgeneid').html(geneid);
								// filter the blast results based on the gene id selected by the user
									$('#blastresults').dataTable().fnFilter(geneid);
								//
								// // blastTable = $('#blastresults').dataTable();
								// $('#blastresults').append(filteredBlastResults(geneid));
								// // blastTable.fnAddData(filteredBlastResults(geneid));
								// // blastTable.fnDraw();
			});

			// function filteredBlastResults(geneid){
			//  $.getJSON( 'users/'+ userid +'/blastresults.json', function( data ) {
			// 		selectedBlastResults = $.grep(data.data, function(element) {
			// 			console.log( element[1]+" == "+geneid);
			// 			return element[1] == geneid;
			// 		});
			// 		console.log("selectedBlastResults: "+  selectedBlastResults);
			// 	});
			//
			// 	return selectedBlastResults;
			// }

		});

