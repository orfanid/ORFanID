$(document).ready(function() {
    $('#clampresults').DataTable( {
        "ajax": 'assets/summary.json',
        "oLanguage": {
            "sStripClasses": "",
            "sSearch": "",
            "sSearchPlaceholder": "Enter Search Term Here",
            "sInfo": "Showing _START_ -_END_ of _TOTAL_ chromosomes",
            "sLengthMenu": '<span>Rows per page:</span>' +
            '<select class="browser-default">' +
            '<option value="5">5</option>' +
            '<option value="10">10</option>' +
            '<option value="20">20</option>' +
            '<option value="50">50</option>' +
            '<option value="-1">All</option>' +
            '</select></div>'
        },
        //  adding download link with a file_download icon at the end of the table
        "columnDefs": [{
            "targets": -1, // add to last column
            "render": function ( data, type, row, meta ) {
                var chromosomeID = row[0]; // get the chomosome number
                return '<a href="assets/data/chromosome' + chromosomeID + '.fasta">' + // fasta file path
                    "<i class='material-icons left'>file_download</i>" + // add a download icon
                    '</a>'; // end of anchor tag
            }
        }],
        dom: 'frtlipB', // order of the component in the datatable
        buttons: [
            ['csv', 'print']
        ],
    } ); // end of data table initialization

    // add same materialize CSS appearence to print and CSV buttons
    $('.buttons-csv').addClass('waves-effect waves-light btn');
    $('.buttons-print').addClass('waves-effect waves-light btn');
} );